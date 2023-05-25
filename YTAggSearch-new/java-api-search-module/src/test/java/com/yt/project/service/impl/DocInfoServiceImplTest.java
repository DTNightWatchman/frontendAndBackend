package com.yt.project.service.impl;

import com.yt.project.model.vo.DocInfoVO;
import com.yt.project.service.DocInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DocInfoServiceImplTest {

    @Resource
    private DocInfoService docInfoService;

    @Test
    void searchDocInfo() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            List<DocInfoVO> docInfoVOS = docInfoService.searchDocInfo(scanner.next(), 0, 10);
            System.out.println(docInfoVOS);
        }
    }
}