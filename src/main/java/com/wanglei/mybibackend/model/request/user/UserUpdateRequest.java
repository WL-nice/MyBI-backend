package com.wanglei.mybibackend.model.request.user;

import lombok.Data;


@Data
public class UserUpdateRequest {
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
     * 密码
     */
    private String userPassword;

}
