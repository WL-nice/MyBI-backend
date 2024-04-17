package com.wanglei.mybibackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanglei.mybibackend.model.domain.Chart;
import com.wanglei.mybibackend.model.request.chart.ChartQueryRequest;


/**
* @author admin
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-04-17 19:32:21
*/
public interface ChartService extends IService<Chart> {

    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);
}
