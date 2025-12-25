package com.yu.histoaiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.histoaiagent.entity.UserQuotaUsage;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;

/**
 * 用户配额使用记录Mapper
 */
@Mapper
public interface UserQuotaUsageMapper extends BaseMapper<UserQuotaUsage> {
    
    /**
     * 查询用户今日使用次数
     */
    @Select("SELECT usage_count FROM user_quota_usage " +
            "WHERE user_id = #{userId} AND usage_date = #{date}")
    Integer findTodayUsage(String userId, LocalDate date);
    
    /**
     * 增加使用次数
     */
    @Update("UPDATE user_quota_usage " +
            "SET usage_count = usage_count + 1 " +
            "WHERE user_id = #{userId} AND usage_date = #{date}")
    int incrementUsage(String userId, LocalDate date);
    
    /**
     * 插入或更新使用记录
     */
    @Insert("INSERT INTO user_quota_usage (user_id, usage_date, usage_count, quota_limit) " +
            "VALUES (#{userId}, #{usageDate}, #{usageCount}, #{quotaLimit}) " +
            "ON DUPLICATE KEY UPDATE " +
            "usage_count = usage_count + #{usageCount}")
    int insertOrUpdateUsage(UserQuotaUsage usage);
    
    /**
     * 删除指定日期之前的记录
     */
    @Delete("DELETE FROM user_quota_usage WHERE usage_date < #{date}")
    int deleteBeforeDate(LocalDate date);
}