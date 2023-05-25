package com.yt.project.job.once.search;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ParserTest {

    @Resource
    private Parser parser;

    @Test
    void runByThread() {
        parser.runByThread();
    }
}