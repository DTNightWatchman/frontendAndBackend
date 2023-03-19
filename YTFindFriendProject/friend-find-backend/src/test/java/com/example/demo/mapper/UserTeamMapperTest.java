package com.example.demo.mapper;

import com.example.demo.model.domain.UserTeam;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserTeamMapperTest {

    @Resource
    private UserTeamMapper userTeamMapper;

    @Test
    void getUserJoinTeamIds() {
        for (Long userJoinTeamId : userTeamMapper.getUserJoinTeamIds(4L)) {
            System.out.println(userJoinTeamId);
        }
    }
}