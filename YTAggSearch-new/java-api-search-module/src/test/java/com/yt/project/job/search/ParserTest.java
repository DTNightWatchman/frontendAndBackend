package com.yt.project.job.search;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ParserTest {

    @Resource
    private Parser parser;

    @Test
    void runByThread() throws InterruptedException {
        parser.runByThread();
    }

    @Test
    void run() {
        parser.run();
    }
}