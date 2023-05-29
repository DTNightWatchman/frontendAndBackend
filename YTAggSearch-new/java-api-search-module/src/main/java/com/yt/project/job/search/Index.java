package com.yt.project.job.search;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yt.project.common.RedisCommon;
import com.yt.project.model.entity.DocInfo;
import com.yt.project.model.entity.Weight;
import com.yt.project.service.DocInfoService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@Data
public class Index {

    private static final String INDEX_PATH = "E:\\github\\frontendAndBackend\\YTAggSearch-new\\java-api-search-module\\indexpath\\";

    private ArrayList<DocInfo> forwardIndex = new ArrayList<>();

    @Resource
    private RedissonClient redissonClient;

    private RMap<String,  List<Weight>> invertedIndex = null;


    @PostConstruct
    private void initIndex() {
        invertedIndex = redissonClient.getMap(RedisCommon.invertedIndex);
    }


    @Resource
    private RedisTemplate<String, Object> redisTemplate;



    /**
     * 从正派索引中获取文档信息
     * @param docId
     * @return
     */
    public DocInfo getDocInfo(int docId) {
        return forwardIndex.get(docId);
    }

    public List<Weight> getInverted(String term) {
        return this.invertedIndex.get(term);
    }

    @Transactional
    public void addDoc(String title, String url, String content) {
        DocInfo docInfo = buildForward(title, url, content);
        if (docInfo == null) {
            return;
        }
        buildInverted(docInfo);
    }

    private void buildInverted(DocInfo docInfo) {
        class WordContent {
            public int titleCount;

            public int contentCount;
        }

        HashMap<String, WordContent> wordContentHashMap = new HashMap<>();

        // 对标题进行分词
        List<Term> terms = ToAnalysis.parse(docInfo.getTitle()).getTerms();
        for (Term term : terms) {
            String word = term.getName();
            WordContent wordContent = wordContentHashMap.get(word);
            if (wordContent == null) {
                WordContent newWordContent = new WordContent();
                newWordContent.titleCount = 1;
                newWordContent.contentCount = 0;
                wordContentHashMap.put(word, newWordContent);
            } else {
                wordContent.titleCount += 1;
            }
        }

        // 针对正文进行分词
        terms = ToAnalysis.parse(docInfo.getContent()).getTerms();
        for (Term term : terms) {
            String word = term.getName();
            WordContent wordContent = wordContentHashMap.get(word);
            if (wordContent == null) {
                WordContent newWordContent = new WordContent();
                newWordContent.titleCount = 0;
                newWordContent.contentCount = 1;
                wordContentHashMap.put(word, newWordContent);
            } else {
                wordContent.contentCount += 1;
            }
        }

        // 汇总结果
        wordContentHashMap.forEach((key, value) -> {
            Weight weight = new Weight();
            weight.setDocId(docInfo.getId());
            weight.setWeight(value.titleCount * 10 + value.contentCount);

            // 使用的是通过redis获取的一个map，不需要加锁，本身就是一个线程安全的map
            List<Weight> list = invertedIndex.get(key);
            if (list == null) {
                ArrayList<Weight> arrayList = new ArrayList<>();
                arrayList.add(weight);
                invertedIndex.put(key, arrayList);
            } else {
                list.add(weight);
            }

        });
    }

    @Autowired
    private DocInfoService docInfoService;

    @Transactional
    public DocInfo buildForward(String title, String url, String content) {
        DocInfo docInfo = new DocInfo();
        docInfo.setTitle(title);
        docInfo.setUrl(url);
        docInfo.setContent(content);
        synchronized (this.forwardIndex) {
            boolean save = docInfoService.save(docInfo);
            //System.err.println(save);
            if (!save) {
                log.error("没有保存成功：" + docInfo);
                return null;
            }
        }
        return docInfo;
    }
    private Gson gson = new Gson();

    public void save() {
        long start = System.currentTimeMillis();
        log.info("开始保存索引");
        File indexPathFile = new File(INDEX_PATH);
        if (!indexPathFile.exists()) {
            indexPathFile.mkdirs();
        }
        File forwardIndexFile = new File(INDEX_PATH + "forward.txt");
        File invertedIndexFile = new File(INDEX_PATH + "inverted.txt");
        try(Writer forwardIndexWriter = new FileWriter(forwardIndexFile);
            Writer invertedIndexWriter = new FileWriter(invertedIndexFile)) {
            String forwardIndexJson = gson.toJson(this.forwardIndex);
            String invertedIndexJson = gson.toJson(this.invertedIndex);
            forwardIndexWriter.write(forwardIndexJson);
            invertedIndexWriter.write(invertedIndexJson);
            long end = System.currentTimeMillis();
            log.info("加载索引结束，耗时:" + (end - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        log.info("开始加载索引");
        long start = System.currentTimeMillis();
        File forwardIndexFile = new File(INDEX_PATH + "forward.txt");
        File invertedIndexFile = new File(INDEX_PATH + "inverted.txt");
        try (Reader forwardIndexReader = new FileReader(forwardIndexFile);
            Reader invertedIndexReader = new FileReader(invertedIndexFile);
            BufferedReader forwardIndexBufferedReader = new BufferedReader(forwardIndexReader);
            BufferedReader invertedIndexBufferedReader = new BufferedReader(invertedIndexReader)) {
            String forwardIndexJson = forwardIndexBufferedReader.readLine();
            String invertedIndexJson = invertedIndexBufferedReader.readLine();
            this.forwardIndex = gson.fromJson(forwardIndexJson, new TypeToken<List<DocInfo>>(){}.getType());
            HashMap<String, List<Weight>> tmpHashMap = gson.fromJson(invertedIndexJson, new TypeToken<HashMap<String, List<Weight>>>(){}.getType());
            for (Map.Entry<String, List<Weight>> stringListEntry : tmpHashMap.entrySet()) {
                this.invertedIndex.put(stringListEntry.getKey(), stringListEntry.getValue());
            }
            long end = System.currentTimeMillis();
            log.info("加载索引结束，耗时:" + (end - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
