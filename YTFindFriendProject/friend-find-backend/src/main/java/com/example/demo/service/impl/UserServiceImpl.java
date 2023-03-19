package com.example.demo.service.impl;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.common.BaseResponse;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.ResultUtil;
import com.example.demo.constant.UserConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.domain.User;
import com.example.demo.model.vo.UserVO;
import com.example.demo.service.UserService;
import com.example.demo.mapper.UserMapper;
import com.example.demo.utils.AlgorithmUtils;
import com.example.demo.utils.PasswordUtil;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import io.swagger.models.auth.In;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.log4j.Log4j;
import org.apache.catalina.security.SecurityUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Resource
    private UserMapper userMapper;


    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }

        // 账户不能包含特殊字符
        String validPattern = "[ _`!@#$%^&*()+=|{}’:;’,.<>/?！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        //String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 校验两个密码相同
        if(!userPassword.equals(checkPassword)) {
            return -1;
        }

        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 插入用户
        User user = new User();
        user.setUsername("");
        user.setUserAccount(userAccount);
        user.setPassword(PasswordUtil.encrypt(userPassword));
        user.setUserRole(0);
        boolean ret = this.save(user);
        if (!ret) {
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            return null;
        }
        if (!PasswordUtil.comparePassword(password, user.getPassword())) {
            return null;
        }
        // 用户脱敏，并放到session中
        User userInfo = getUserInfo(user);
        //放置userInfo
        HttpSession session = request.getSession(true);
        session.setAttribute(UserConstant.USER_INFO, userInfo);
        userInfo = (User) request.getSession().getAttribute(UserConstant.USER_INFO);
        return userInfo;
    }

    /**
     * 用户脱敏
     * @param user
     * @return 脱敏后的用户
     */

    @Override
    public User getUserInfo(User user) {
        if (user == null) {
            return null;
        }
        User userInfo = new User();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setUserAccount(user.getUserAccount());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setGender(user.getGender());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        userInfo.setUserStatus(user.getUserStatus());
        userInfo.setCreateTime(user.getCreateTime());
        userInfo.setIsDelete(user.getIsDelete());
        userInfo.setUserRole(user.getUserRole());
        userInfo.setPlanetCode(user.getPlanetCode());
        userInfo.setTags(user.getTags());

        return userInfo;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return 0;
    }

    @Override
    public List<User> searchUsersByTagsByMem(List<String> tagList) {
        if(CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        List<User> res = userList.parallelStream().filter(user -> {
            String tagsString = user.getTags();
            if (StringUtils.isBlank(tagsString)) {
                return false;
            }
            Set<String> tagNameSet = gson.fromJson(tagsString, new TypeToken<Set<String>>() {}.getType());
            tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
            for (String tag : tagList) {
                if (!tagNameSet.contains(tag)) {
                    return false;
                }
            }
            return true;
        }).map(this::getUserInfo).collect(Collectors.toList());
        return res;
    }

    //在数据库中直接扫描判断
    @Override
    public List<User> searchUsersByTags(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接add
        for (String tag : tagList) {
            queryWrapper = queryWrapper.like("tags", tag);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        // 脱敏
        List<User> res = new ArrayList<>();
        userList.forEach(user -> {
            User userInfo = this.getUserInfo(user);
            res.add(userInfo);
        });
        return res;
    }

    /**
     * 更新用户信息
     * @param user
     * @param loginUser
     * @return
     */
    @Override
    public int updateUser(User user, User loginUser) {
        Long userId = user.getId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 仅管理员和自己可以修改
        // 如果是管理员，允许修改所有用户
        // 如果不是管理员，只允许修改自己的信息
        if (!isAdmin(loginUser) && !userId.equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }


    /**
     * 根据request获取当前的user
     * @param request
     * @return
     */
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        if(request.getSession() == null) {
            return null;
        }
        User userInfo = (User) request.getSession().getAttribute(UserConstant.USER_INFO);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return userInfo;
    }

    /**
     * 判断是不是管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User userInfo = (User) session.getAttribute(UserConstant.USER_INFO);

        if (userInfo == null || userInfo.getUserRole() != UserConstant.ADMIN_USER) {
            return false;
        }
        return true;
    }

    /**
     * 重构方法，根据user判断是不是管理员
     * @param loginUser
     * @return
     */
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_USER;
    }

    @Override
    public Page<User> getRecommendUsers(long pageNum, long pageSize,HttpServletRequest request) {
        User userInfo = getLoginUser(request);
        String redisKey = String.format(UserConstant.REDIS_PRE_STRING + userInfo.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存，不操作数据库
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return userPage;
        }
        // 无缓存
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = page(new Page<>(pageNum, pageSize), queryWrapper);
        // 获取到page之后，写入缓存
        try {
            // 指定过期时间，防止一直读的都是缓存中的数据
            valueOperations.set(redisKey, userPage, UserConstant.REDIS_RECOMMEND_USER_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 防止因为写入缓存失败而无法正确返回响应结果
            log.error("redis set key error", e);
        }
        return userPage;
    }


    @Override
    public Page<User> getRecommendUsers(long pageNum, long pageSize,User loginUser) {
        User userInfo = this.getUserInfo(loginUser);
        String redisKey = String.format(UserConstant.REDIS_PRE_STRING + userInfo.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        // 如果有缓存，直接读缓存，不操作数据库
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return userPage;
        }
        // 无缓存
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = page(new Page<>(pageNum, pageSize), queryWrapper);
        // 获取到page之后，写入缓存
        try {
            // 指定过期时间，防止一直读的都是缓存中的数据
            valueOperations.set(redisKey, userPage, UserConstant.REDIS_RECOMMEND_USER_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 防止因为写入缓存失败而无法正确返回响应结果
            log.error("redis set key error", e);
        }
        return userPage;
    }

    @Override
    public List<UserVO> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(qw -> qw.isNotNull("tags").or().ne("tags","[]"));
        queryWrapper.select("id", "tags");
        List<User> list = this.list(queryWrapper);
        List<UserVO> result = new ArrayList<>();
        SortedMap<Integer, Integer> indexDistanceMap = new TreeMap<>((a,b) -> a - b);
        String tags = loginUser.getTags();
        if (StringUtils.isBlank(tags) || "[]".equals(tags)) {
            Page<User> userPage = this.getRecommendUsers(1, 20, loginUser);
            List<User> userList = userPage.getRecords();
            result = userList.stream().map(user -> {
                UserVO userVO = new UserVO();
                try {
                    BeanUtils.copyProperties(userVO, user);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return userVO;
            }).collect(Collectors.toList());
        } else {
            Gson gson = new Gson();
            List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
            }.getType());
            List<Pair<User, Integer>> list1 = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                String userTags = list.get(i).getTags();
                if (StringUtils.isBlank(userTags) || "[]".equals(userTags)
                        || loginUser.getId().equals(list.get(i).getId())) {
                    continue;
                }
                List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
                }.getType());
                int distance = AlgorithmUtils.minDistance(tagList, userTagList);
                indexDistanceMap.put(i, distance);
                list1.add(new Pair<>(list.get(i), distance));
            }
            List<Pair<User, Integer>> topUserPairList = list1.stream().sorted((a, b) -> a.getValue() - b.getValue()).limit(num).collect(Collectors.toList());
            List<Long> idList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            String joinStr = StrUtil.join(",", idList);
            userQueryWrapper.in("id", idList).last("order by field (id," + joinStr + ")");
            result = this.list(userQueryWrapper).stream().map(user -> {
                UserVO userVO = new UserVO();
                try {
                    BeanUtils.copyProperties(userVO, user);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return userVO;
            }).collect(Collectors.toList());
        }
        return result;
    }


}




