package com.lovelin.lifesaga.repository;

import com.lovelin.lifesaga.model.Achievement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AchievementRepository {

    private final JdbcTemplate jdbcTemplate;

    public AchievementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Achievement mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Achievement a = new Achievement();
        a.setId(rs.getLong("id"));
        a.setCode(rs.getString("code"));
        a.setName(rs.getString("name"));
        a.setDescription(rs.getString("description"));
        a.setIcon(rs.getString("icon"));
        a.setRarity(rs.getString("rarity"));
        a.setConditionType(rs.getString("condition_type"));
        a.setConditionValue(rs.getInt("condition_value"));
        a.setXpReward(rs.getInt("xp_reward"));
        a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return a;
    }

    public List<Achievement> findAll() {
        String sql = "SELECT * FROM achievements ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Optional<Achievement> findById(Long id) {
        String sql = "SELECT * FROM achievements WHERE id = ?";
        List<Achievement> list = jdbcTemplate.query(sql, this::mapRow, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<Achievement> findByCode(String code) {
        String sql = "SELECT * FROM achievements WHERE code = ?";
        List<Achievement> list = jdbcTemplate.query(sql, this::mapRow, code);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
