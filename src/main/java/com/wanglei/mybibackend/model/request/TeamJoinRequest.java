package com.wanglei.mybibackend.model.request;


import lombok.Data;



@Data
public class TeamJoinRequest {
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;



}


