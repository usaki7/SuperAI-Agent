package com.yu.histoaiagent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yu.histoaiagent.entity.SysUser;
import com.yu.histoaiagent.entity.UserInfo;
import com.yu.histoaiagent.entity.UserQuotaUsage;
import com.yu.histoaiagent.enums.UserRole;
import com.yu.histoaiagent.mapper.SysUserMapper;
import com.yu.histoaiagent.mapper.UserQuotaUsageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 用户权限服务
 * 负责用户信息管理、配额管理等
 */
@Service
@Slf4j
public class UserPermissionService {
    
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UserQuotaUsageMapper quotaUsageMapper;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USAGE_KEY_PREFIX = "user:usage:";
    private static final String CONV_MSG_COUNT_PREFIX = "conversation:message:count:";

    /**
     * 获取用户信息
     */
    public UserInfo getUserInfo(String userId) {
        log.debug("Getting user info from database: {}", userId);

        // 1. 从数据库查询用户基本信息
        SysUser sysUser = sysUserMapper.findByUserId(userId);
        if (sysUser == null) {
            log.warn("User not found: {}", userId);
            return null;
        }

        // 2. 获取今日使用次数（优先从Redis，其次从数据库）
        Integer todayUsage = getTodayUsageCount(userId);

        // 3. 转换为UserInfo
        UserInfo userInfo = UserInfo.builder()
                .userId(sysUser.getUserId())
                .username(sysUser.getUsername())
                .role(sysUser.getRoleEnum())
                .enabled(sysUser.getEnabled())
                .todayUsageCount(todayUsage)
                .conversationMessageCount(0) // 会在需要时单独查询
                .vipExpireTime(sysUser.getVipExpireTime())
                .trialExpireTime(sysUser.getTrialExpireTime())
                .build();

        log.debug("User info loaded: {}, role: {}, today usage: {}",
                userId, userInfo.getRole().getDesc(), todayUsage);

        return userInfo;
    }

    /**
     * 获取今日使用次数
     * 优先从Redis获取，Redis没有则从MySQL获取并回填Redis
     */
    public Integer getTodayUsageCount(String userId) {
        LocalDate today = LocalDate.now();

        // 1. 尝试从Redis获取
        if (redisTemplate != null) {
            String key = getUsageKey(userId, today);
            Object count = redisTemplate.opsForValue().get(key);
            if (count != null) {
                log.debug("Usage count from Redis: {} = {}", userId, count);
                return ((Number) count).intValue();
            }
        }

        // 2. 从数据库获取
        Integer count = quotaUsageMapper.findTodayUsage(userId, today);
        if (count == null) {
            count = 0;
        }

        // 3. 回填到Redis
        if (redisTemplate != null && count > 0) {
            String key = getUsageKey(userId, today);
            redisTemplate.opsForValue().set(key, count);
            long secondsUntilMidnight = getSecondsUntilMidnight();
            redisTemplate.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
        }

        log.debug("Usage count from DB: {} = {}", userId, count);
        return count;
    }

    /**
     * 增加使用次数
     * 同时更新Redis和MySQL
     */
    @Transactional
    public void incrementUsage(String userId) {
        LocalDate today = LocalDate.now();

        // 1. 获取用户信息以确定配额限制
        UserInfo userInfo = getUserInfo(userId);
        if (userInfo == null) {
            log.warn("User not found when incrementing usage: {}", userId);
            return;
        }

        // 2. 增加Redis计数
        Long newCount = null;
        if (redisTemplate != null) {
            String key = getUsageKey(userId, today);
            newCount = redisTemplate.opsForValue().increment(key);

            // 设置过期时间（第一次创建时）
            if (newCount == 1) {
                long secondsUntilMidnight = getSecondsUntilMidnight();
                redisTemplate.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
            }
        }

        // 3. 更新MySQL（异步或同步都可以）
        try {
            UserQuotaUsage usage = UserQuotaUsage.builder()
                    .userId(userId)
                    .usageDate(today)
                    .usageCount(1) // 增加1次
                    .quotaLimit(userInfo.getRole().getDailyQuota())
                    .build();

            quotaUsageMapper.insertOrUpdateUsage(usage);

            log.debug("User {} usage incremented to {}", userId, newCount);
        } catch (Exception e) {
            log.error("Failed to update quota usage in DB for user: {}", userId, e);
        }
    }

    /**
     * 重置用户配额
     */
    @Transactional
    public void resetQuota(String userId) {
        LocalDate today = LocalDate.now();

        // 1. 删除Redis缓存
        if (redisTemplate != null) {
            String key = getUsageKey(userId, today);
            redisTemplate.delete(key);
        }

        // 2. 删除或重置MySQL记录
        QueryWrapper<UserQuotaUsage> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("usage_date", today);
        quotaUsageMapper.delete(wrapper);

        log.info("Reset quota for user: {}", userId);
    }

    /**
     * 升级用户为VIP
     */
    @Transactional
    public void upgradeToVip(String userId, int days) {
        LocalDateTime expireTime = LocalDateTime.now().plusDays(days);

        // 1. 更新角色
        sysUserMapper.updateRole(userId, "VIP");

        // 2. 更新过期时间
        sysUserMapper.updateVipExpireTime(userId, expireTime);

        log.info("Upgraded user {} to VIP for {} days, expire at {}",
                userId, days, expireTime);
    }

    /**
     * 禁用用户
     */
    @Transactional
    public void disableUser(String userId) {
        sysUserMapper.updateEnabledStatus(userId, false);
        log.info("Disabled user: {}", userId);
    }

    /**
     * 启用用户
     */
    @Transactional
    public void enableUser(String userId) {
        sysUserMapper.updateEnabledStatus(userId, true);
        log.info("Enabled user: {}", userId);
    }

    /**
     * 获取会话消息数
     */
    public Integer getConversationMessageCount(String conversationId) {
        if (redisTemplate == null) {
            return 0;
        }

        String key = CONV_MSG_COUNT_PREFIX + conversationId;
        Object count = redisTemplate.opsForValue().get(key);
        return count != null ? ((Number) count).intValue() : 0;
    }

    /**
     * 增加会话消息数
     */
    public void incrementConversationMessageCount(String userId, String conversationId, int count) {
        if (redisTemplate == null) {
            return;
        }

        String key = CONV_MSG_COUNT_PREFIX + conversationId;
        Long newCount = redisTemplate.opsForValue().increment(key, count);

        // 设置24小时过期
        if (newCount == count) {
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        }

        log.debug("Conversation {} message count: {}", conversationId, newCount);
    }

    /**
     * 定时清理过期数据（建议配置定时任务调用）
     */
    @Transactional
    public void cleanupExpiredData() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        int deleted = quotaUsageMapper.deleteBeforeDate(thirtyDaysAgo);
        log.info("Cleaned up {} expired quota usage records", deleted);
    }

    /**
     * 获取使用统计的Redis Key
     */
    private String getUsageKey(String userId, LocalDate date) {
        return USAGE_KEY_PREFIX + date.toString() + ":" + userId;
    }
    
    /**
     * 计算到午夜的秒数
     */
    private long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return java.time.Duration.between(now, midnight).getSeconds();
    }
}