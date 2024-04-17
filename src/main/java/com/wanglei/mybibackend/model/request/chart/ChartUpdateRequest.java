package com.wanglei.mybibackend.model.request.chart;


import lombok.Data;



@Data
public class ChartUpdateRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 图表数据
     */
    private String chartData;


    /**
     * 生成的分析结论
     */
    private String genResult;

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

}
