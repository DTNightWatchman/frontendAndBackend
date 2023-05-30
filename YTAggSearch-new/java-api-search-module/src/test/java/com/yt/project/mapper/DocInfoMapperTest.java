package com.yt.project.mapper;

import com.yt.project.model.entity.DocInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DocInfoMapperTest {

    @Resource
    private DocInfoMapper docInfoMapper;

    @Test
    void batchInsert() {
        List<DocInfo> list = new ArrayList<>();

        docInfoMapper.batchInsert(list);
    }
}