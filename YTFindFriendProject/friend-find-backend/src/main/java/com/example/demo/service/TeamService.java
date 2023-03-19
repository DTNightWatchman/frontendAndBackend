package com.example.demo.service;

import com.example.demo.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.domain.User;
import com.example.demo.model.domain.UserTeam;
import com.example.demo.model.dto.TeamQuery;
import com.example.demo.model.request.DeleteTeamRequest;
import com.example.demo.model.request.TeamJoinRequest;
import com.example.demo.model.request.TeamQuitRequest;
import com.example.demo.model.request.TeamUpdateRequest;
import com.example.demo.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author lenovo
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2022-11-07 23:52:43
*/
public interface TeamService extends IService<Team> {


    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);


    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);


    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 队长解散队伍
     * @param deleteTeamRequest
     * @param loginUser
     * @return
     */
    boolean deleteTeam(DeleteTeamRequest deleteTeamRequest, User loginUser);


    /**
     * 获取队伍信息
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin, HttpServletRequest request);


    List<TeamUserVO> listTeamsBySql(TeamQuery teamQuery, boolean isAdmin);


    /**
     * 修改队伍信息
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);


    /**
     * 获取到自己创建的队伍
     * @param id
     * @return
     */
    List<TeamUserVO> listMyTeams(Long id, HttpServletRequest request);
}
