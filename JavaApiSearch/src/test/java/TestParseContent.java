import java.io.File;

/**
 * created by YT
 * description:
 * User:lenovo
 * Data:2022-07-26
 * Time:20:42
 */
public class TestParseContent {
    public static void main(String[] args) {
        Parser parser = new Parser();
        File file = new File("D:\\桌面\\jdk-8u341-docs-all\\docs\\api\\serialized-form.html");

        String ret = parser.parseContent(file);
        System.out.println(ret);
    }
}
