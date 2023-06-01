package com.yt.ytsearch.datasource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class JavaApiDocDatasourceTest {

    @Resource
    private JavaApiDocDatasource javaApiDocDatasource;

    @Test
    void doSearch() {
        javaApiDocDatasource.doSearch("arrayList", 0, 10);
    }
}