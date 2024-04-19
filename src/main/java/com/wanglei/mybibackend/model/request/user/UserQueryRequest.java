package com.wanglei.mybibackend.model.request.user;


import com.wanglei.mybibackend.commmon.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest {
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


}
