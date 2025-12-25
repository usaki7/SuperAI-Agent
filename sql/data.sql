-- ==========================================
-- 用户权限系统数据库表结构
-- ==========================================

-- 1. 用户基础信息表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id VARCHAR(64) NOT NULL UNIQUE COMMENT '用户ID（业务ID）',
    username VARCHAR(100) NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) NOT NULL DEFAULT 'FREE' COMMENT '用户角色：FREE/TRIAL/VIP/ENTERPRISE',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    vip_expire_time DATETIME COMMENT 'VIP过期时间',
    trial_expire_time DATETIME COMMENT '试用过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',

    INDEX idx_user_id (user_id),
    INDEX idx_role (role),
    INDEX idx_enabled (enabled),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础信息表';

-- 2. 用户配额使用记录表
CREATE TABLE IF NOT EXISTS user_quota_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    usage_date DATE NOT NULL COMMENT '使用日期',
    usage_count INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    quota_limit INT NOT NULL COMMENT '配额限制',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    UNIQUE KEY uk_user_date (user_id, usage_date),
    INDEX idx_usage_date (usage_date),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户配额使用记录表';

-- 3. 会话消息统计表
CREATE TABLE IF NOT EXISTS conversation_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    conversation_id VARCHAR(100) NOT NULL UNIQUE COMMENT '会话ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    message_count INT NOT NULL DEFAULT 0 COMMENT '消息数量',
    last_message_time DATETIME COMMENT '最后一条消息时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话消息统计表';

-- 4. 用户操作日志表
CREATE TABLE IF NOT EXISTS user_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型：CHAT/UPGRADE/DISABLE/ENABLE/RESET_QUOTA',
    operation_desc VARCHAR(500) COMMENT '操作描述',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',

    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户操作日志表';

-- ==========================================
-- 插入测试数据
-- ==========================================

-- 清空现有数据（谨慎使用）
-- TRUNCATE TABLE sys_user;

-- 插入测试用户数据
INSERT INTO sys_user (user_id, username, email, role, enabled, vip_expire_time, trial_expire_time) VALUES
-- 免费用户
('free_user', '张三', 'zhangsan@example.com', 'FREE', 1, NULL, NULL),

-- 试用用户（7天试用期）
('trial_user', '李四', 'lisi@example.com', 'TRIAL', 1, NULL, DATE_ADD(NOW(), INTERVAL 7 DAY)),

-- VIP用户（1个月有效期）
('vip_user', '王五', 'wangwu@example.com', 'VIP', 1, DATE_ADD(NOW(), INTERVAL 30 DAY), NULL),

-- 企业用户
('enterprise_user', '某科技公司', 'enterprise@example.com', 'ENTERPRISE', 1, NULL, NULL),

-- 禁用用户
('disabled_user', '已禁用用户', 'disabled@example.com', 'FREE', 0, NULL, NULL),

-- 过期VIP用户
('expired_vip', '过期VIP用户', 'expired@example.com', 'VIP', 1, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL),

-- 更多测试用户
('test_user_1', '测试用户1', 'test1@example.com', 'FREE', 1, NULL, NULL),
('test_user_2', '测试用户2', 'test2@example.com', 'VIP', 1, DATE_ADD(NOW(), INTERVAL 90 DAY), NULL),
('test_user_3', '测试用户3', 'test3@example.com', 'TRIAL', 1, NULL, DATE_ADD(NOW(), INTERVAL 3 DAY));

-- ==========================================
-- 测试查询语句
-- ==========================================

-- 查询所有用户
SELECT user_id, username, role, enabled,
       vip_expire_time, trial_expire_time
FROM sys_user
WHERE is_del = 0;

-- 查询有效VIP用户
SELECT user_id, username, vip_expire_time
FROM sys_user
WHERE role = 'VIP'
  AND enabled = 1
  AND vip_expire_time > NOW()
  AND is_del = 0;

-- 查询即将过期的VIP（3天内）
SELECT user_id, username, vip_expire_time,
       DATEDIFF(vip_expire_time, NOW()) as days_remaining
FROM sys_user
WHERE role = 'VIP'
  AND enabled = 1
  AND vip_expire_time > NOW()
  AND vip_expire_time <= DATE_ADD(NOW(), INTERVAL 3 DAY)
  AND is_del = 0;

-- 查询今日用户使用统计
SELECT u.user_id, u.username, u.role,
       COALESCE(q.usage_count, 0) as today_usage
FROM sys_user u
LEFT JOIN user_quota_usage q
  ON u.user_id = q.user_id
  AND q.usage_date = CURDATE()
WHERE u.is_del = 0;

-- ==========================================
-- 定时清理任务SQL（建议配置定时任务执行）
-- ==========================================

-- 清理30天前的配额记录
DELETE FROM user_quota_usage
WHERE usage_date < DATE_SUB(CURDATE(), INTERVAL 30 DAY);

-- 清理90天前的操作日志
DELETE FROM user_operation_log
WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- 清理过期的会话统计（7天前）
DELETE FROM conversation_stats
WHERE created_at < DATE_SUB(NOW(), INTERVAL 7 DAY);