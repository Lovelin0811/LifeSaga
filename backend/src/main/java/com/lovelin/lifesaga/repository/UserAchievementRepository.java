package com.lovelin.lifesaga.repository;

import com.lovelin.lifesaga.model.UserAchievement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserAchievementRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserAchievementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private UserAchievement mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        UserAchievement ua = new UserAchievement();
        ua.setId(rs.getLong("id"));
        ua.setUserId(rs.getLong("user_id"));
        ua.setAchievementId(rs.getLong("achievement_id"));
        ua.setUnlockedAt(rs.getTimestamp("unlocked_at").toLocalDateTime());
        return ua;
    }

    public List<UserAchievement> findByUserId(Long userId) {
        String sql = "SELECT * FROM user_achievements WHERE user_id = ? ORDER BY unlocked_at DESC";
        return jdbcTemplate.query(sql, this::mapRow, userId);
    }

    public Optional<UserAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId) {
        String sql = "SELECT * FROM user_achievements WHERE user_id = ? AND achievement_id = ?";
        List<UserAchievement> list = jdbcTemplate.query(sql, this::mapRow, userId, achievementId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public UserAchievement save(UserAchievement userAchievement) {
        String sql = "INSERT INTO user_achievements (user_id, achievement_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userAchievement.getUserId(), userAchievement.getAchievementId());
        return findByUserIdAndAchievementId(userAchievement.getUserId(), userAchievement.getAchievementId()).orElseThrow();
    }
}
