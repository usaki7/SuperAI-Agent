package com.yu.histoaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class TherapyAppMysqlTest {
    @Resource
    private TherapyAppMysql therapyAppMysql;

    @Test
    void doChat() {
        String conversationId = UUID.randomUUID().toString();

        String message = "你好，我是TT";
        String answer = therapyAppMysql.doChat(message, conversationId);
        Assertions.assertNotNull(answer);

        message = "我叫啥名字";
        answer = therapyAppMysql.doChat(message, conversationId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
    }
}