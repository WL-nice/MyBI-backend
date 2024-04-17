package com.wanglei.mybibackend.model.request.chart;

import com.wanglei.mybibackend.commmon.PageRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ChartQueryRequest extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 图表名称
     */
    private String name;


    /**
     * 图表类型
     */
    private String chartType;

    /**
     * wait,running,succeed,failed
     */
    private String status;

    /**
     * 执行信息
     */
    private String execMessage;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;


}
