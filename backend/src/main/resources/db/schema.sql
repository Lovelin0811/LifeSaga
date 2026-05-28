CREATE DATABASE IF NOT EXISTS lifesaga DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lifesaga;

-- 用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    openid VARCHAR(64) NOT NULL UNIQUE,
    nickname VARCHAR(64) DEFAULT '',
    avatar_url VARCHAR(512) DEFAULT '',
    level INT DEFAULT 1,
    xp INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 副本表
CREATE TABLE sagas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    type VARCHAR(32) NOT NULL DEFAULT 'life',
    cover_url VARCHAR(512) DEFAULT '',
    description TEXT,
    status VARCHAR(16) DEFAULT 'active',
    node_count INT DEFAULT 0,
    rarity VARCHAR(16) DEFAULT 'common',
    started_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

-- 节点表
CREATE TABLE saga_nodes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    saga_id BIGINT NOT NULL,
    title VARCHAR(256) DEFAULT '',
    content TEXT,
    location VARCHAR(256) DEFAULT '',
    latitude DECIMAL(10,7) DEFAULT NULL,
    longitude DECIMAL(10,7) DEFAULT NULL,
    node_time TIMESTAMP NULL,
    photos TEXT,
    is_milestone TINYINT(1) DEFAULT 0,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_saga_id (saga_id),
    INDEX idx_node_time (node_time)
);

-- 成就表
CREATE TABLE achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(512) DEFAULT '',
    icon VARCHAR(64) DEFAULT '',
    rarity VARCHAR(16) DEFAULT 'common',
    condition_type VARCHAR(64) NOT NULL,
    condition_value INT DEFAULT 1,
    xp_reward INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户成就关联表
CREATE TABLE user_achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_achievement (user_id, achievement_id),
    INDEX idx_user_id (user_id)
);

INSERT INTO achievements (code, name, description, icon, rarity, condition_type, condition_value, xp_reward) VALUES
('first_saga', '冒险新手', '创建第一个副本', 'seedling', 'common', 'first_saga', 1, 10),
('first_node', '记录者', '添加第一个节点', 'pencil', 'common', 'first_node', 1, 10),
('first_photo', '摄影师', '添加第一张照片', 'camera', 'common', 'first_photo', 1, 10),
('explorer', '探险家', '创建3个不同类型副本', 'compass', 'rare', 'saga_types_count', 3, 50),
('traveler', '旅行家', '完成1个旅行副本', 'plane', 'rare', 'completed_type_travel', 1, 50),
('scholar', '学霸', '完成1个学习副本', 'book', 'rare', 'completed_type_study', 1, 50),
('collector', '收藏家', '累计10个副本', 'gem', 'epic', 'total_sagas', 10, 100),
('legend_hunter', '传说猎人', '获得第一个传说副本', 'star', 'epic', 'has_legendary', 1, 100),
('streak_7', '连续记录', '连续7天添加节点', 'fire', 'epic', 'streak_days', 7, 80),
('completionist', '全满贯', '所有类型副本各完成一个', 'trophy', 'legendary', 'all_types_completed', 6, 200);
