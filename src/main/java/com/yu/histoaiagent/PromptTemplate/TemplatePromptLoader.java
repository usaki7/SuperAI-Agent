package com.yu.histoaiagent.PromptTemplate;

import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class TemplatePromptLoader {

    // 正确路径：classpath:/ 指向resources根目录
    @Value("classpath:/prompts/system-message.st")
    private Resource systemTemplateResource;

    /**
     * 渲染系统提示词模板（兼容JAR/测试环境）
     */
    public String renderSystemPrompt(Map<String, Object> variables) {
        try {
            // 关键：用InputStream读取，避免getFile()在JAR/测试环境失效
            byte[] contentBytes = systemTemplateResource.getInputStream().readAllBytes();
            String templateContent = new String(contentBytes, StandardCharsets.UTF_8);

            SystemPromptTemplate template = new SystemPromptTemplate(templateContent);
            return template.createMessage(variables).getText();
        } catch (Exception e) {
            throw new RuntimeException("渲染系统提示词模板失败", e);
        }
    }
}