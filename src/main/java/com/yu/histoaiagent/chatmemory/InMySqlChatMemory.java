package com.yu.histoaiagent.chatmemory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yu.histoaiagent.entity.MysqlChatMemory;
import com.yu.histoaiagent.mapper.MysqlChatMemoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 基于MySQL的ChatMemory自定义实现
 * 参考文章：https://jishuzhan.net/article/1942048244019802114
 * 
 * 实现思路：
 * 1. 每条消息作为单独的一行记录存储到数据库
 * 2. 通过chat_id关联同一会话的所有消息
 * 3. 使用MyBatis-Plus进行数据库操作
 */
@Component
@Slf4j
public class InMySqlChatMemory implements ChatMemory {

    private final MysqlChatMemoryMapper mysqlChatMemoryMapper;

    public InMySqlChatMemory(MysqlChatMemoryMapper mysqlChatMemoryMapper) {
        this.mysqlChatMemoryMapper = mysqlChatMemoryMapper;
    }

    @Override
    public void add(String conversationId, Message message) {
        ChatMemory.super.add(conversationId, message);
    }

    /**
     * 添加消息列表到指定会话
     * @param conversationId 会话ID
     * @param messages 消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            log.warn("No messages to add for conversation: {}", conversationId);
            return;
        }

        List<MysqlChatMemory> mysqlChatMemoryList = new ArrayList<>();
        for (Message message : messages) {
            MysqlChatMemory mysqlChatMemory = new MysqlChatMemory();
            mysqlChatMemory.setChatId(conversationId);
            mysqlChatMemory.setType(message.getMessageType().getValue());
            mysqlChatMemory.setContent(message.getText());
            mysqlChatMemoryList.add(mysqlChatMemory);
        }

        // 批量插入
        mysqlChatMemoryList.forEach(mysqlChatMemoryMapper::insert);
        log.debug("Added {} messages to conversation: {}", messages.size(), conversationId);
    }

    @Override
    public List<Message> get(String conversationId) {
        QueryWrapper<MysqlChatMemory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chat_id", conversationId)
                .orderByAsc("create_time");  // 按创建时间升序排列

        List<MysqlChatMemory> mysqlChatMemoryList = mysqlChatMemoryMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(mysqlChatMemoryList)) {
            log.debug("No messages found for conversation: {}", conversationId);
            return Collections.emptyList();
        }

        // 将数据库记录转换为Message对象
        List<Message> messages = new ArrayList<>();
        for (MysqlChatMemory mysqlChatMemory : mysqlChatMemoryList) {
            Message message = convertToMessage(mysqlChatMemory);
            if (message != null) {
                messages.add(message);
            }
        }

        log.debug("Retrieved {} messages for conversation: {}", messages.size(), conversationId);
        return messages;
    }

    /**
     * 获取指定会话的最近N条消息
     * @param conversationId 会话ID
     * @param lastN 获取最近的N条消息，如果为0则获取所有消息
     * @return 消息列表
     */
    public List<Message> get(String conversationId, int lastN) {
        QueryWrapper<MysqlChatMemory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chat_id", conversationId)
                .orderByAsc("create_time");  // 按创建时间升序排列
        
        if (lastN > 0) {
            // 如果指定了lastN，则获取最后N条记录
            queryWrapper.last("limit " + lastN);
        }

        List<MysqlChatMemory> mysqlChatMemoryList = mysqlChatMemoryMapper.selectList(queryWrapper);
        
        if (CollectionUtils.isEmpty(mysqlChatMemoryList)) {
            log.debug("No messages found for conversation: {}", conversationId);
            return Collections.emptyList();
        }

        // 将数据库记录转换为Message对象
        List<Message> messages = new ArrayList<>();
        for (MysqlChatMemory mysqlChatMemory : mysqlChatMemoryList) {
            Message message = convertToMessage(mysqlChatMemory);
            if (message != null) {
                messages.add(message);
            }
        }

        log.debug("Retrieved {} messages for conversation: {}", messages.size(), conversationId);
        return messages;
    }

    /**
     * 清空指定会话的所有消息
     * @param conversationId 会话ID
     */
    @Override
    public void clear(String conversationId) {
        QueryWrapper<MysqlChatMemory> queryWrapper = new QueryWrapper<>();
        if (conversationId != null) {
            queryWrapper.eq("chat_id", conversationId);
        }
        
        int deletedCount = mysqlChatMemoryMapper.delete(queryWrapper);
        log.debug("Cleared {} messages for conversation: {}", deletedCount, conversationId);
    }

    /**
     * 将数据库记录转换为Spring AI的Message对象
     * @param mysqlChatMemory 数据库记录
     * @return Message对象
     */
    private Message convertToMessage(MysqlChatMemory mysqlChatMemory) {
        String type = mysqlChatMemory.getType();
        String content = mysqlChatMemory.getContent();

        return switch (type) {
            case "system" -> new SystemMessage(content);
            case "user" -> new UserMessage(content);
            case "assistant" -> new AssistantMessage(content);
            default -> {
                log.warn("Unknown message type: {}, treating as UserMessage", type);
                yield new UserMessage(content);
            }
        };
    }
}