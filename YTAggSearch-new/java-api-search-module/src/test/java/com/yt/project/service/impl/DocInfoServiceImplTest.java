package com.yt.project.service.impl;

import com.yt.common.model.vo.DocInfoVO;
import com.yt.project.service.DocInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;
import java.util.Scanner;

@SpringBootTest
class DocInfoServiceImplTest {

    @Resource
    private DocInfoService docInfoService;

    @Test
    void searchDocInfo() {
        List<DocInfoVO> docInfoVOS = docInfoService.searchDocInfo("arrayList", 0, 10);
        for (DocInfoVO docInfoVO : docInfoVOS) {
            System.out.println(docInfoVO);
        }
    }

    @Test
    void searchDocInfoFromRedis() {
        final List<DocInfoVO> docInfoVOS = docInfoService.searchDocInfoFromRedis("arrayList", 1, 10);
        System.out.println(docInfoVOS);
    }
}