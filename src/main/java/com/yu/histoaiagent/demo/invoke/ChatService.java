package com.yu.histoaiagent.demo.invoke;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;// 方式1：使用构造器注入

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem("你是xxx")
            .build();
    }
}

