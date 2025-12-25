package com.yu.histoaiagent.chatmemory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于Redis的ChatMemory实现
 * 
 * 存储结构：
 * - Key: chat:memory:{conversationId}
 * - Value: List<MessageDTO> (使用Redis List数据结构)
 * 
 * 优势：
 * - 高性能内存存储
 * - 支持TTL自动过期
 * - 分布式友好
 * - 支持消息数量限制
 */
@Component
@Slf4j
public class RedisChatMemory implements ChatMemory {

    private static final String KEY_PREFIX = "chat:memory:";
    private static final long DEFAULT_TTL_HOURS = 24; // 默认24小时过期
    private static final int DEFAULT_MAX_MESSAGES = 100; // 默认最多保存100条消息

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisChatMemory(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void add(String conversationId, Message message) {
        ChatMemory.super.add(conversationId, message);
    }

    /**
     * 添加消息列表到Redis
     * 
     * @param conversationId 会话ID
     * @param messages 消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            log.warn("No messages to add for conversation: {}", conversationId);
            return;
        }

        String key = getKey(conversationId);
        
        try {
            // 将Message转换为Map（便于序列化）
            List<Map<String, Object>> messageMaps = messages.stream()
                    .map(this::messageToMap)
                    .collect(Collectors.toList());

            // 使用RPUSH将消息添加到列表末尾
            redisTemplate.opsForList().rightPushAll(key, messageMaps.toArray());
            
            // 限制消息数量，保留最新的消息
            Long size = redisTemplate.opsForList().size(key);
            if (size != null && size > DEFAULT_MAX_MESSAGES) {
                // 删除最旧的消息
                redisTemplate.opsForList().trim(key, size - DEFAULT_MAX_MESSAGES, -1);
            }

            // 设置过期时间
            redisTemplate.expire(key, DEFAULT_TTL_HOURS, TimeUnit.HOURS);
            
            log.debug("Added {} messages to conversation: {}", messages.size(), conversationId);
        } catch (Exception e) {
            log.error("Failed to add messages to Redis for conversation: {}", conversationId, e);
            throw new RuntimeException("Failed to add messages to Redis", e);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        String key = getKey(conversationId);

        try {
            List<Object> messageMaps;

            // 获取所有消息
            messageMaps = redisTemplate.opsForList().range(key, 0, -1);

            if (messageMaps == null || messageMaps.isEmpty()) {
                log.debug("No messages found for conversation: {}", conversationId);
                return Collections.emptyList();
            }

            // 转换为Message对象
            List<Message> messages = messageMaps.stream()
                    .map(obj -> mapToMessage((Map<String, Object>) obj))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.debug("Retrieved {} messages for conversation: {}", messages.size(), conversationId);
            return messages;

        } catch (Exception e) {
            log.error("Failed to get messages from Redis for conversation: {}", conversationId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取指定会话的消息列表
     * 
     * @param conversationId 会话ID
     * @param lastN 获取最近N条消息，0表示获取所有消息
     * @return 消息列表
     */
    public List<Message> get(String conversationId, int lastN) {
        String key = getKey(conversationId);
        
        try {
            List<Object> messageMaps;
            
            if (lastN > 0) {
                // 获取最后N条消息
                long start = Math.max(0, redisTemplate.opsForList().size(key) - lastN);
                messageMaps = redisTemplate.opsForList().range(key, start, -1);
            } else {
                // 获取所有消息
                messageMaps = redisTemplate.opsForList().range(key, 0, -1);
            }

            if (messageMaps == null || messageMaps.isEmpty()) {
                log.debug("No messages found for conversation: {}", conversationId);
                return Collections.emptyList();
            }

            // 转换为Message对象
            List<Message> messages = messageMaps.stream()
                    .map(obj -> mapToMessage((Map<String, Object>) obj))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.debug("Retrieved {} messages for conversation: {}", messages.size(), conversationId);
            return messages;
            
        } catch (Exception e) {
            log.error("Failed to get messages from Redis for conversation: {}", conversationId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 清空指定会话的所有消息
     * 
     * @param conversationId 会话ID
     */
    @Override
    public void clear(String conversationId) {
        String key = getKey(conversationId);
        
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Cleared conversation: {}, deleted: {}", conversationId, deleted);
        } catch (Exception e) {
            log.error("Failed to clear conversation: {}", conversationId, e);
            throw new RuntimeException("Failed to clear conversation", e);
        }
    }

    /**
     * 获取会话的消息数量
     * 
     * @param conversationId 会话ID
     * @return 消息数量
     */
    public long size(String conversationId) {
        String key = getKey(conversationId);
        Long size = redisTemplate.opsForList().size(key);
        return size != null ? size : 0;
    }

    /**
     * 设置会话的过期时间
     * 
     * @param conversationId 会话ID
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void setExpire(String conversationId, long timeout, TimeUnit unit) {
        String key = getKey(conversationId);
        redisTemplate.expire(key, timeout, unit);
        log.debug("Set expiration for conversation: {} to {} {}", conversationId, timeout, unit);
    }

    /**
     * 检查会话是否存在
     * 
     * @param conversationId 会话ID
     * @return 是否存在
     */
    public boolean exists(String conversationId) {
        String key = getKey(conversationId);
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }

    /**
     * 构造Redis key
     */
    private String getKey(String conversationId) {
        return KEY_PREFIX + conversationId;
    }

    /**
     * 将Message对象转换为Map
     */
    private Map<String, Object> messageToMap(Message message) {
        Map<String, Object> map = new HashMap<>();
        map.put("messageType", message.getMessageType().getValue());
        map.put("content", message.getText());
        
        if (message.getMetadata() != null && !message.getMetadata().isEmpty()) {
            map.put("metadata", message.getMetadata());
        }
        
        return map;
    }

    /**
     * 将Map转换为Message对象
     */
    private Message mapToMessage(Map<String, Object> map) {
        try {
            String messageType = (String) map.get("messageType");
            String content = (String) map.get("content");

            return switch (messageType) {
                case "system" -> new SystemMessage(content);
                case "user" -> new UserMessage(content);
                case "assistant" -> new AssistantMessage(content);
                default -> {
                    log.warn("Unknown message type: {}, treating as UserMessage", messageType);
                    yield new UserMessage(content);
                }
            };
        } catch (Exception e) {
            log.error("Failed to convert map to message", e);
            return null;
        }
    }
}