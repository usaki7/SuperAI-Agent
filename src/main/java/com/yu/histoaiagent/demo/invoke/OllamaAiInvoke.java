package com.yu.histoaiagent.demo.invoke;

//import jakarta.annotation.Resource;
//import org.springframework.ai.chat.messages.AssistantMessage;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class OllamaAiInvoke implements CommandLineRunner {
//
//    @Resource
//    @Qualifier("ollamaChatModel")
//    private ChatModel ollamaChatModel;
//
//    @Override
//    public void run(String... args) throws Exception {
//        AssistantMessage assistantMessage = ollamaChatModel.call(new Prompt("hello, I'm TT"))
//                .getResult()
//                .getOutput();
//        System.out.println(assistantMessage.getText());
//    }
//}
