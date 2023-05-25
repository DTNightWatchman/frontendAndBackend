package com.yt.project.service.impl;

import com.yt.project.job.once.search.Index;
import com.yt.project.model.entity.DocInfo;
import com.yt.project.model.entity.Weight;
import com.yt.project.model.vo.DocInfoVO;
import com.yt.project.service.DocInfoService;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocInfoServiceImpl implements DocInfoService {

    private final Set<String> stopWord = new HashSet<>();

    @Resource
    private Index index;

    @PostConstruct
    private void loadStopWord() {
        index.load();
        // load 暂停词
        // 下载
        String stopWordPath = "E:\\github\\frontendAndBackend\\YTAggSearch-new\\java-api-search-module\\stop.txt";
        try (Reader reader = new FileReader(stopWordPath)){
            BufferedReader bufferedReader = new BufferedReader(reader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                stopWord.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据倒排索引搜搜文档
     * @param line
     */
    @Override
    public List<DocInfoVO> searchDocInfo(String line, long current, int size) {
        HashMap<String, ArrayList<Weight>> invertedIndex = index.getInvertedIndex();
        List<Term> oldTerms = ToAnalysis.parse(line).getTerms();
        List<Term> terms = new ArrayList<>();
        // 去掉暂替词
        for (Term term : oldTerms) {
            if (!stopWord.contains(term.getName())) {
                // 如果包含，去除
                terms.add(term);
            }
        }

        List<List<Weight>> allResult = new ArrayList<>();
        for (Term term : terms) {
            ArrayList<Weight> weights = invertedIndex.get(term.getName());
            if (weights != null) {
                allResult.add(weights);
            }
        }
        List<Weight> result = mergeResult(allResult);
        // 将结果根据降序排序
        result.sort((o1, o2) -> o2.getWeight() - o1.getWeight());
        // 根据结果查询倒排索引
        return result.stream().map(weight -> {
            int docId = weight.getDocId();
            DocInfo docInfo = index.getDocInfo(docId);
            DocInfoVO docInfoVO = new DocInfoVO();
            docInfoVO.setTitle(docInfo.getTitle());
            docInfoVO.setUrl(docInfo.getUrl());
            String desc = getDesc(docInfo.getContent(), terms);
            docInfoVO.setDesc(desc);
            return docInfoVO;
        }).collect(Collectors.toList());
    }

    private String getDesc(String content, List<Term> terms) {
        int firstPos = -1;
        for (Term term : terms) {
            String word = term.getName();
            content = content.toLowerCase().replaceAll("\\b" + word + "\\b", " " + word + " ");
            firstPos = content.toLowerCase().indexOf(" " + word + " ");
            if (firstPos > 0) {
                break;
            }
        }
        if (firstPos == -1) {
            return null;
        }
        String desc = null;
        int descBegin = Math.max(firstPos - 60, 0);
        if (firstPos + 160 >= content.length()) {
            desc = content.substring(descBegin);
        } else {
            desc = content.substring(descBegin, firstPos + 160) + "...";
        }
        // 为数据打上标签
        for (Term term : terms) {
            String word = term.getName();
            desc = desc.replaceAll("(?i) " + word + " ", "<em>" + word + "</em>");
        }
        return desc;
    }

    /**
     * 合并结果(使用优先队列)
     * @param allResult
     */
    private List<Weight> mergeResult(List<List<Weight>> allResult) {
        // 定义一个临时类
        class Pos {
            public int row;
            public int col;

            public Pos(int row, int col) {
                this.row = row;
                this.col = col;
            }
        }

        for (List<Weight> curRow : allResult) {
            curRow.sort(Comparator.comparingInt(Weight::getDocId));
        }

        List<Weight> res = new ArrayList<>();
        // 定义优先队列
        PriorityQueue<Pos> queue = new PriorityQueue<>(new Comparator<Pos>() {
            @Override
            public int compare(Pos o1, Pos o2) {
                Weight w1 = allResult.get(o1.row).get(o1.col);
                Weight w2 = allResult.get(o2.row).get(o2.col);
                return w1.getDocId() - w2.getDocId();
            }
        });
        for (int i = 0; i < allResult.size(); i++) {
            queue.offer(new Pos(i, 0));
        }
        while (!queue.isEmpty()) {
            Pos min = queue.poll();
            Weight w = allResult.get(min.row).get(min.col);
            if (res.size() > 0) {
                // 添加合并
                Weight befWeight = res.get(res.size() - 1);
                // 如何docId相同，就合并
                if (befWeight.getDocId() == w.getDocId()) {
                    befWeight.setWeight(befWeight.getWeight() + w.getWeight());
                } else {
                    res.add(w);
                }
            } else {
                res.add(w);
            }
            // 向后移动
            Pos newPos = new Pos(min.row, min.col + 1);
            // 如果达到这一条的结尾，就直接结束这部分，不放入队列中
            if (newPos.col < allResult.get(newPos.row).size()) {
                // 在范围内，放入队列中
                queue.offer(newPos);
            }
        }
        return res;
    }
}