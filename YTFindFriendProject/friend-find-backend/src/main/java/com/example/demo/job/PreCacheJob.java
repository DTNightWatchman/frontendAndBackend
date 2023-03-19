package com.example.demo.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.constant.UserConstant;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.domain.User;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;


    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserMapper userMapper;

    // 使用了多进程，这里的数据多了，就需要
    private List<Long> mainUserList = Arrays.asList(1L, 2L, 4L);

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 100, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10));

    /**
     * 加载预热推荐用户
     */
    @Scheduled(cron = "0 20 18 * * *")
    public void doCacheRecommendUser() {

        // redisson给的一个可重入锁
        RLock rLock = redissonClient.getLock(UserConstant.REDIS_PRECACHE_JOB_DO_CACHE_LOCK);
        try {
            // 此时使用看门狗机制，这里的l1必须使用-1
            // 这里会每10秒续期
            if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                log.info("getLock" + Thread.currentThread().getId());

                for (Long userId : mainUserList) {
                    // 给重点用户添加缓存
                    threadPoolExecutor.execute(() -> {
                        String redisKey = String.format("findFriend:user:recommend:%s", userId.longValue());
                        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                        Page<User> userPage = userService.page(new Page<>(0, 20), queryWrapper);
                        try {
                            valueOperations.set(redisKey, userPage, UserConstant.REDIS_RECOMMEND_USER_TIME_OUT, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            log.error("缓存写入失败");
                        }
                    });
                }
                threadPoolExecutor.shutdown();
                // 判断是否结果线程池
                while (threadPoolExecutor.isTerminated()){};
                log.info("cache job is over");

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 释放锁 只能释放自己线程加的锁
            if(rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                System.out.println("解锁");
            }
            log.info("unLock" + Thread.currentThread().getId());
        }
    }

}
