package com.example.demo.service;

import com.example.demo.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author: YT
 * @Description:
 * @DateTime: 2022/9/26$ - 16:31
 */

@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("YT摆渡人");
        user.setUserAccount("123");
        user.setAvatarUrl("https://profile.csdnimg.cn/9/1/7/1_qq_52846188");
        user.setGender((byte)0);
        user.setPassword("admin");
        user.setPhone("123");
        user.setEmail("456");
        user.setUserStatus(0);
        user.setUserRole(0);
        user.setPlanetCode("");
        user.setTags("");
        boolean res =  userService.save(user);
        System.out.printf(String.valueOf(user.getId()));
        Assertions.assertTrue(res);
    }

}