import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by YT
 * description:
 * User:lenovo
 * Data:2022-07-26
 * Time:12:32
 */
public class Parser {
    private static final String INPUT_PATH = "D:/桌面/jdk-8u341-docs-all/docs/api";

    private Index index = new Index();

    public void runByThread() throws InterruptedException {
        long beg = System.currentTimeMillis();
        System.out.println("开始制作索引");

        ArrayList<File> files = new ArrayList<>();
        enumFile(INPUT_PATH, files);
        // 使用线程池来制作索引
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (File file : files) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("解析：" + file.getAbsolutePath());
                    parseHTML(file);
                    countDownLatch.countDown();
                }
            });
        }
        // 直到所有任务完成
        countDownLatch.await();

        executorService.shutdown();

        // 保存索引
        index.save();

        long end = System.currentTimeMillis();
        System.out.println("结束制作索引");
        System.out.println("多线程消耗时间" + (end - beg) + "ms");
    }

    public void run() {
        long begin = System.currentTimeMillis();
        // 入口
        // 1. 根据指定路径，枚举出所有文件（html）,这个过程需要把所有子目录的文件全部获取到
        List<File> fileList = new ArrayList<>();
        enumFile(INPUT_PATH, fileList);
        for (File file: fileList) {
            System.out.println("开始解析：" + file.getAbsolutePath());
            parseHTML(file);
        }
        //System.out.println(fileList.size());
        // 2. 根据文件路径，打开文件，读取文件内容，并进行解析，构建索引
        // 3. 把在内存中构造的索引数据结构保存到指定文件中
        index.save();
        long end = System.currentTimeMillis();
        System.out.println("索引制作完毕，消耗时间为：" + (end - begin) + "ms");

    }

    /**
    * @Author: YT摆渡人
    * @Description: 解析html页面
    * @DateTime: 2022/7/26 13:11
    * @Params:
    * @Return
    */

    private void parseHTML(File file) {
        // 一条搜索结果就包含了 标题，描述（是正文的一段摘要），展示url，这些信息就要来自解析的html
        // 要想得到描述，先要获得正文
        String title = parseTitle(file);
        String url = parseUrl(file);
        String content = parseContentByRegex(file);
        // 加载到index
        index.addDoc(title, url, content);

    }

    public String parseContentByRegex(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file),1024 * 1024)) {
            while (true) {
                int ch = reader.read();
                if (ch < 0 ) {
                    break;
                }
                char c = (char) ch;
                if (c == '\n' || c == '\r') {
                    c = ' ';
                }
                content.append(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return "++++++++" + content.toString();
        String ret = content.toString().replaceAll("<script.*?>(.*?)</script>", " ");
        ret = ret.replaceAll("<.*?>", " ");
        ret = ret.replaceAll("\\s+"," ");
        return  ret;
    }

    public String parseContent(File file) {
        // 以 < 和 > 判断是否拷贝，注意：是按照字符来读取
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file),1024 * 1024)) {
            // 是否拷贝开发 flag
            boolean flag = true;
            while (true) {
                int ch = reader.read();
                if (ch < 0) {
                    break;
                }

                if (flag) {
                    if ((char)ch == '<') {
                        flag = false;
                        continue;
                    }
                    if ((char)ch == '\n' || (char)ch == '\r') {
                        content.append(' ');
                        continue;
                    }

                    content.append((char)ch);
                } else {
                    if ((char)ch == '>') {
                        flag = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return content.toString();
    }

    private String parseUrl(File file) {
        String part1 = "https://docs.oracle.com/javase/8/docs/api";
        String part2 = file.getAbsolutePath().substring(INPUT_PATH.length());
        String url = part1 + part2;
        return url;
    }

    private String parseTitle(File file) {
        String name = file.getName();
        String title = name.substring(0, name.length() - ".html".length());
        return title;
    }

    /**
    * @Author: YT摆渡人
    * @Description: 枚举文件
    * @DateTime: 2022/7/26 12:43
    * @Params: （1）表示递归文件目录  （2）递归得到的结果
    */
    private void enumFile(String inputPath, List<File> fileList) {
        File rootPath = new File(inputPath);
        // 使用listFiles只能看到一级目录
        File[] files = rootPath.listFiles();
        for (File file: files) {
            if (file.isDirectory()) {
                enumFile(file.getAbsolutePath(), fileList);
            } else {
                if (file.getName().endsWith(".html")) {
                    fileList.add(file);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser();
        parser.run();
        String ret1 = parser.parseContentByRegex(new File(INPUT_PATH + "\\javax\\lang\\model\\util\\Types.html"));

        System.out.println(ret1);
    }

}
