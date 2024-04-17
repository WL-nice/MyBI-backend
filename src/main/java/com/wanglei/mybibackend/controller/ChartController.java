package com.wanglei.mybibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanglei.mybibackend.commmon.BaseResponse;
import com.wanglei.mybibackend.commmon.ErrorCode;
import com.wanglei.mybibackend.commmon.ResultUtils;
import com.wanglei.mybibackend.exception.BusinessException;
import com.wanglei.mybibackend.model.domain.Chart;
import com.wanglei.mybibackend.model.domain.User;
import com.wanglei.mybibackend.model.request.chart.ChartQueryRequest;
import com.wanglei.mybibackend.model.request.chart.ChartUpdateRequest;
import com.wanglei.mybibackend.service.ChartService;
import com.wanglei.mybibackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {
    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;


    @PostMapping("/add")
    public BaseResponse<Boolean> addTeam(@RequestBody Chart chart, HttpServletRequest request) {
        if (chart == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        boolean result = chartService.save(chart);

        return ResultUtils.success(result);

    }


    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody ChartUpdateRequest chartUpdateRequest, HttpServletRequest request) {
        if (chartUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart byId = chartService.getById(chartUpdateRequest.getId());
        if (byId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "图表不存在");
        }
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 用户删除
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestParam("id") long id, HttpServletRequest request) {
        //仅管理员可查询
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean result = chartService.removeById(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listQuestionByPage(@RequestBody ChartQueryRequest chartQueryRequest, HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        QueryWrapper<Chart> queryWrapper = chartService.getQueryWrapper(chartQueryRequest);
        Page<Chart> questionPage = chartService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(questionPage);
    }
}
