package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.ErrorCode;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.TeamMapper;
import com.example.demo.model.domain.Team;
import com.example.demo.model.domain.User;
import com.example.demo.model.domain.UserTeam;
import com.example.demo.model.vo.TeamUserVO;
import com.example.demo.model.vo.UserVO;
import com.example.demo.service.TeamService;
import com.example.demo.service.UserService;
import com.example.demo.service.UserTeamService;
import com.example.demo.mapper.UserTeamMapper;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author lenovo
* @description 针对表【user_team(用户队伍对应表)】的数据库操作Service实现
* @createDate 2022-11-08 11:41:53
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

    @Resource
    private UserTeamMapper userTeamMapper;


//    @Resource
//    private TeamService teamService;


    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserService userService;

    @Override
    public List<TeamUserVO> getUserJoinTeams(User loginUser) {
        List<Long> userJoinTeamIds = userTeamMapper.getUserJoinTeamIds(loginUser.getId());
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        if (userJoinTeamIds.size() != 0) {
            teamQueryWrapper.in("id", userJoinTeamIds);
        }
        //List<Team> teamList = teamService.list(teamQueryWrapper);
        teamQueryWrapper.gt("expireTime", new Date());
        List<Team> teamList = teamMapper.selectList(teamQueryWrapper);
        UserVO userVO = new UserVO();

        try {
            BeanUtils.copyProperties(userVO, loginUser);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        for (Team team : teamList) {
            TeamUserVO teamUserVO = new TeamUserVO();
            try {
                BeanUtils.copyProperties(teamUserVO, team);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            User userMessage = userService.getById(team.getUserId());
            try {
                BeanUtils.copyProperties(userVO, userMessage);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }
}




