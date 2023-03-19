package com.example.demo.service.impl;

import cn.hutool.core.date.StopWatch;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.domain.User;
import com.example.demo.utils.PasswordUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class insertUsersTest {


    @Autowired
    private UserMapper userMapper;


}
