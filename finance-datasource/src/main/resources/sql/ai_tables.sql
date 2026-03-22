-- AI 会话表
CREATE TABLE IF NOT EXISTS `ai_conversations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `conversation_id` VARCHAR(32) NOT NULL COMMENT '会话唯一标识',
    `user_id` BIGINT DEFAULT NULL COMMENT '关联用户 ID',
    `title` VARCHAR(100) DEFAULT NULL COMMENT '会话标题',
    `agent_type` VARCHAR(20) NOT NULL DEFAULT 'chat' COMMENT 'Agent 类型（chat/code/data）',
    `model_id` VARCHAR(50) DEFAULT 'zhipu' COMMENT '模型 ID',
    `message_count` INT NOT NULL DEFAULT 0 COMMENT '消息数量',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/DELETED）',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '软删除标记',
    `lock_version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_conversation_id` (`conversation_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_agent_status` (`user_id`, `agent_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 会话表';

-- AI 消息表
CREATE TABLE IF NOT EXISTS `ai_messages` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `message_id` VARCHAR(32) NOT NULL COMMENT '消息唯一标识',
    `conversation_id` VARCHAR(32) NOT NULL COMMENT '关联会话 ID',
    `role` VARCHAR(20) NOT NULL COMMENT '角色（user/assistant/system）',
    `content` TEXT COMMENT '消息内容',
    `tool_calls` JSON DEFAULT NULL COMMENT '工具调用信息',
    `deleted` INT NOT NULL DEFAULT 0 COMMENT '软删除标记',
    `lock_version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_id` (`message_id`),
    KEY `idx_conversation_id` (`conversation_id`),
    CONSTRAINT `fk_message_conversation` FOREIGN KEY (`conversation_id`)
        REFERENCES `ai_conversations` (`conversation_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 消息表';
