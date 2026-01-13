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

    @Test
    void doChatWithRag() {
        String conversationId = UUID.randomUUID().toString();

        String message = "你好，我是TT。我得了抑郁症，经常在早上起床时感到胸口烦闷，一整天都没有精神";
        String therapyReport = therapyApp.doChatWithRag(message, conversationId);
        Assertions.assertNotNull(therapyReport);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = therapyApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
        String message = "我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点";
        String answer =  therapyApp.doChatWithMcp(message, chatId);
    }

}