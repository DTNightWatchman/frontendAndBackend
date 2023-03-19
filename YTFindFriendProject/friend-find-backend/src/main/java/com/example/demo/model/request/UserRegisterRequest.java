package com.example.demo.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: YT
 * @Description: 用户注册请求体
 * @DateTime: 2022/9/27$ - 16:54
 */
@Data
public class UserRegisterRequest implements Serializable {

    private String userAccount;

    private String userPassword;

    private String checkPassword;

}
