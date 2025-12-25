package com.yu.histoaiagent.PromptTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试类必须添加@SpringBootTest，加载Spring上下文
 */
@SpringBootTest // 核心：启动Spring容器，让@Value/@Autowired生效
public class TemplatePromptLoaderTest {

    // 自动注入Spring管理的Bean（而非new）
    @Autowired
    private TemplatePromptLoader templatePromptLoader;

    @Test
    public void renderSystemPrompt() {
        // 1. 构造测试变量
        Map<String, Object> variables = Map.of(
                "username", "测试用户",
                "conversationContext", "测试上下文",
                "maxLength", 100,
                "language", "中文"
        );

        // 3. 执行测试
        String result = templatePromptLoader.renderSystemPrompt(variables);

        // 4. 断言结果
        assertNotNull(result);
        assertTrue(result.contains("测试用户"));
        assertTrue(result.contains("测试上下文"));
        System.out.println("测试渲染结果：\n" + result);
    }
}