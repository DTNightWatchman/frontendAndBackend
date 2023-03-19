package com.example.demo.service;

import com.example.demo.model.domain.User;
import com.example.demo.model.domain.UserTeam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.vo.TeamUserVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author lenovo
* @description 针对表【user_team(用户队伍对应表)】的数据库操作Service
* @createDate 2022-11-08 11:41:53
*/
public interface UserTeamService extends IService<UserTeam> {

    /**
     * 获取登录用户的加入的队伍信息
     * @param loginUser
     * @return
     */
    List<TeamUserVO> getUserJoinTeams(User loginUser);


}
