package com.wanglei.mybibackend.model.request.chart;

import com.wanglei.mybibackend.commmon.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;


    private String goal;


    /**
     * 图表类型
     */
    private String chartType;

    private String name;


    /**
     * 创建用户 id
     */
    private Long userId;



}
