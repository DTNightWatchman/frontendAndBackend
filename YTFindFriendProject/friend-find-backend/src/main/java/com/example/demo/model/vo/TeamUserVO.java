package com.example.demo.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.example.demo.model.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍和用户信息封装类
 */
@Data
public class TeamUserVO implements Serializable {
    private static final long serialVersionUID = 7608321771172779778L;
    /**
     * id
     */
    private Long id;

    /**
     * 队伍信息
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开, 1 - 私有, 2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人信息
     */
    private UserVO createUser;

    /**
     * 加入的队伍的用户数目
     */
    private Integer hasJoinNum;


    /**
     * 加入人的信息
     */
    private List<UserVO> userList;

    /**
     * 此用户是否加入队伍
     */
    private boolean hasJoin;
}
