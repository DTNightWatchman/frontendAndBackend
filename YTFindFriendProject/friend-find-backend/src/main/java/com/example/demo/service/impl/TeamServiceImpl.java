package com.example.demo.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.ErrorCode;
import com.example.demo.constant.RedisConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.domain.Team;
import com.example.demo.model.domain.User;
import com.example.demo.model.domain.UserTeam;
import com.example.demo.model.dto.TeamQuery;
import com.example.demo.model.enums.TeamStatusEnum;
import com.example.demo.model.request.DeleteTeamRequest;
import com.example.demo.model.request.TeamJoinRequest;
import com.example.demo.model.request.TeamQuitRequest;
import com.example.demo.model.request.TeamUpdateRequest;
import com.example.demo.model.vo.TeamUserVO;
import com.example.demo.model.vo.UserVO;
import com.example.demo.service.TeamService;
import com.example.demo.mapper.TeamMapper;
import com.example.demo.service.UserService;
import com.example.demo.service.UserTeamService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.DVALRecord;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author lenovo
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2022-11-07 23:52:43
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if(team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        final long loginUserId = loginUser.getId();
        // 防止取出为空值
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }

        String name = team.getName();
        if (StringUtils.isNotBlank(name) && name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不符合要求");
        }

        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不符合要求");
        }

        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(status);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态参数错误");
        }
        // 逻辑判断，如果是加密状态，就必须要有密码，并且密码的长度要小于32
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum) && (StringUtils.isBlank(team.getPassword()) || team.getPassword().length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不符合规范");
        }
        //
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            // 此时已经过期
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间错误");
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUserId);
        long hasTeamNum = this.count(queryWrapper);
        // todo 多线程问题，最好使用分布式锁
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.NO_AUTH, "当前权限不能创建五个以上的队伍");
        }
        team.setId(null);
        team.setUserId(loginUserId);
        boolean result = this.save(team);
        long teamId = team.getId();
        if (!result || teamId <= 0) {
            throw new BusinessException(ErrorCode.ADD_ERROR, "创建队伍失败");
        }
        UserTeam userTeam = new UserTeam();

        userTeam.setUserId(loginUserId);
        userTeam.setJoinTime(new Date());
        userTeam.setTeamId(teamId);
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.ADD_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        // 这里判断退出的人（当前用户）是否在队伍中
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(loginUser.getId());
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(queryUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count <= 0) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 退出队伍逻辑
        long teamUserNum = this.countTeamUserByTeamId(teamId);
        if (teamUserNum <= 1) {
            // 直接解散队伍
            this.removeById(teamId);
        } else {
            // 1. 先判断自己是不是队长，如果不是队长，就直接退出，如果是队长就顺延
            // 判断自己是不是队长
            if (team.getUserId().longValue() == loginUser.getId().longValue()) {
                // 是队长
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by createTime limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamUserId = nextUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamUserId);
                boolean b = this.updateById(updateTeam);
                if (!b) {
                    // 更新队长失败
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队长失败");
                }
            }
        }
        // 删除逻辑关系
        boolean remove = userTeamService.remove(queryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return true;
    }



    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        // 判断参数
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        if (team.getExpireTime() != null) {
            if (team.getExpireTime().before(new Date())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已经过期");
            }
        }
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(team.getStatus());
        if (TeamStatusEnum.PRIVATE.equals(teamStatusEnum)) {
            // 私密，不能加入
            throw new BusinessException(ErrorCode.NO_AUTH, "不能加入私有队伍");
        }
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(teamStatusEnum)) {
            // 加密队伍
            if (StringUtils.isBlank(password) || !password.equals(teamJoinRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        // 允许加入
        // 判断目前自己加入的队伍不能超过多少
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", loginUser.getId());
        RLock countUserJoinNumLock = redissonClient.getLock(RedisConstant.REDIS_JOIN_COUNT_LOCK + loginUser.getId());
        try {
            boolean isLock = countUserJoinNumLock.tryLock(0, -1, TimeUnit.MILLISECONDS);
            if (!isLock) {
                throw new BusinessException(ErrorCode.ADD_ERROR, "加入频率过快");
            }
            long userHasJoinTeamCount = userTeamService.count(userTeamQueryWrapper);
            if (userHasJoinTeamCount >= 5) {
                // 最多加入5个
                throw new BusinessException(ErrorCode.ADD_ERROR, "最多加入5个队伍");
            }
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.eq("teamId", teamId);
            long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
            if (hasUserJoinTeam > 0) {
                // 已经加入队伍
                throw new BusinessException(ErrorCode.ADD_ERROR, "当前用户已经在队伍中");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 解锁
            countUserJoinNumLock.unlock();
        }
        // 判断队伍是否已经满了
        // 针对队伍获取锁
        RLock teamLock = redissonClient.getLock(RedisConstant.REDIS_TEAM_JOINER_COUNT_LOCK + teamId);
        while (true){
            try {
                boolean isLock = teamLock.tryLock(0, -1, TimeUnit.MILLISECONDS);
                if (!isLock) {
                    continue;
                }
                long teamUserCount = this.countTeamUserByTeamId(teamId);
                if (teamUserCount >= team.getMaxNum()) {
                    throw new BusinessException(ErrorCode.ADD_ERROR, "当前队伍已满");
                }
                // 加入队伍
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(loginUser.getId());
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                // todo  真正加入到队伍，加锁，和前面的一起，判断两个条件，加入的队伍最多时5个，并且不能反复加入队伍
                // 粒度分析：
                /**
                 * 1. 判断当前用户加入了几个队伍，锁：hasJoinCount:userId:lock
                 * 2. 判断当前用户是否加入了当前队伍：hasJoinTeam:userId:lock
                 * 3. 同时获取到这两把锁的才能加入队伍，如果没有获取到这两把锁，就直接退出
                 * 4. 死锁问题，如果两个线程各获取到一个锁，- 解决：保持线程对锁的获取顺序，先获取到第一把锁后再获取另一把锁
                 * 5. 当由多个用户同时加入到一个队伍中的时候，会导致加入的队伍过多，又需要对team加锁: teamJoinerCount:teamId:lock
                 * 6. 当获取到3把锁的时候，才能够加入队伍
                 * 多种解决方案，1. 利用秒杀的问题，让sql语句多判断一个条件，count是否大于队伍上限（简单）； 2.加锁
                 * 加锁解决方案：
                 * a. 先获取到前两把锁，如果没有获取到，就直接返回，
                 * b. 只有获取到这个前两把锁的人才能去执行后面的操作
                 * c. 获取teamId 的锁，获取成功，插入元素，然后释放锁
                 * d. 释放前两把锁
                 */
                boolean result = userTeamService.save(userTeam);
                if (!result) {
                    // 加入失败
                    throw new BusinessException(ErrorCode.ADD_ERROR);
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                teamLock.unlock();
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(DeleteTeamRequest deleteTeamRequest, User loginUser) {
        if(deleteTeamRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long teamId = deleteTeamRequest.getId();
        Team team = this.getTeamById(teamId);
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 移除信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(queryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.DELETE_ERROR);
        }
        result = this.removeById(teamId);
        if (!result) {
            throw new BusinessException(ErrorCode.DELETE_ERROR);
        }
        return true;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin, HttpServletRequest request) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();

        if(teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            String searchText = teamQuery.getSearchText();
            if ("".equals(searchText) || searchText == null) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description" ,description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            Integer status = teamQuery.getStatus();
            TeamStatusEnum teamStatusEnum = Optional.ofNullable(TeamStatusEnum.getEnumByValue(status)).orElse(TeamStatusEnum.PUBLIC);
            if (!isAdmin && teamStatusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            if (!(isAdmin && teamQuery.getStatus() == null)) {
                queryWrapper.eq("status", teamStatusEnum.getValue());
            }

        } else {
            if (!isAdmin) {
                queryWrapper.eq("status", TeamStatusEnum.PUBLIC.getValue());
            }
        }
        queryWrapper.and(qw -> {
            qw.gt("expireTime", new Date()).or().isNull("expireTime");
        });


        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
             return new ArrayList<>();
        }
        // 没有结果返回空
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            // 判断当前用户是否加入了此队伍
            QueryWrapper<UserTeam> countQueryWrapper = new QueryWrapper<>();
            countQueryWrapper.eq("userId", userService.getLoginUser(request).getId());
            countQueryWrapper.eq("teamId", team.getId());
            long ifJoin = userTeamService.count(countQueryWrapper);
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            // 继续查询,获取用户信息
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            UserVO userVO = new UserVO();
            try {
                BeanUtils.copyProperties(teamUserVO, team);
                if (user != null) {
                    BeanUtils.copyProperties(userVO, user);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            teamUserVO.setCreateUser(userVO);
            teamUserVOList.add(teamUserVO);
            teamUserVO.setHasJoin(ifJoin == 1);
            // 判断当前队伍的用户数目
            teamUserVO.setHasJoinNum(countTeamUserByTeamId(team.getId()));
        }
        return teamUserVOList;
    }

    @Override
    public List<TeamUserVO> listTeamsBySql(TeamQuery teamQuery, boolean isAdmin) {
        // todo sql关联查询
        return null;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getTeamById(id);
        if (!oldTeam.getUserId().equals(loginUser.getId()) && userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //将长度为0的字符串属性设置为null 防止出现sql语句构造错误
        if (StringUtils.isBlank(teamUpdateRequest.getName())) teamUpdateRequest.setName(null);
        if (StringUtils.isBlank(teamUpdateRequest.getDescription())) teamUpdateRequest.setDescription(null);
        if (StringUtils.isBlank(teamUpdateRequest.getPassword())) teamUpdateRequest.setPassword(null);
        // 1. 不传入status
        // 2. 传入status但是是非法的 -- 都不改变状态
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (teamStatusEnum == null) {
            teamUpdateRequest.setStatus(null);
        } else {
            // 3. 传入public的状态 -- 设置状态，不做操作
            // 4. 传入私密的状态 -- 同上 -- 将密码置空
            if (teamStatusEnum.equals(TeamStatusEnum.PRIVATE) || teamStatusEnum.equals(TeamStatusEnum.PUBLIC)) {
                teamUpdateRequest.setPassword(null);
            } else if (teamStatusEnum.equals(TeamStatusEnum.SECRET)) {
                // 5. 传入加密状态 -- 如果此时密码是非空的需要加上密码
                if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                    // 提示传入密码
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "设置为私密需要传入密码");
                }
            } else {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamUpdateRequest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this.updateById(team);
    }

    @Override
    public List<TeamUserVO> listMyTeams(Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", id);
        return this.list(queryWrapper).stream().map(team -> {
            TeamUserVO teamUserVO = new TeamUserVO();
            teamUserVO.setHasJoin(true);
            User loginUser = userService.getLoginUser(request);
            UserVO createUser = new UserVO();
            try {
                BeanUtils.copyProperties(teamUserVO, team);
                BeanUtils.copyProperties(createUser, loginUser);
                teamUserVO.setCreateUser(createUser);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            // 判断当前队伍的用户数目
            teamUserVO.setHasJoinNum(countTeamUserByTeamId(team.getId()));
            return teamUserVO;
        }).collect(Collectors.toList());
    }

    /**
     * 判断某个队伍已经加入的人数
     * @param teamId
     * @return
     */
    private int countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        long teamUserNum = userTeamService.count(userTeamQueryWrapper);
        return (int) teamUserNum;
    }

    /**
     * 获取team
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在team
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        return team;
    }



}




