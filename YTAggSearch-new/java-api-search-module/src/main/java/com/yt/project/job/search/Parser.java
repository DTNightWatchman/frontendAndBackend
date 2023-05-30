package com.yt.project.job.search;

import com.yt.project.mapper.DocInfoMapper;
import com.yt.project.model.entity.DocInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class Parser {

    @Resource
    private DocInfoMapper docInfoMapper;



    // jdk路径
    private final static String JDK_DOC_PATH = "E:/github/frontendAndBackend/YTAggSearch-new/java-api-search-module/jdk-8u341-docs-all/docs/api";

    @Resource
    private Index index;

    @Transactional
    public void runByThread() throws InterruptedException {
        long begin = System.currentTimeMillis();
        log.info("开始制作索引");
        ArrayList<File> files = new ArrayList<>();
        enumFile(JDK_DOC_PATH, files);
        // 使用线程池来构建索引
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        ExecutorService executorService = Executors.newFixedThreadPool(12);
        System.out.println(files.size());
        for (File file : files) {
            executorService.submit(() -> {
                log.info("开始解析：" + file.getAbsolutePath());
                parseHtml(file);
                countDownLatch.countDown();
                //log.error(String.valueOf(countDownLatch.getCount()));
            });
        }
        // 等待所有任务完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        // 保存索引
        //index.save();
        index.saveToDb();
        // 将所有的结果一次性存入数据库中
        long end = System.currentTimeMillis();
        log.info("解析并制作索引完成，耗时：" + (end - begin) + "ms");
    }

    /**
     * 解析html文件
     * @param file
     */
    @Transactional
    public void parseHtml(File file) {
        String title = parseTitle(file);
        String url = parseUrl(file);
        String content = parseContent(file);
        index.addDoc(title, url, content);
    }

    /**
     * 解析得到正文
     * @param file
     * @return
     */
    private String parseContent(File file) {
        StringBuilder content = new StringBuilder();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file), 1024 * 1024)) {
            while (true) {
                int ch = bufferedReader.read();
                if (ch < 0) {
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
        // 去掉 script 标签中的内容
        String ret = content.toString().replaceAll("<script.*?>(.*?)</script>", " ");
        // 去掉所有的标签
        ret = ret.replaceAll("<.*?>", " ");
        ret = ret.replaceAll("\\s+", " ");
        return ret;
    }


    /**
     * 解析得到url
     * @param file
     * @return
     */
    private String parseUrl(File file) {
        String part1 = "https://docs.oracle.com/javase/8/docs/api";
        String part2 = file.getAbsolutePath().substring(JDK_DOC_PATH.length());
        return part1 + part2;
    }


    /**
     * 解析出title
     * @param file
     * @return
     */
    private String parseTitle(File file) {
        String name = file.getName();
        return name.substring(0, name.length() - ".html".length());
    }

    /**
     * 开始加载索引
     * @param path
     * @param fileList
     */
    private void enumFile(String path, ArrayList<File> fileList) {
        File rootPath = new File(path);
        File[] files = rootPath.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                enumFile(file.getAbsolutePath(), fileList);
            } else {
                 if (file.getName().endsWith(".html")) {
                     fileList.add(file);
                 }
            }
        }
    }

}
