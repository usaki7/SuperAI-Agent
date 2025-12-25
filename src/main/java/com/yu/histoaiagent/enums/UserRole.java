package com.yu.histoaiagent.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    /**
     * 免费用户 - 基础权限
     */
    FREE("FREE", "免费用户", 10, 50),
    
    /**
     * 试用用户 - 限时体验
     */
    TRIAL("TRIAL", "试用用户", 50, 200),
    
    /**
     * VIP用户 - 高级权限
     */
    VIP("VIP", "VIP用户", 500, 2000),
    
    /**
     * 企业用户 - 无限制
     */
    ENTERPRISE("ENTERPRISE", "企业用户", Integer.MAX_VALUE, Integer.MAX_VALUE);

    private final String code;
    private final String desc;
    private final int dailyQuota;      // 每日对话次数限制
    private final int messageLimit;     // 单次对话消息数限制

    UserRole(String code, String desc, int dailyQuota, int messageLimit) {
        this.code = code;
        this.desc = desc;
        this.dailyQuota = dailyQuota;
        this.messageLimit = messageLimit;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public int getDailyQuota() {
        return dailyQuota;
    }

    public int getMessageLimit() {
        return messageLimit;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return FREE; // 默认返回免费用户
    }
}