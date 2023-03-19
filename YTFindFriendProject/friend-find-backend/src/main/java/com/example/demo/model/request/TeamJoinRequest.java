package com.example.demo.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 7110736150311995658L;
    /**
     * 队伍Id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
