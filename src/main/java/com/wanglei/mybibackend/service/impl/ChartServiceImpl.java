package com.wanglei.mybibackend.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.wanglei.mybibackend.commmon.ErrorCode;
import com.wanglei.mybibackend.constant.CommonConstant;
import com.wanglei.mybibackend.exception.BusinessException;
import com.wanglei.mybibackend.mapper.ChartMapper;
import com.wanglei.mybibackend.model.domain.Chart;
import com.wanglei.mybibackend.model.request.chart.ChartQueryRequest;
import com.wanglei.mybibackend.service.ChartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author admin
 * @description 针对表【chart(图表信息表)】的数据库操作Service实现
 * @createDate 2024-04-17 19:32:21
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
        implements ChartService {

    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String chartType = chartQueryRequest.getChartType();
        String status = chartQueryRequest.getStatus();
        Long userId = chartQueryRequest.getUserId();


        long size = chartQueryRequest.getPageSize();
        String sortOrder = chartQueryRequest.getSortOrder();
        String sortField = chartQueryRequest.getSortField();
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "title", name);
        queryWrapper.like(StringUtils.isNotBlank(chartType), "content", chartType);
        queryWrapper.like(StringUtils.isNotBlank(status), "answer", status);

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);

        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }
}




