package com.yu.histoaiagent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * AI对话记忆实体类
 * 对应ai_chat_memory表
 */
@Data
@TableName("ai_chat_memory")
public class MysqlChatMemory {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private String chatId;

    /**
     * 消息类型：user/assistant/system
     */
    private String type;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标记 0-未删除 1-已删除
     */
    @TableLogic
    private Integer isDel;

}