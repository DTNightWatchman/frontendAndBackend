package com.yt.project.job;

import com.yt.project.common.RedisCommon;
import com.yt.project.model.entity.Weight;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class TestRedis {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        private String name;

        private int age;
    }

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Test
    void testSaveUser() {
        // 写入数据
        //ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        //valueOperations.set("user:100", new User("YTbaiduren", 18));
        // 获取数据
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        ArrayList<Weight> weights = new ArrayList<>();
        Weight weight1 = new Weight();
        weight1.setDocId(1);
        weight1.setWeight(10);
        Weight weight2 = new Weight();
        weight2.setDocId(1);
        weight2.setWeight(20);
        weights.add(weight1);
        weights.add(weight2);
        hashOperations.put(RedisCommon.invertedIndex, "test word", weights);
        //User user = (User) valueOperations.get("user:100");
        List<Weight> weightRes = (List<Weight>) hashOperations.get("ytSearchProject:javaApiDocSearch:invertedIndex", "test word");
        System.out.println(weightRes);
        //System.out.println(user);

    }
}
