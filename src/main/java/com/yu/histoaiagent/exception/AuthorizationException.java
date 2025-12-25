package com.yu.histoaiagent.exception;

/**
 * 权限校验异常
 */
public class AuthorizationException extends RuntimeException {
    
    private final String errorCode;
    private final String userId;
    
    public AuthorizationException(String message) {
        super(message);
        this.errorCode = "UNAUTHORIZED";
        this.userId = null;
    }
    
    public AuthorizationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.userId = null;
    }
    
    public AuthorizationException(String errorCode, String message, String userId) {
        super(message);
        this.errorCode = errorCode;
        this.userId = userId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getUserId() {
        return userId;
    }
    
    // 预定义的异常类型
    public static AuthorizationException userNotFound(String userId) {
        return new AuthorizationException("USER_NOT_FOUND", "用户不存在", userId);
    }
    
    public static AuthorizationException userDisabled(String userId) {
        return new AuthorizationException("USER_DISABLED", "用户已被禁用", userId);
    }
    
    public static AuthorizationException quotaExceeded(String userId, int remaining) {
        return new AuthorizationException(
            "QUOTA_EXCEEDED", 
            String.format("今日对话次数已用完，剩余次数: %d", remaining),
            userId
        );
    }
    
    public static AuthorizationException messageLimitExceeded(String userId, int limit) {
        return new AuthorizationException(
            "MESSAGE_LIMIT_EXCEEDED",
            String.format("单次对话消息数超过限制: %d条", limit),
            userId
        );
    }
    
    public static AuthorizationException vipExpired(String userId) {
        return new AuthorizationException("VIP_EXPIRED", "VIP会员已过期", userId);
    }
    
    public static AuthorizationException trialExpired(String userId) {
        return new AuthorizationException("TRIAL_EXPIRED", "试用期已过期", userId);
    }
}