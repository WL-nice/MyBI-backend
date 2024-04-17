package com.wanglei.mybibackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UserVo implements Serializable {
    /**
     * 用户id
     */

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 创建时间
     */
    private Date createTime;



    /**
     * 用户状态
     */
    private Integer userStatus;


    /**
     * 用户身份
     */
    private Integer userRole;


    private static final long serialVersionUID = 1L;

}
