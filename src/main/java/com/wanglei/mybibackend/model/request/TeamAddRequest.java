package com.wanglei.mybibackend.model.request;


import lombok.Data;

import java.util.Date;
@Data
public class TeamAddRequest {

    /**
     * 队伍名
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 密码
     */
    private String password;

    /**
     * 队伍成员最大数
     */
    private Integer maxNum;

    /**
     * 队伍状态
     */
    private Integer teamStatus;





}
