package com.yt.project.job.once;

import com.yt.project.common.RedisCommon;
import com.yt.project.model.entity.DocInfo;
import com.yt.project.model.entity.Weight;
import com.yt.project.service.DocInfoService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 启动时构建索引,解析
 */
@Component
@Slf4j
public class Parser {

    private final String INPUT_PATH = "E:/github/frontendAndBackend/YTAggSearch-new/java-api-search-module/jdk-8u341-docs-all/docs/api";

    private final String BASE_URL_Path = "https://docs.oracle.com/javase/8/docs/api";

    private Index index = new Index();

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private DocInfoService docInfoService;


    public void runByThreads() {
        long begin = System.currentTimeMillis();

        List<File> fileList = new ArrayList<>();
        // 枚举所有文件
        enumFile(INPUT_PATH, fileList);

        CountDownLatch countDownLatch = new CountDownLatch(fileList.size());
        log.error("" + fileList.size());
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (File file : fileList) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    log.info("开始解析：" + file.getAbsolutePath());
                    parserHtml(file);
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        // 保存索引
        // index.save();
        // 将索引存入数据库中
        RMap<String,  List<Weight>> invertedIndex = redissonClient.getMap(RedisCommon.invertedIndex);
        invertedIndex.putAll(index.getInvertedIndex());
        log.info("开始将数据统一存入数据库中");
        ArrayList<DocInfo> forwardIndex = index.getForwardIndex();
        for (int i = 0; i < forwardIndex.size(); i+=200) {
            docInfoService.saveBatch(forwardIndex.subList(i, Math.min(i+200, forwardIndex.size())));
        }
        log.info("存入mysql结束");
        long end = System.currentTimeMillis();
        log.info("保存时间完毕，解析时间是：" + (end - begin) + "ms");
    }

    public void run() {
        long begin = System.currentTimeMillis();

        List<File> fileList = new ArrayList<>();
        // 枚举所有文件
        enumFile(INPUT_PATH, fileList);
        // 开始解析
        for (File file : fileList) {
            log.info("开始解析：" + file.getAbsolutePath());
            parserHtml(file);
        }
        index.save();
        long end = System.currentTimeMillis();
        log.info("解析时间为：" + (end - begin) + "ms");
    }

    /**
     * 解析html文件
     * @param file
     */
    private void parserHtml(File file) {
        String title = parserTitle(file);
        String url = parserUrl(file);
        String content = parserContent(file);
        index.addDoc(title, url, content);
    }



    /**
     * 解析文章内容
     * @param file
     * @return
     */
    private String parserContent(File file) {
        // 需要将内容中的标签去掉
        StringBuilder content = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(file), 1024 * 1024)) {
            //是否拷贝的flag
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
                    if ((char) ch == '\n' || (char)ch == '\r') {
                        content.append(" ");
                        continue;
                    }
                    content.append((char) ch);
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

    /**
     * url解析
     * @param file
     * @return
     */
    private String parserUrl(File file) {
        String path = file.getAbsolutePath().substring(INPUT_PATH.length());
        String url = BASE_URL_Path + path;
        return url;
    }

    /**
     * 解析标题
     * @param file
     * @return
     */
    private String parserTitle(File file) {
        String name = file.getName();
        String title = name.substring(0, name.length() - ".html".length());
        return title;
    }

    /**
     * 递归的方式枚举所有文件
     * @param input_path
     * @param fileList
     */
    private void enumFile(String input_path, List<File> fileList) {
        File rootPath = new File(input_path);
        // 使用listFiles只能看到一级目录
        File[] files = rootPath.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                enumFile(file.getAbsolutePath() ,fileList);
            } else {
                if (file.getName().endsWith(".html")) {
                    fileList.add(file);
                }
            }
        }

    }


}
