package com.yt.ytsearch.service.impl;

import com.yt.ytsearch.model.entity.Picture;
import com.yt.ytsearch.service.PictureService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PictureServiceImplTest {

    @Resource
    private PictureService pictureService;

    @Test
    void searchPicture() {
        List<Picture> test = pictureService.searchPicture("小黑子露出鸡脚", 1L);
        System.out.println(test);
    }
}