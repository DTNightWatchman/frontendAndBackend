package com.example.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.BaseResponse;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.request.TeamQuitRequest;
import com.example.demo.model.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 对比密码
     * @return 新用户id
     */
    public long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param password 用户密码
     * @return 用户信息（脱敏）
     */
    public User userLogin(String userAccount, String password, HttpServletRequest request);

    /**
     * 获取用户脱敏信息
     * @param user
     * @return user信息，脱敏
     */
    User getUserInfo(User user);


    /**
     * 用户退出登录
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    List<User> searchUsersByTags(List<String> tagList);


    public List<User> searchUsersByTagsByMem(List<String> tagList);

    int updateUser(User user, User userInfo);

    /**
     * 获取当前登录的用户的信息
     * @return user
     */
    public User getLoginUser(HttpServletRequest request);


    /**
     * 根据request判断用户是否是admin
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);


    /**
     * 根据user判断用户是否是admin
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 获取推荐用户并写入缓存
     * @param request
     * @return
     */
    Page<User> getRecommendUsers(long pageNum, long pageSize,HttpServletRequest request);

    /**
     * 取推荐用户并写入缓存
     * @param pageNum
     * @param pageSize
     * @param loginUser
     * @return
     */
    public Page<User> getRecommendUsers(long pageNum, long pageSize,User loginUser);

    /**
     * 获取当前用户相似的标签用户
     * @param num
     * @param loginUser
     * @return
     */
    List<UserVO> matchUsers(long num, User loginUser);


    /**
     * 修改用户头像
     * @param file
     * @return
     */
    String updateUserAvatar(MultipartFile file, HttpServletRequest request);
}
