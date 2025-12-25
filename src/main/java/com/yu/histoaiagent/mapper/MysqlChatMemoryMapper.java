package com.yu.histoaiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.histoaiagent.entity.MysqlChatMemory;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI对话记忆Mapper接口
 * 使用MyBatis-Plus提供的BaseMapper
 */
@Mapper
public interface MysqlChatMemoryMapper extends BaseMapper<MysqlChatMemory> {
    
}