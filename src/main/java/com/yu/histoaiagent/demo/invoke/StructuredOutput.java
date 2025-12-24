package com.yu.histoaiagent.demo.invoke;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

public class StructuredOutput {
    private ChatModel chatModel;
    // 定义一个记录类
    record ActorsFilms(String actor, List<String> movies) {}

    // 使用高级 ChatClient API
    ActorsFilms actorsFilms = ChatClient.create(chatModel).prompt()
            .user("Generate 5 movies for Tom Hanks.")
            .call()
            .entity(ActorsFilms.class);

    // 可以转换为对象列表
    List<ActorsFilms> actorsFilmsList = ChatClient.create(chatModel).prompt()
            .user("Generate the filmography of 5 movies for Tom Hanks and Bill Murray.")
            .call()
            .entity(new ParameterizedTypeReference<List<ActorsFilms>>() {});

    Map<String, Object> result = ChatClient.create(chatModel).prompt()
        .user(u -> u.text("Provide me a List of {subject}")
                    .param("subject", "an array of numbers from 1 to 9 under they key name 'numbers'"))
        .call()
        .entity(new ParameterizedTypeReference<Map<String, Object>>() {});

}
