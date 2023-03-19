package com.example.demo;
import java.util.Date;

import com.example.demo.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;


    @Test
    void hello() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("YTString","YT1");
        valueOperations.set("int", 1);
        valueOperations.set("double", 1.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("YT摆渡人");
        valueOperations.set("user", user);
        System.out.println("???");
        // 查
        Object ytString = valueOperations.get("YTString");
        Assert.assertTrue("YT1".equals(ytString));
        Object anInt = valueOperations.get("int");
        Assert.assertTrue(1 == ((Integer) ((anInt == null) ? 0 : anInt)));
        Object aDouble = valueOperations.get("double");
        Assert.assertTrue(1.0 == (Double)aDouble);
        System.out.println(valueOperations.get("user"));


    }
}
