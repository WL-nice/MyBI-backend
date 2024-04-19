package com.wanglei.mybibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanglei.mybibackend.commmon.BaseResponse;
import com.wanglei.mybibackend.commmon.ErrorCode;
import com.wanglei.mybibackend.commmon.ResultUtils;
import com.wanglei.mybibackend.exception.BusinessException;
import com.wanglei.mybibackend.model.domain.User;
import com.wanglei.mybibackend.model.request.user.UserLoginRequest;
import com.wanglei.mybibackend.model.request.user.UserQueryRequest;
import com.wanglei.mybibackend.model.request.user.UserRegisterRequest;
import com.wanglei.mybibackend.model.request.user.UserUpdateRequest;
import com.wanglei.mybibackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import static com.wanglei.mybibackend.constant.UserConstant.USER_LOGIN_STATE;

@RestController //适用于编写restful风格的API，返回值默认为json类型
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.UserRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);

    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = userService.doLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);

    }

    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long id = currentUser.getId();

        User user = userService.getById(id);
        User safetyuser = userService.getSafetUser(user);
        return ResultUtils.success(safetyuser);

    }

    /**
     * 查询获取用户（分页）
     */
    @PostMapping("list/page")
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(userQueryRequest);
        Page<User> userPage = userService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userPage);
    }



    /**
     * 全量查询用户
     *
     * @param pageSize 每页数据量
     * @param pageNum  当前页数
     */
    @GetMapping("/search")
    public BaseResponse<Page<User>> userSearch(long pageSize, long pageNum, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        //如果有缓存，直接读缓存
        String redisKey = String.format("mybi:search:%s", loginUser.getId());
        ValueOperations<String, Object> ValueOperations = redisTemplate.opsForValue();
        Page<User> userPage = (Page<User>) ValueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        //无缓存查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        //写缓存
        try {
            ValueOperations.set(redisKey, userPage, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return ResultUtils.success(userPage);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = userService.updateUser(userUpdateRequest, loginUser);
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
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }





}
