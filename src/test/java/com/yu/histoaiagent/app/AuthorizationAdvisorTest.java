package com.yu.histoaiagent.app;

import com.yu.histoaiagent.exception.AuthorizationException;
import com.yu.histoaiagent.service.UserPermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuthorizationAdvisor 单元测试
 */
@SpringBootTest
class AuthorizationAdvisorTest {

    @Autowired
    private UserPermissionService userPermissionService;

    @BeforeEach
    void setUp() {
        // 重置所有用户的配额
        userPermissionService.resetQuota("free_user");
        userPermissionService.resetQuota("trial_user");
        userPermissionService.resetQuota("vip_user");
        userPermissionService.resetQuota("enterprise_user");
    }

    @Test
    void testFreeUserQuota() {
        // 免费用户每天10次
        for (int i = 0; i < 10; i++) {
            userPermissionService.incrementUsage("free_user");
        }
        
        int usage = userPermissionService.getTodayUsageCount("free_user");
        assertEquals(10, usage);
    }

    @Test
    void testVipUserQuota() {
        // VIP用户每天500次
        for (int i = 0; i < 100; i++) {
            userPermissionService.incrementUsage("vip_user");
        }
        
        int usage = userPermissionService.getTodayUsageCount("vip_user");
        assertEquals(100, usage);
    }

    @Test
    void testUserNotFound() {
        assertNull(userPermissionService.getUserInfo("non_existent_user"));
    }

    @Test
    void testDisabledUser() {
        var userInfo = userPermissionService.getUserInfo("disabled_user");
        assertNotNull(userInfo);
        assertFalse(userInfo.getEnabled());
        assertFalse(userInfo.isValid());
    }

    @Test
    void testExpiredVip() {
        var userInfo = userPermissionService.getUserInfo("expired_vip");
        assertNotNull(userInfo);
        assertTrue(userInfo.getEnabled());
        assertFalse(userInfo.isValid()); // VIP已过期
    }

    @Test
    void testQuotaReset() {
        // 增加使用次数
        userPermissionService.incrementUsage("free_user");
        userPermissionService.incrementUsage("free_user");
        assertEquals(2, userPermissionService.getTodayUsageCount("free_user"));
        
        // 重置配额
        userPermissionService.resetQuota("free_user");
        assertEquals(0, userPermissionService.getTodayUsageCount("free_user"));
    }

    @Test
    void testUpgradeToVip() {
        var userInfo = userPermissionService.getUserInfo("free_user");
        assertEquals("免费用户", userInfo.getRole().getDesc());
        
        // 升级为VIP
        userPermissionService.upgradeToVip("free_user", 30);
        
        userInfo = userPermissionService.getUserInfo("free_user");
        assertEquals("VIP用户", userInfo.getRole().getDesc());
        assertTrue(userInfo.isValid());
    }

    @Test
    void testConversationMessageCount() {
        String conversationId = "test_conv_001";
        
        // 增加消息数
        userPermissionService.incrementConversationMessageCount("free_user", conversationId, 2);
        userPermissionService.incrementConversationMessageCount("free_user", conversationId, 2);
        
        int count = userPermissionService.getConversationMessageCount(conversationId);
        assertEquals(4, count);
    }

    @Test
    void testEnableDisableUser() {
        // 禁用用户
        userPermissionService.disableUser("free_user");
        var userInfo = userPermissionService.getUserInfo("free_user");
        assertFalse(userInfo.getEnabled());
        
        // 启用用户
        userPermissionService.enableUser("free_user");
        userInfo = userPermissionService.getUserInfo("free_user");
        assertTrue(userInfo.getEnabled());
    }

    @Test
    void testRemainingQuota() {
        var userInfo = userPermissionService.getUserInfo("test_user_1");
        assertEquals(10, userInfo.getRemainingQuota());
        
        // 使用3次
        userPermissionService.incrementUsage("free_user");
        userPermissionService.incrementUsage("free_user");
        userPermissionService.incrementUsage("free_user");
        
        userInfo = userPermissionService.getUserInfo("free_user");
        assertEquals(7, userInfo.getRemainingQuota());
    }
}