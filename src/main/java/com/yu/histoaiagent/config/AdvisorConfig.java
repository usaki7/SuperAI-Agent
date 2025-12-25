package com.yu.histoaiagent.config;

import com.yu.histoaiagent.advisor.AuthorizationAdvisor;
import com.yu.histoaiagent.service.UserPermissionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Advisor配置类
 */
@Configuration
public class AdvisorConfig {
    
    /**
     * 注册权限校验Advisor
     * Order设为0，确保在其他Advisor之前执行
     */
    @Bean
    public AuthorizationAdvisor authorizationAdvisor(UserPermissionService userPermissionService) {
        return new AuthorizationAdvisor(userPermissionService, 0);
    }
}