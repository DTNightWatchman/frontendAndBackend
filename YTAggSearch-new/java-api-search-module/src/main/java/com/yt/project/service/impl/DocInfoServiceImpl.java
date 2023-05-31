package com.yt.project.service.impl;

import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yt.common.model.vo.DocInfoVO;
import com.yt.project.common.RedisCommon;
import com.yt.project.job.once.Index;
import com.yt.project.model.entity.DocInfo;
import com.yt.project.model.entity.Weight;
import com.yt.project.service.DocInfoService;
import com.yt.project.mapper.DocInfoMapper;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang3.StringUtils;
import org.nlpcn.commons.lang.util.CollectionUtil;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author lenovo
* @description 针对表【doc_info(存储Java的api文档信息)】的数据库操作Service实现
* @createDate 2023-05-29 17:15:09
*/
@Service
public class DocInfoServiceImpl extends ServiceImpl<DocInfoMapper, DocInfo>
    implements DocInfoService{

    private final Set<String> stopWord = new HashSet<>();
//
//    @Resource
//    private Index index;



    @PostConstruct
    private void loadStopWord() {
        //index.load();
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

    @Resource
    private RedissonClient redissonClient;

    @Override
    public List<DocInfoVO> searchDocInfoFromRedis(String line, long current, int size) {
        if (current < 0) {
            return new ArrayList<>();
        }
        if (StringUtils.isBlank(line)) {
            return new ArrayList<>();
        }
        // 读取redis缓存
        RList<DocInfoVO> docInfoVOSInRedis = redissonClient.getList("ytSearchProject:javaApiDocSearch:" + line);
        if (!CollectionUtils.isEmpty(docInfoVOSInRedis)) {
            // 结果不为空，直接将结果返回，并刷新redis缓存的时间
            if (current >= docInfoVOSInRedis.size()) {
                return new ArrayList<>();
            }
            // 刷新过期时间，返回结果
            docInfoVOSInRedis.expire(100, TimeUnit.SECONDS);
            return docInfoVOSInRedis.subList(Long.valueOf(current).intValue(),
                    Math.min(docInfoVOSInRedis.size(), Long.valueOf(current).intValue() + Long.valueOf(size).intValue()));
        }
        RMap<String, ArrayList<Weight>> invertedIndex = redissonClient.getMap(RedisCommon.invertedIndex);
        List<Term> oldTerms = ToAnalysis.parse(line).getTerms();
        List<Term> terms = new ArrayList<>();
        for (Term term : oldTerms) {
            // 去除暂停词
            if (!stopWord.contains(term.getName())) {
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
        // 将结果根据weight进行降序
        result.sort(((o1, o2) -> o2.getWeight() - o1.getWeight()));
        // 根据结果从数据中排序
        List<Integer> docIdCollect = result.stream().map(Weight::getDocId).collect(Collectors.toList());
        QueryWrapper<DocInfo> queryWrapper = new QueryWrapper<>();
        if (!CollectionUtils.isEmpty(docIdCollect)) {
            String joinStr = StrUtil.join(",", docIdCollect);
            queryWrapper.in("id", docIdCollect).last(" order by field (id," + joinStr + ")");
        }
        List<DocInfo> docInfoList = this.list(queryWrapper);
        List<DocInfoVO> docInfoVOS = docInfoList.stream().map(docInfo -> {
            DocInfoVO docInfoVO = new DocInfoVO();
            docInfoVO.setUrl(docInfo.getUrl());
            docInfoVO.setTitle(docInfo.getTitle());
            String desc = getDesc(docInfo.getContent(), terms);
            docInfoVO.setDesc(desc);
            return docInfoVO;
        }).collect(Collectors.toList());
        // 刷新缓存后将结果返回
        docInfoVOSInRedis.addAll(docInfoVOS);
        docInfoVOSInRedis.expire(100, TimeUnit.SECONDS);
        if (current >= docInfoVOSInRedis.size()) {
            return new ArrayList<>();
        }
        return docInfoVOSInRedis.subList(Long.valueOf(current).intValue(), Math.min(Long.valueOf(current + size).intValue(), docInfoVOSInRedis.size()));
    }

    Index index = new Index();



    /**
     * 根据倒排索引搜搜文档
     * @param line
     */
    @Override
    public List<DocInfoVO> searchDocInfo(String line, long current, int size) {
        HashMap<String, ArrayList<Weight>> invertedIndex = index.getInvertedIndex();
        List<Term> terms = new ArrayList<>();
        List<Term> oldTerms = ToAnalysis.parse(line).getTerms();
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
        PriorityQueue<Pos> queue = new PriorityQueue<>((o1, o2) -> {
            Weight w1 = allResult.get(o1.row).get(o1.col);
            Weight w2 = allResult.get(o2.row).get(o2.col);
            return w1.getDocId() - w2.getDocId();
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




