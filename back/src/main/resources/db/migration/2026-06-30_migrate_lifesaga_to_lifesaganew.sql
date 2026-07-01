CREATE DATABASE IF NOT EXISTS LifeSagaNew DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS LifeSagaNew.users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    openid VARCHAR(64) NOT NULL UNIQUE,
    nickname VARCHAR(64) DEFAULT '',
    avatar_url VARCHAR(512) DEFAULT '',
    level INT DEFAULT 1,
    xp INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS LifeSagaNew.sagas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(128) NOT NULL,
    type VARCHAR(32) NOT NULL DEFAULT 'LIFE',
    cover_url VARCHAR(512) DEFAULT '',
    description TEXT,
    status VARCHAR(16) DEFAULT 'ACTIVE',
    is_public TINYINT(1) DEFAULT 0,
    node_count INT DEFAULT 0,
    rarity VARCHAR(16) DEFAULT 'COMMON',
    started_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

CREATE TABLE IF NOT EXISTS LifeSagaNew.saga_nodes (
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

CREATE TABLE IF NOT EXISTS LifeSagaNew.node_favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    node_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_node (user_id, node_id),
    INDEX idx_user_id (user_id),
    INDEX idx_node_id (node_id)
);

CREATE TABLE IF NOT EXISTS LifeSagaNew.achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(512) DEFAULT '',
    icon VARCHAR(64) DEFAULT '',
    rarity VARCHAR(16) DEFAULT 'COMMON',
    condition_type VARCHAR(64) NOT NULL,
    condition_value INT DEFAULT 1,
    xp_reward INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS LifeSagaNew.user_achievements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_achievement (user_id, achievement_id),
    INDEX idx_user_id (user_id)
);

INSERT INTO LifeSagaNew.users (id, openid, nickname, avatar_url, level, xp, created_at, updated_at)
SELECT u.id, u.openid, u.nickname, u.avatar_url, u.level, u.xp, u.created_at, u.updated_at
FROM lifesaga.users u
WHERE NOT EXISTS (
    SELECT 1 FROM LifeSagaNew.users nu WHERE nu.id = u.id
);

INSERT INTO LifeSagaNew.sagas (
    id, user_id, name, type, cover_url, description, status, is_public, node_count, rarity, started_at, ended_at, created_at, updated_at
)
SELECT
    s.id,
    s.user_id,
    s.name,
    CASE
        WHEN LOWER(s.type) = 'relationship' THEN 'RELATIONSHIP'
        ELSE UPPER(s.type)
    END,
    s.cover_url,
    s.description,
    UPPER(s.status),
    s.is_public,
    s.node_count,
    UPPER(s.rarity),
    s.started_at,
    s.ended_at,
    s.created_at,
    s.updated_at
FROM lifesaga.sagas s
WHERE NOT EXISTS (
    SELECT 1 FROM LifeSagaNew.sagas ns WHERE ns.id = s.id
);

INSERT INTO LifeSagaNew.saga_nodes (
    id, saga_id, title, content, location, latitude, longitude, node_time, photos, is_milestone, sort_order, created_at, updated_at
)
SELECT
    n.id,
    n.saga_id,
    n.title,
    n.content,
    n.location,
    n.latitude,
    n.longitude,
    n.node_time,
    n.photos,
    n.is_milestone,
    n.sort_order,
    n.created_at,
    n.updated_at
FROM lifesaga.saga_nodes n
WHERE NOT EXISTS (
    SELECT 1 FROM LifeSagaNew.saga_nodes nn WHERE nn.id = n.id
);

INSERT INTO LifeSagaNew.node_favorites (id, user_id, node_id, created_at)
SELECT f.id, f.user_id, f.node_id, f.created_at
FROM lifesaga.node_favorites f
WHERE NOT EXISTS (
    SELECT 1 FROM LifeSagaNew.node_favorites nf WHERE nf.id = f.id
);

INSERT INTO LifeSagaNew.achievements (
    id, code, name, description, icon, rarity, condition_type, condition_value, xp_reward, created_at
)
SELECT
    a.id,
    a.code,
    a.name,
    a.description,
    a.icon,
    UPPER(a.rarity),
    a.condition_type,
    a.condition_value,
    a.xp_reward,
    a.created_at
FROM lifesaga.achievements a
WHERE NOT EXISTS (
    SELECT 1 FROM LifeSagaNew.achievements na WHERE na.id = a.id
);

INSERT INTO LifeSagaNew.user_achievements (id, user_id, achievement_id, unlocked_at)
SELECT ua.id, ua.user_id, ua.achievement_id, ua.unlocked_at
FROM lifesaga.user_achievements ua
WHERE NOT EXISTS (
    SELECT 1 FROM LifeSagaNew.user_achievements nua WHERE nua.id = ua.id
);

ALTER TABLE LifeSagaNew.sagas AUTO_INCREMENT = 1;
ALTER TABLE LifeSagaNew.saga_nodes AUTO_INCREMENT = 1;
ALTER TABLE LifeSagaNew.node_favorites AUTO_INCREMENT = 1;
ALTER TABLE LifeSagaNew.achievements AUTO_INCREMENT = 1;
ALTER TABLE LifeSagaNew.user_achievements AUTO_INCREMENT = 1;
ALTER TABLE LifeSagaNew.users AUTO_INCREMENT = 1;

SET @users_next_id = (SELECT COALESCE(MAX(id), 0) + 1 FROM LifeSagaNew.users);
SET @sagas_next_id = (SELECT COALESCE(MAX(id), 0) + 1 FROM LifeSagaNew.sagas);
SET @saga_nodes_next_id = (SELECT COALESCE(MAX(id), 0) + 1 FROM LifeSagaNew.saga_nodes);
SET @node_favorites_next_id = (SELECT COALESCE(MAX(id), 0) + 1 FROM LifeSagaNew.node_favorites);
SET @achievements_next_id = (SELECT COALESCE(MAX(id), 0) + 1 FROM LifeSagaNew.achievements);
SET @user_achievements_next_id = (SELECT COALESCE(MAX(id), 0) + 1 FROM LifeSagaNew.user_achievements);

SET @sql = CONCAT('ALTER TABLE LifeSagaNew.users AUTO_INCREMENT = ', @users_next_id);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT('ALTER TABLE LifeSagaNew.sagas AUTO_INCREMENT = ', @sagas_next_id);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT('ALTER TABLE LifeSagaNew.saga_nodes AUTO_INCREMENT = ', @saga_nodes_next_id);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT('ALTER TABLE LifeSagaNew.node_favorites AUTO_INCREMENT = ', @node_favorites_next_id);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT('ALTER TABLE LifeSagaNew.achievements AUTO_INCREMENT = ', @achievements_next_id);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = CONCAT('ALTER TABLE LifeSagaNew.user_achievements AUTO_INCREMENT = ', @user_achievements_next_id);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE LifeSagaNew.sagas
    ADD CONSTRAINT fk_sagas_user
        FOREIGN KEY (user_id) REFERENCES LifeSagaNew.users (id)
        ON DELETE CASCADE;

ALTER TABLE LifeSagaNew.saga_nodes
    ADD CONSTRAINT fk_saga_nodes_saga
        FOREIGN KEY (saga_id) REFERENCES LifeSagaNew.sagas (id)
        ON DELETE CASCADE;

ALTER TABLE LifeSagaNew.node_favorites
    ADD CONSTRAINT fk_node_favorites_user
        FOREIGN KEY (user_id) REFERENCES LifeSagaNew.users (id)
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_node_favorites_node
        FOREIGN KEY (node_id) REFERENCES LifeSagaNew.saga_nodes (id)
        ON DELETE CASCADE;

ALTER TABLE LifeSagaNew.user_achievements
    ADD CONSTRAINT fk_user_achievements_user
        FOREIGN KEY (user_id) REFERENCES LifeSagaNew.users (id)
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_user_achievements_achievement
        FOREIGN KEY (achievement_id) REFERENCES LifeSagaNew.achievements (id)
        ON DELETE CASCADE;
