package com.wanglei.mybibackend.model.dto;

import com.wanglei.mybibackend.commmon.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * id 列表
     */
    private List<Long> idList;

    /**
     * 搜索关键词（同时对队伍名称和描述查询）
     */
    private String searchText = "";

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
