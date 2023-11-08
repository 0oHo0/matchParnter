package com.duu.matchPartner.job; /**
 * @author : duu
 * @data : 2023/11/2
 * @from ：https://github.com/0oHo0
 **/

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.duu.matchPartner.model.domain.User;
import com.duu.matchPartner.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: matchPartner-backend
 * @BelongsPackage: com.duu.matchPartner.job
 * @Author: duu
 * @CreateTime: 2023-11-02  17:54
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;
    
    @Resource
    private RedisTemplate redisTemplate;
    
    @Resource
    RedissonClient redissonClient;

    @Scheduled(cron = "0 30 0 * * *")
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("duu-matchPartner-recommendUser:lock");
        try {
            if(lock.tryLock(0,-1, TimeUnit.MILLISECONDS)){
                QueryWrapper queryWrapper = new QueryWrapper<>();
                List<User> userlist = userService.list(queryWrapper);
                for(User user : userlist){
                    String key = String.format("duu-matchPartner-recommendUser:%s", user.getId());
                    ValueOperations valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(key,user,1,TimeUnit.DAYS);
                    }catch (Exception e){
                        log.error("缓存推荐人失败",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        }finally {
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}   
