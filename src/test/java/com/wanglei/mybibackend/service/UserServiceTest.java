package com.wanglei.mybibackend.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 */
@SpringBootTest
@Slf4j
class UserServiceTest {

    @Resource
    private UserService userservice;

    @Test
    void testAdduser() {


    }

    @Test
    void userRegister() {
        String userAccount = "muqiu";
        String userPasward = "12345678";
        String checkPassword = "12345678";
        String acptCode = "1212";
        long result = userservice.UserRegister(userAccount, userPasward, checkPassword);
        //Assertions.assertEquals(-1, result);

//        userPasward = "12345678";
//        userAccount = "mu";
//        result = userservice.UserRegister(userAccount, userPasward, checkPassword,acptCode);
//        Assertions.assertEquals(-1, result);
//
//        userAccount = "muqiu";
//        userPasward = "123456";
//        result = userservice.UserRegister(userAccount, userPasward, checkPassword,acptCode);
//        Assertions.assertEquals(-1, result);
//
//
//        userAccount = "mu qiu";
//        result = userservice.UserRegister(userAccount, userPasward, checkPassword,acptCode);
//        Assertions.assertEquals(-1, result);
//
//        userPasward = "12345678";
//        checkPassword = "123456";
//        result = userservice.UserRegister(userAccount, userPasward, checkPassword,acptCode);
//        Assertions.assertEquals(-1, result);
//
//        checkPassword = "12345678";
//        userAccount = "123";
//        result = userservice.UserRegister(userAccount, userPasward, checkPassword,acptCode);
//        Assertions.assertEquals(-1, result);
//
//        userAccount = "yuyu";
//        userPasward = "12345678";
//        checkPassword = "12345678";
//        result = userservice.UserRegister(userAccount, userPasward, checkPassword,acptCode);
        //Assertions.assertEquals(-1, result);


    }

    @Test
    void testSearchUserservice(){

    }


}