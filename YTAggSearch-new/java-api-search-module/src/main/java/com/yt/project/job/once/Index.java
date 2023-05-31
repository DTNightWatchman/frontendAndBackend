package com.yt.project.job.once;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yt.project.model.entity.DocInfo;
import com.yt.project.model.entity.Weight;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 索引类
 */
@Data
@Slf4j
//@Component
public class Index {

    private ArrayList<DocInfo> forwardIndex = new ArrayList<>();

    private HashMap<String, ArrayList<Weight>> invertedIndex = new HashMap<>();

    // 查询文档信息
    public DocInfo getDocInfo(int docId) {
        return this.forwardIndex.get(docId);
    }

    // 根据词语查找倒排索引
    public List<Weight> getInverted(String term) {
        return this.invertedIndex.get(term);
    }

    // 向索引中添加一个文档
    public void addDoc(String title, String url, String content) {
        DocInfo docInfo = buildForward(title, url, content);
        buildInverted(docInfo);
    }

    /**
     * 构建倒排索引
     * @param docInfo
     */
    private void buildInverted(DocInfo docInfo) {
        class WordCount {
            public int titleCount;
            public int contentCount;
        }
        HashMap<String, WordCount> wordCountHashMap = new HashMap<>();
        // 对标题进行分词
        List<Term> terms = ToAnalysis.parse(docInfo.getTitle()).getTerms();
        // 遍历
        for (Term term : terms) {
            String word = term.getName();
            WordCount wordCount = wordCountHashMap.get(word);
            if (wordCount == null) {
                wordCount = new WordCount();
                wordCount.titleCount = 1;
                wordCount.contentCount = 0;
                wordCountHashMap.put(word, wordCount);
            } else {
                wordCount.titleCount += 1;
            }
        }
        // 对正文进行分词
        terms = ToAnalysis.parse(docInfo.getContent()).getTerms();
        for (Term term : terms) {
            String word = term.getName();
            WordCount wordCount = wordCountHashMap.get(word);
            if (wordCount == null) {
                wordCount = new WordCount();
                wordCount.titleCount = 0;
                wordCount.contentCount = 1;
                wordCountHashMap.put(word, wordCount);
            } else {
                wordCount.contentCount += 1;
            }
        }
        // 汇总结果
        wordCountHashMap.forEach((key, value) -> {
            Weight weight = new Weight();
            weight.setDocId(docInfo.getId());
            weight.setWeight(value.titleCount * 10 + value.contentCount);
            synchronized (this.invertedIndex) {
                ArrayList<Weight> list = invertedIndex.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                    list.add(weight);
                    invertedIndex.put(key, list);
                } else {
                    list.add(weight);
                }
            }
        });
    }

    /**
     * 构建正排索引
     * @param title
     * @param url
     * @param content
     * @return
     */
    private DocInfo buildForward(String title, String url, String content) {
        DocInfo docInfo = new DocInfo();
        docInfo.setTitle(title);
        docInfo.setUrl(url);
        docInfo.setContent(content);
        synchronized (this.forwardIndex) {
            docInfo.setId(this.forwardIndex.size());
            forwardIndex.add(docInfo);
        }
        return docInfo;
    }

    // save and load
    private static final String INDEX_PATH = "E:\\github\\frontendAndBackend\\YTAggSearch-new\\java-api-search-module\\indexpath\\";
    private ObjectMapper objectMapper = new ObjectMapper();

    public void save() {
        long t1 = System.currentTimeMillis();
        log.info("开始保存索引");
        File indexFilePath = new File(INDEX_PATH);
        if (!indexFilePath.exists()) {
            indexFilePath.mkdirs();
        }
        File forwardIndexFile = new File(INDEX_PATH + "forward.txt");
        File invertedIndexFile = new File(INDEX_PATH + "inverted.txt");

        try {
            objectMapper.writeValue(forwardIndexFile, this.forwardIndex);
            objectMapper.writeValue(invertedIndexFile, this.invertedIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        log.info("保存结束：" + (t2 - t1) + "ms");
    }

    @PostConstruct
    public void load() {
        long t1 = System.currentTimeMillis();
        log.info("开始加载索引");
        File forwardIndexFile = new File(INDEX_PATH + "forward.txt");
        File invertedIndexFile = new File(INDEX_PATH + "inverted.txt");
        try {
            this.forwardIndex = objectMapper.readValue(forwardIndexFile, new TypeReference<ArrayList<DocInfo>>() {});
            this.invertedIndex = objectMapper.readValue(invertedIndexFile, new TypeReference<HashMap<String, ArrayList<Weight>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        log.info("加载索引结束：" + (t2 - t1) + "ms");
    }
}
