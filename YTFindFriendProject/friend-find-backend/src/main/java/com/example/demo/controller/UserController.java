package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.BaseResponse;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.ResultUtil;
import com.example.demo.constant.UserConstant;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.request.UserLoginRequest;
import com.example.demo.model.domain.User;
import com.example.demo.model.request.UserRegisterRequest;
import com.example.demo.model.vo.UserVO;
import com.example.demo.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: YT
 * @Description: 用户操纵
 * @DateTime: 2022/9/27$ - 15:29
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserServiceImpl userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword));

        long id = userService.userRegister(userAccount, userPassword, checkPassword);
        //return new BaseResponse<Long>(0, id, "ok");
        return ResultUtil.success(id);
    }


//    @PostMapping("/login")
//    public Map<String, Object> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
//        if (userLoginRequest == null) {
//            return null;
//        }
//        String userAccount = userLoginRequest.getUserAccount();
//        String userPassword = userLoginRequest.getUserPassword();
//        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
//            return null;
//        }
//        User user = userService.userLogin(userAccount, userPassword, request);
//        Map<String, Object> res = new HashMap<>();
//        res.put("status", "ok");
//        res.put("currentUser", user);
//        return res;
//    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        //return new BaseResponse<>(0, user, "ok");
        return ResultUtil.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogin(HttpServletRequest request, HttpServletResponse response) {
        if (request == null) {
            return null;
        }
        Integer ret = userService.userLogout(request);
        return ResultUtil.success(ret);
    }

    @RequestMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> users = userService.list(queryWrapper);
        List<User> res = new ArrayList<>();
        users.stream().forEach((user) -> {
            User userInfo = userService.getUserInfo(user);
            res.add(userInfo);
        });
        return ResultUtil.success(res);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id < 0) {
            return null;
        }
        Boolean b = userService.removeById(id);
        return ResultUtil.success(b);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        User user = (User) session.getAttribute(UserConstant.USER_INFO);
        User currentUser = userService.getById(user.getId());
        User safeUser = userService.getUserInfo(currentUser);
        return ResultUtil.success(safeUser);
    }

    /**
     * 获取推荐用户
     * @param pageSize
     * @param pageNum
     * @param request
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {
        Page<User> userPage = userService.getRecommendUsers(pageNum, pageSize, request);
        return ResultUtil.success(userPage);
    }

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        List<User> userList = userService.searchUsersByTagsByMem(tagNameList);
        return ResultUtil.success(userList);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验权限,这个时候返回的loginUser一定是含值的，否则getLoginUser会报错
        User loginUser = userService.getLoginUser(request);

        int result = userService.updateUser(user, loginUser);
        // 判断是不是管理员
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //int res = userService.updateUser(user);
        //return ResultUtil.success(res);
        return ResultUtil.success(result);
    }



    @GetMapping("/match")
    public BaseResponse<List<UserVO>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        List<UserVO> result = userService.matchUsers(num, loginUser);
        return ResultUtil.success(result);
    }
}
