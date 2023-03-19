package com.example.demo.service.impl;

import cn.hutool.core.date.StopWatch;
import com.example.demo.model.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.utils.PasswordUtil;
import com.mysql.cj.util.TimeUtil;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: YT
 * @Description:
 * @DateTime: 2022/9/27$ - 14:24
 */
@SpringBootTest
class UserServiceImplTest {

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

//    @Test
//    public void doInsertUsers() {
//        final int INSERT_NUM = 10_0000;
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        List<User> userList = new ArrayList<>();
//        List<CompletableFuture<Void>> futureList = new ArrayList<>();
//        int j = 0;
//        for (int i = 0; i < 10; i++) {
//            while (true) {
//                j++;
//                User user = new User();
//                user.setUsername("假的用户名");
//                user.setUserAccount("fakeUserAccount");
//                user.setAvatarUrl("https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg");
//                user.setGender((byte)0);
//                user.setPassword(PasswordUtil.encrypt("password"));
//                user.setPhone("123456789");
//                user.setEmail("123456@qq.com");
//                user.setUserStatus(0);
//                user.setUserRole(0);
//                user.setPlanetCode("11111");
//                userList.add(user);
//                if (j % 10000 == 0) {
//                    break;
//                }
//            }
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                System.out.println("threadName" + Thread.currentThread().getName());
//                userService.saveBatch(userList, 10000);
//            }, executorService);
//            futureList.add(future);
//        }
//        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
//        stopWatch.stop();
//        System.out.println(stopWatch.getTotalTimeMillis());
//    }


    @Test
    void searchUsersByTags() {
        List<String> tagList = Arrays.asList("男");

        List<User> userList = userService.searchUsersByTags(tagList);
        System.out.println(userList);
        System.out.println(userList.size());
        Assert.assertNotNull(userList);
    }

    @Test
    void searchUsersByTagsByIn() {
        List<String> tagList = Arrays.asList("男","java");
        List<User> userList = userService.searchUsersByTagsByMem(tagList);
        userList.forEach(user -> {
            System.out.println(user);
        });

    }
}