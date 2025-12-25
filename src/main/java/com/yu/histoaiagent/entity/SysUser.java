package com.yu.histoaiagent.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yu.histoaiagent.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统用户实体（数据库表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class SysUser {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID（业务ID）
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 用户角色（存储为字符串）
     */
    private String role;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * VIP过期时间
     */
    private LocalDateTime vipExpireTime;
    
    /**
     * 试用过期时间
     */
    private LocalDateTime trialExpireTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Boolean isDel;
    
    /**
     * 获取用户角色枚举
     */
    public UserRole getRoleEnum() {
        return UserRole.fromCode(this.role);
    }
    
    /**
     * 设置用户角色枚举
     */
    public void setRoleEnum(UserRole userRole) {
        this.role = userRole.getCode();
    }
    
    /**
     * 检查用户是否有效
     */
    public boolean isValid() {
        if (!Boolean.TRUE.equals(enabled)) {
            return false;
        }
        
        UserRole userRole = getRoleEnum();
        
        // 检查VIP是否过期
        if (userRole == UserRole.VIP && vipExpireTime != null) {
            return LocalDateTime.now().isBefore(vipExpireTime);
        }
        
        // 检查试用是否过期
        if (userRole == UserRole.TRIAL && trialExpireTime != null) {
            return LocalDateTime.now().isBefore(trialExpireTime);
        }
        
        return true;
    }
}