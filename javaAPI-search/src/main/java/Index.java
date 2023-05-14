import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by YT
 * description: 通过这个类在内存中构造索引结构
 * User:lenovo
 * Data:2022-07-26
 * Time:21:05
 */
public class Index {
    // 数组下标表示docId
    private ArrayList<DocInfo> forwardIndex = new ArrayList<>();

    // hash表来表示倒排索引
    // key 就是词
    // value 就是一组有关文章
    private HashMap<String, ArrayList<Weight>> invertedIndex = new HashMap<>();

    // 1. 给定一个 docId，在正排索引中查询文档信息
    public DocInfo getDocInfo(int docId) {
        return forwardIndex.get(docId);
    }

    // 2. 给定一个 词，在倒排索引中查找有关的 docId，
    public List<Weight> getInverted(String term) {
        return invertedIndex.get(term);
    }

    // 3. 往索引中新增一个文档
    public void addDoc(String title, String url, String content) {
        // 新增文档操作，既需要给正排索引增加信息，也需要给倒排索引添加信息
        DocInfo docInfo =  buildForward(title, url, content);
        buildInveted(docInfo);
    }

    private void buildInveted(DocInfo docInfo) {
        // 词 => 文档id 的映射关系
        // 针对标题 针对正文 进行分词
        // 加入索引
        // 简单通过词出现的次数来决定权重
        class WordCount {
            public int titleCount;
            public int contentCount;
        }

        HashMap<String,WordCount> wordCountHashMap = new HashMap<>();

        // 1. 对标题进行分词
        List<Term> terms = ToAnalysis.parse(docInfo.getTitle()).getTerms();
        // 2. 遍历分词结果，统计每个词出现的个数
        for (Term term: terms) {
            // 先判断term是否存在
            String word = term.getName();
            WordCount wordCount = wordCountHashMap.get(word);
            if (wordCount == null) {
                WordCount newWordCount = new WordCount();
                newWordCount.titleCount = 1;
                newWordCount.contentCount = 0;
                wordCountHashMap.put(word,newWordCount);
            } else{
                wordCount.titleCount+=1;
            }
        }
        // 3. 针对正文进行分词
        terms = ToAnalysis.parse(docInfo.getContent()).getTerms();

        // 4. 遍历分词结果，统计每个词出现的个数
        for (Term term: terms) {
            // 先判断term是否存在
            String word = term.getName();
            WordCount wordCount = wordCountHashMap.get(word);
            if (wordCount == null) {
                WordCount newWordCount = new WordCount();
                newWordCount.contentCount = 1;
                newWordCount.titleCount = 0;
                wordCountHashMap.put(word,newWordCount);
            } else {
                wordCount.contentCount+=1;
            }
        }
        // 5. 汇总结果
        //    权重：标题中出现次数 * 10 + 正文中出现次数 * 1
        // 6. 遍历 hash表，更新表
        for (Map.Entry<String,WordCount> entry: wordCountHashMap.entrySet()) {

            Weight weight = new Weight();
            weight.setDocId(docInfo.getDocId());
            weight.setWeight(entry.getValue().titleCount * 10 + entry.getValue().contentCount);

            synchronized (invertedIndex) {
                List<Weight> list = invertedIndex.get(entry.getKey());
                if (list == null) {
                    ArrayList<Weight> arrayList = new ArrayList<>();
                    arrayList.add(weight);
                    invertedIndex.put(entry.getKey(), arrayList);
                } else {
                    list.add(weight);
                }
            }
        }
        // 存储到磁盘中，不能在启动服务器的时候构建索引
        // 把耗时操作单独的完成
    }

    private DocInfo buildForward(String title, String url, String content) {

        DocInfo docInfo = new DocInfo();
        docInfo.setTitle(title);
        docInfo.setUrl(url);
        docInfo.setContent(content);
        synchronized (forwardIndex) {
            docInfo.setDocId(this.forwardIndex.size());
            forwardIndex.add(docInfo);
        }
        return docInfo;

    }


    private static final String INDEX_PATH = "E:\\Javacode\\20220726\\";

    private ObjectMapper objectMapper = new ObjectMapper();
    // 4. 把内存中的索引结果保存到磁盘中
    public void save() {
        long t1 = System.currentTimeMillis();
        System.out.println("开始保存索引");
        // 使用两个文件分别保存正排和倒排
        File indexPathFile = new File(INDEX_PATH);
        if (indexPathFile.exists()) {
            indexPathFile.mkdirs();
        }

        File forwardIndexFile = new File(INDEX_PATH + "forward.txt");
        File invertedIndexFile = new File(INDEX_PATH + "inverted.txt");

        try {
            objectMapper.writeValue(forwardIndexFile, forwardIndex);
            objectMapper.writeValue(invertedIndexFile, invertedIndex);

            long t2 = System.currentTimeMillis();
            System.out.println("保存文件结束，耗时为：" + (t2 - t1) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 5. 把磁盘中的索引数据加载到内存中
    public void load() {
        System.out.println("开始加载模块");
        long t1 = System.currentTimeMillis();
        File forwardIndexFile = new File(INDEX_PATH + "forward.txt");
        File invertedIndexFile = new File(INDEX_PATH + "inverted.txt");
        try {
            this.forwardIndex = objectMapper.readValue(forwardIndexFile, new TypeReference<ArrayList<DocInfo>>() {});
            this.invertedIndex = objectMapper.readValue(invertedIndexFile, new TypeReference<HashMap<String, ArrayList<Weight>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("加载结束，耗时为" + (t2 - t1) + "ms");
    }

    public static void main(String[] args) {
        Index index = new Index();
        index.load();
    }

}
