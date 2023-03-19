package com.example.demo.model.request;

import lombok.Data;

/**
 * @Author: YT
 * @Description: 登录的
 * @DateTime: 2022/9/27$ - 15:30
 */
@Data
public class UserLoginRequest {

    private String userAccount;
    private String userPassword;
}
