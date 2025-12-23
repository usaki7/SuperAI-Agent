package com.yu.histoaiagent.demo.invoke;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.dashscope.QwenChatModel;

public class LangChainAiInvoke {

    public static void main(String[] args) {
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                    .apiKey("api key")
                    .modelName("qwen-max")
                    .build();

        String answer = qwenChatModel.generate("hello, I'm TT");
        System.out.println(answer);
    }
}
