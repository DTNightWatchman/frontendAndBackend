import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * created by YT
 * description:
 * User:lenovo
 * Data:2022-07-27
 * Time:15:48
 */
public class DocSearcher {

    private Index index = new Index();

    public DocSearcher() {
        index.load();
        loadStopWords();
    }

    public List<Result> search(String query) {
        // 1. 进行分词
        List<Term> oldTerms = ToAnalysis.parse(query).getTerms();
        List<Term> terms = new ArrayList<>();

        // 去掉暂停词
        for (Term t : oldTerms) {
            if (!stopWords.contains(t.getName())) {
                terms.add(t);
            }
        }

        // 2. 针对分词结果去查倒排
        List<List<Weight>> allResult = new ArrayList<>();
        for (Term term : terms) {
            String word = term.getName();
            List<Weight> list = index.getInverted(word);
            if (list != null) {
                allResult.add(list);
            }
        }

        List<Weight> finalResult = mergeResult(allResult);

        // 3. 针对触发结果按照权重降序排序
        finalResult.sort(new Comparator<Weight>() {
            @Override
            public int compare(Weight o1, Weight o2) {
                return o2.getWeight() - o1.getWeight();
            }
        });
        // 4. 包装结果，针对结果查找正排，打包数据
        List<Result> results = new ArrayList<>();
        for (Weight weight: finalResult) {
            DocInfo docInfo = index.getDocInfo(weight.getDocId());
            Result result = new Result();
            result.setTitle(docInfo.getTitle());
            result.setUrl(docInfo.getUrl());
            String desc = getDesc(docInfo.getContent(), terms);
            //System.out.println(desc);
            if (desc != null) {
                result.setDesc(desc);
                results.add(result);
            }

        }
        return results;
    }

    static class Pos {
        public int row;
        public int col;

        public Pos(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private List<Weight> mergeResult(List<List<Weight>> allResult) {
        for (List<Weight> curRow: allResult) {
            curRow.sort(new Comparator<Weight>() {
                @Override
                public int compare(Weight o1, Weight o2) {
                    return o1.getDocId() - o2.getDocId();
                }
            });
        }
        List<Weight> ret = new ArrayList<>();
        // 有限队列
        PriorityQueue<Pos> queue = new PriorityQueue<>(new Comparator<Pos>() {
            @Override
            public int compare(Pos o1, Pos o2) {
                // 根据位置找到pos值找到weight，再根据docId排序
                Weight w1 = allResult.get(o1.row).get(o1.col);
                Weight w2 = allResult.get(o2.row).get(o2.row);
                return w1.getDocId() - w2.getDocId();

            }
        });
        for (int i = 0; i < allResult.size(); i++) {
            queue.offer(new Pos(i,0));
        }
        while (!queue.isEmpty()) {
            Pos min = queue.poll();
            Weight w = allResult.get(min.row).get(min.col);
            if (ret.size() > 0) {
                Weight befWeight = ret.get(ret.size() - 1);
                if (befWeight.getDocId() == w.getDocId()) {
                    befWeight.setWeight(befWeight.getWeight() + w.getWeight());
                } else {
                    ret.add(w);
                }
            } else {
                ret.add(w);
            }
            // 向后移动
            Pos newPos = new Pos(min.row, min.col + 1);
            if (newPos.col < allResult.get(newPos.row).size()) {
                //未到达末尾
                queue.offer(newPos);
            }

        }
        return ret;
    }

    private String getDesc(String content, List<Term> terms) {
        int firstPos = -1;
        for (Term term : terms) {
            String word = term.getName();
            content = content.toLowerCase().replaceAll("\\b" + word + "\\b"," " + word + " ");
            firstPos = content.toLowerCase().indexOf(" " + word + " ");
            if (firstPos > 0) {
                break;
            }
        }
        if (firstPos == -1) {
            return null;
        }

        // 第一个位置作为基准位置
        String desc = null;
        int descBegin = firstPos < 60 ? 0 : firstPos - 60;
        if (firstPos + 160 >= content.length()) {
            desc = content.substring(descBegin);
        } else {
            desc = content.substring(descBegin, firstPos + 160) + "...";
        }
        //System.out.println("1");
        for (Term term: terms) {
            //System.out.println("2");
            String word  = term.getName();
            //System.out.println(desc);
            desc  = desc.replaceAll("(?i) " + word + " ", "<i> " + word + " </i>");
            //System.out.println(desc);
        }
        return desc;
    }

    public static final String STOP_WORD_PATH = "E:/Javacode/20220726/stop.txt";

    public Set<String> stopWords = new HashSet<>();
    public void loadStopWords() {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(STOP_WORD_PATH))) {
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                stopWords.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DocSearcher docSearcher = new DocSearcher();
        Scanner scanner = new Scanner(System.in);
        String query = scanner.next();
        List<Result> results = docSearcher.search(query);
        for (Result result : results) {
            System.out.println(result);
        }

    }
}
