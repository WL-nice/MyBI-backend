package com.wanglei.mybibackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanglei.mybibackend.model.domain.User;
import com.wanglei.mybibackend.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;
    private List<Long> mainUser = Arrays.asList(1L);

    @Scheduled(cron = "0 0 0 * * *")
    public void doCache() {
        RLock lock = redissonClient.getLock("bromatch:PreCacheJob:doCache:lock");
        try {
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                for (Long userId : mainUser) {
                    String redisKey = String.format("bromatch:recommend:%s", userId);
                    ValueOperations<String, Object> ValueOperations = redisTemplate.opsForValue();
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    //写缓存
                    try {
                        ValueOperations.set(redisKey, userPage, 5, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);

                    }
                }

            }
        } catch (InterruptedException e) {
            log.error("doCache error", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }
}
