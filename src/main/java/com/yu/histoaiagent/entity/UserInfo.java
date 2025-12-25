package com.yu.histoaiagent.entity;

import com.yu.histoaiagent.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户角色
     */
    private UserRole role;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 今日已使用次数
     */
    private Integer todayUsageCount;
    
    /**
     * 会话消息数
     */
    private Integer conversationMessageCount;
    
    /**
     * VIP过期时间（仅VIP用户）
     */
    private LocalDateTime vipExpireTime;
    
    /**
     * 试用过期时间（仅试用用户）
     */
    private LocalDateTime trialExpireTime;
    
    /**
     * 检查用户是否有效
     */
    public boolean isValid() {
        if (!Boolean.TRUE.equals(enabled)) {
            return false;
        }
        
        // 检查VIP是否过期
        if (role == UserRole.VIP && vipExpireTime != null) {
            return LocalDateTime.now().isBefore(vipExpireTime);
        }
        
        // 检查试用是否过期
        if (role == UserRole.TRIAL && trialExpireTime != null) {
            return LocalDateTime.now().isBefore(trialExpireTime);
        }
        
        return true;
    }
    
    /**
     * 检查是否还有配额
     */
    public boolean hasQuota() {
        return todayUsageCount < role.getDailyQuota();
    }
    
    /**
     * 检查会话消息数是否超限
     */
    public boolean isConversationWithinLimit() {
        return conversationMessageCount < role.getMessageLimit();
    }
    
    /**
     * 获取剩余配额
     */
    public int getRemainingQuota() {
        return Math.max(0, role.getDailyQuota() - todayUsageCount);
    }
}