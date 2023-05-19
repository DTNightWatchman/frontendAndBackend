package com.yt.project.job.once.search;

import com.yt.project.model.entity.DocInfo;
import com.yt.project.model.entity.Weight;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Index {

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
}
