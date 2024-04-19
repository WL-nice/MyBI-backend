package com.wanglei.mybibackend.model.request.chart;



import lombok.Data;



@Data
public class ChartAddRequest {

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
     * 图表类型
     */
    private String chartType;

}
