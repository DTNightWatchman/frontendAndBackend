import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by YT
 * description:
 * User:lenovo
 * Data:2022-07-26
 * Time:12:12
 */
public class TestAnsj {
    public static void loadDictFromUerFile() throws Exception {
        //JAR包同级目录下/library/userLibrary.dic
        Forest userForest = Library.makeForest(System.getProperty("user.dir") + "/library/userLibrary.dic");
        List<String[]> userDict = new ArrayList<>();
        for (Map.Entry oneData : userForest.toMap().entrySet()) {
            String[] values = oneData.getKey().toString().split("\\t|\\s+");
            if (values.length != 3) {
                throw new Exception("user dictionary data count wrong, please check the format, key:" + values[0]);
            }
            userDict.add(values);
        }
        for (String[] dict : userDict) {
            DicLibrary.insert(DicLibrary.DEFAULT, dict[0].toLowerCase(), dict[1], Integer.parseInt(dict[2]));
        }
    }

    public static void main(String[] args) {
        String str = "小明毕业于福建农林大学的计算机与信息学院，后来又去蓝翔学习挖掘机，将挖掘机技术与计算机技术相结合";
        List<Term> terms = ToAnalysis.parse(str).getTerms();
        // Term 就表示一个分词结果
        for (Term t : terms) {
            System.out.println(t.getName());
        }

        List<Term> termList = DicAnalysis.parse(str).getTerms();
        for (Term term : termList) {
            System.out.println("分词名称：" + term.getName() + "，词性：" + term.getNatureStr());
        }

    }
}
