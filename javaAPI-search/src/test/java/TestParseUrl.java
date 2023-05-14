import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.io.File;

/**
 * created by YT
 * description:
 * User:lenovo
 * Data:2022-07-26
 * Time:20:23
 */
public class TestParseUrl {
    private static final String INPUT_PATH = "D:/桌面/jdk-8u341-docs-all/docs/api";
    public static void main(String[] args) {
        File file = new File("D:\\桌面\\jdk-8u341-docs-all\\docs\\api\\serialized-form.html");
        String part1 = "https://docs.oracle.com/javase/8/docs/api";
        String part2 = file.getAbsolutePath().substring(INPUT_PATH.length());
        String url = part1 + part2;
        System.out.println(url);

    }
}
