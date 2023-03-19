package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.BaseResponse;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.ResultUtil;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.domain.Team;
import com.example.demo.model.domain.User;
import com.example.demo.model.dto.TeamQuery;
import com.example.demo.model.request.*;
import com.example.demo.model.vo.TeamUserVO;
import com.example.demo.model.vo.UserVO;
import com.example.demo.service.TeamService;
import com.example.demo.service.UserService;
import com.example.demo.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Author: YT
 * @Description: 队伍操纵
 * @DateTime: 2022/11/08 - 15:29
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {
    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;


    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamRequest teamRequest, HttpServletRequest request) {
        if (teamRequest == null) {
            throw  new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamRequest);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        long teamId = teamService.addTeam(team, loginUser);
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入失败");
        }
        // 将队伍Id返回回去
        return ResultUtil.success(teamId);
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteTeamRequest deleteTeamRequest, HttpServletRequest request) {
        if (deleteTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean b = teamService.deleteTeam(deleteTeamRequest, loginUser);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }
        // 删除成功
        return ResultUtil.success(true);
    }


    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Team team = teamService.getById(id);
        if (team == null ) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        return ResultUtil.success(team);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.UPDATE_ERROR);
        }
        return ResultUtil.success(true);
    }

//    @GetMapping("/list")
//    public BaseResponse<List<Team>> getTeams(TeamQuery teamQuery) {
//        if (teamQuery == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        Team team = new Team();
//        try {
//            BeanUtils.copyProperties(team, teamQuery);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//        }
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
//        List<Team> list = teamService.list(queryWrapper);
//        return ResultUtil.success(list);
//    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> getTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, userService.getLoginUser(request).getUserRole() == 1, request);
        return ResultUtil.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamQuery);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        int current = teamQuery.getPageNum();
        int pageSize = teamQuery.getPageSize();
        Page<Team> page = new Page<>(current, pageSize);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtil.success(resultPage);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtil.success(result);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtil.success(result);
    }


    /**
     * 获取我创建的用户
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyTeams(HttpServletRequest request) {
        TeamQuery teamQuery = new TeamQuery();
        User loginUser = userService.getLoginUser(request);
        List<TeamUserVO> teamList = teamService.listMyTeams(loginUser.getId(), request);
        return ResultUtil.success(teamList);
    }


    /**
     * 获取加入的队伍
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(HttpServletRequest request) {
        List<TeamUserVO> userJoinTeams = userTeamService.getUserJoinTeams(userService.getLoginUser(request));
        return ResultUtil.success(userJoinTeams);
    }


}
