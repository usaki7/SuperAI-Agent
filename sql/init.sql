-- 创建ai_chat_memory表（每条消息作为一行记录）
CREATE TABLE `ai_chat_memory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
  `chat_id` VARCHAR(100) NOT NULL COMMENT '会话ID',
  `type` VARCHAR(10) NOT NULL DEFAULT 'user' COMMENT '消息类型(user/assistant/system)',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_del` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记,0-未删除;1-已删除',

  INDEX idx_chat_id (chat_id),
  INDEX idx_create_time (create_time)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话记忆表';