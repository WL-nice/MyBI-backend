package com.wanglei.mybibackend.model.vo;



import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍和用户信息封装类
 */
@Data
public class TeamUserVo implements Serializable {
    /**
     * id
     */

    private Long id;

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
     * 队伍成员最大数
     */
    private Integer maxNum;

    /**
     * 队伍状态
     */
    private Integer teamStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 1L;

    /**
     * 创建人信息
     */
    UserVo createUser;

    /**
     * 是否已加入
     */
    private boolean hasJoin;

    /**
     * 已加入用户数
     */
    private Integer hasJoinNum;


}
