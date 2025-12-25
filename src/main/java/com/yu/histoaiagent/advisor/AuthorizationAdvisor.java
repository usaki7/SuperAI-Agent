package com.yu.histoaiagent.advisor;

import com.yu.histoaiagent.entity.UserInfo;
import com.yu.histoaiagent.exception.AuthorizationException;
import com.yu.histoaiagent.service.UserPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;

import java.util.Map;

/**
 * 权限校验Advisor
 *
 * 功能：
 * 1. 校验用户是否存在
 * 2. 校验用户是否启用
 * 3. 校验用户角色权限
 * 4. 校验每日配额
 * 5. 校验会话消息数限制
 * 6. 记录使用情况
 *
 * 使用方式：
 * advisorSpec.param(AuthorizationAdvisor.USER_ID_PARAM, userId)
 */
@Slf4j
public class AuthorizationAdvisor implements CallAdvisor {

    /**
     * 用户ID参数名
     */
    public static final String USER_ID_PARAM = "userId";

    /**
     * 跳过权限检查参数（用于管理员等特殊场景）
     */
    public static final String SKIP_AUTH_CHECK = "skipAuthCheck";

    private final UserPermissionService userPermissionService;

    /**
     * Advisor执行顺序（数字越小越先执行）
     * 权限检查应该在最前面，在ChatMemory之前
     */
    private final int order;

    public AuthorizationAdvisor(UserPermissionService userPermissionService) {
        this(userPermissionService, 0);
    }

    public AuthorizationAdvisor(UserPermissionService userPermissionService, int order) {
        this.userPermissionService = userPermissionService;
        this.order = order;
    }

    @Override
    public String getName() {
        return "AuthorizationAdvisor";
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        log.debug("=== AuthorizationAdvisor - 开始权限校验 ===");

        // 1. 检查是否跳过权限检查
        if (shouldSkipAuthCheck(request)) {
            log.debug("跳过权限检查");
            return chain.nextCall(request);
        }

        // 2. 获取用户ID
        String userId = getUserId(request);
        if (userId == null || userId.isEmpty()) {
            throw new AuthorizationException("USER_ID_REQUIRED", "用户ID不能为空");
        }

        log.info("权限校验 - 用户ID: {}", userId);

        // 3. 获取用户信息
        UserInfo userInfo = userPermissionService.getUserInfo(userId);
        if (userInfo == null) {
            throw AuthorizationException.userNotFound(userId);
        }

        // 4. 检查用户是否启用
        if (!Boolean.TRUE.equals(userInfo.getEnabled())) {
            throw AuthorizationException.userDisabled(userId);
        }

        // 5. 检查用户是否有效（VIP/试用是否过期）
        if (!userInfo.isValid()) {
            if (userInfo.getRole().name().equals("VIP")) {
                throw AuthorizationException.vipExpired(userId);
            } else if (userInfo.getRole().name().equals("TRIAL")) {
                throw AuthorizationException.trialExpired(userId);
            }
        }

        // 6. 检查每日配额
        if (!userInfo.hasQuota()) {
            throw AuthorizationException.quotaExceeded(
                userId,
                userInfo.getRemainingQuota()
            );
        }

        // 7. 检查会话消息数限制（如果有conversationId）
        String conversationId = getConversationId(request);
        if (conversationId != null) {
            int messageCount = userPermissionService.getConversationMessageCount(conversationId);
            if (messageCount >= userInfo.getRole().getMessageLimit()) {
                throw AuthorizationException.messageLimitExceeded(
                    userId,
                    userInfo.getRole().getMessageLimit()
                );
            }
        }

        log.info("权限校验通过 - 用户: {}, 角色: {}, 今日剩余: {}/{}",
                userInfo.getUsername(),
                userInfo.getRole().getDesc(),
                userInfo.getRemainingQuota(),
                userInfo.getRole().getDailyQuota());

        // 8. 增加使用次数
        userPermissionService.incrementUsage(userId);

        // 9. 继续执行后续Advisor和AI调用
        ChatClientResponse response = chain.nextCall(request);

        // 10. 记录会话消息数
        if (conversationId != null) {
            // 用户消息 + AI响应消息 = 2条
            userPermissionService.incrementConversationMessageCount(userId, conversationId, 2);
        }

        log.debug("=== AuthorizationAdvisor - 权限校验完成 ===");
        return response;
    }

    /**
     * 从请求中获取用户ID
     */
    private String getUserId(ChatClientRequest request) {
        Map<String, Object> context = request.context();
        return (String) context.get(USER_ID_PARAM);
    }

    /**
     * 从请求中获取会话ID
     */
    private String getConversationId(ChatClientRequest request) {
        Map<String, Object> context = request.context();
        return (String) context.get(ChatMemory.CONVERSATION_ID);
    }

    /**
     * 检查是否跳过权限检查
     */
    private boolean shouldSkipAuthCheck(ChatClientRequest request) {
        Map<String, Object> context = request.context();
        Boolean skip = (Boolean) context.get(SKIP_AUTH_CHECK);
        return Boolean.TRUE.equals(skip);
    }
}