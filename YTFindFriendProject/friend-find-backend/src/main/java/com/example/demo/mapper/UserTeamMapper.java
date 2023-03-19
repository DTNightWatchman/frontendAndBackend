package com.example.demo.mapper;

import com.example.demo.model.domain.Team;
import com.example.demo.model.domain.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author lenovo
* @description 针对表【user_team(用户队伍对应表)】的数据库操作Mapper
* @createDate 2022-11-08 11:41:52
* @Entity com.example.demo.model.domain.UserTeam
*/
public interface UserTeamMapper extends BaseMapper<UserTeam> {
    List<Long> getUserJoinTeamIds(Long userId);

}




