package com.yu.histoaiagent.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatModelPrimaryConfig {

    // 标记 dashscopeChatModel 为全局默认的 ChatModel
    @Primary
    @Bean
    public ChatModel primaryChatModel(@Qualifier("dashscopeChatModel") ChatModel dashscopeChatModel) {
        return dashscopeChatModel;
    }

    // 若想默认用 ollama，替换为：
    // @Primary
    // @Bean
    // public ChatModel primaryChatModel(@Qualifier("ollamaChatModel") ChatModel ollamaChatModel) {
    //     return ollamaChatModel;
    // }
}