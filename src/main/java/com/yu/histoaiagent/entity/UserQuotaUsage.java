package com.yu.histoaiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户配额使用记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_quota_usage")
public class UserQuotaUsage {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 使用日期
     */
    private LocalDate usageDate;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 配额限制
     */
    private Integer quotaLimit;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}