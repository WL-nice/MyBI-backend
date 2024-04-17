package com.wanglei.mybibackend.commmon;

import com.wanglei.mybibackend.constant.CommonConstant;
import lombok.Data;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest {
    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前第几页
     */
    protected int current = 1;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;

    /**
     * 排序字段
     */
    private String sortField;
}
