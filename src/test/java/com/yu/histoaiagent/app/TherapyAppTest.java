package com.yu.histoaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class TherapyAppTest {

    @Resource
    private TherapyApp therapyApp;

    @Test
    void doChat() {
        String conversationId = UUID.randomUUID().toString();

        String message = "你好，我是TT";
        String answer = therapyApp.doChat(message, conversationId);
        Assertions.assertNotNull(answer);

        message = "我很烦恼";
        answer = therapyApp.doChat(message, conversationId);
        Assertions.assertNotNull(answer);

        message = "你还记得我叫什么名字吗";
        answer = therapyApp.doChat(message, conversationId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String conversationId = UUID.randomUUID().toString();

        String message = "你好，我是TT。我经常在早上起床时感到胸口烦闷，一整天都没有精神";
        TherapyApp.TherapyReport therapyReport = therapyApp.doChatWithReport(message, conversationId);
        Assertions.assertNotNull(therapyReport);
    }
}