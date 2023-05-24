package com.yt.project.job.once.search;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yt.project.model.entity.DocInfo;
import com.yt.project.model.entity.Weight;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import javax.print.Doc;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
public class Index {

    private static final String INDEX_PATH = "E:\\github\\frontendAndBackend\\YTAggSearch-new\\java-api-search-module\\indexpath\\";

    private ArrayList<DocInfo> forwardIndex = new ArrayList<>();

    private HashMap<String, ArrayList<Weight>> invertedIndex = new HashMap<>();

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

    public void addDoc(String title, String url, String content) {
        DocInfo docInfo = buildForward(title, url, content);
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

            synchronized (invertedIndex) {
                // 加锁
                List<Weight> list = invertedIndex.get(key);
                if (list == null) {
                    ArrayList<Weight> arrayList = new ArrayList<>();
                    arrayList.add(weight);
                    invertedIndex.put(key, arrayList);
                } else {
                    list.add(weight);
                }
            }

        });
    }

    public DocInfo buildForward(String title, String url, String content) {
        DocInfo docInfo = new DocInfo();
        docInfo.setTitle(title);
        docInfo.setUrl(url);
        docInfo.setContent(content);
        synchronized (forwardIndex) {
            docInfo.setId(this.forwardIndex.size());
            forwardIndex.add(docInfo);
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
            log.info("加载索引结束，耗时：" + (end - start) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        log.info("开始加载模块");
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
            //this.invertedIndex = gson.fromJson(invertedIndexJson, ne )
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
