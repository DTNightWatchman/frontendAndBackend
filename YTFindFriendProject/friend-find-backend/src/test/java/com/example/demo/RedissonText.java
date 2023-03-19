package com.example.demo;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedissonText {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void test() {
        // list map set
        RList<Object> rList = redissonClient.getList("test-list");
        rList.remove("hello1");
        rList.forEach(t -> {
            System.out.println(t);
        });
    }

    @Test
    public void test1() {
        String t = "";
        System.out.println("t的值为：" + t);
        boolean blank = StringUtils.isNotBlank("");
        System.out.println(blank);
    }
}
