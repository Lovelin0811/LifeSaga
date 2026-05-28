package com.lovelin.lifesaga.repository;

import com.lovelin.lifesaga.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setOpenid(rs.getString("openid"));
        user.setNickname(rs.getString("nickname"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setLevel(rs.getInt("level"));
        user.setXp(rs.getInt("xp"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return user;
    };

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByOpenid(String openid) {
        String sql = "SELECT * FROM users WHERE openid = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, openid);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public User save(User user) {
        String sql = "INSERT INTO users (openid, nickname, avatar_url, level, xp) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getOpenid(), user.getNickname(), user.getAvatarUrl(),
                user.getLevel(), user.getXp());
        return findByOpenid(user.getOpenid()).orElseThrow();
    }

    public int update(User user) {
        String sql = "UPDATE users SET nickname = ?, avatar_url = ?, level = ?, xp = ? WHERE id = ?";
        return jdbcTemplate.update(sql, user.getNickname(), user.getAvatarUrl(),
                user.getLevel(), user.getXp(), user.getId());
    }

    public int addXp(Long userId, int xpToAdd) {
        String sql = "UPDATE users SET xp = xp + ? WHERE id = ?";
        return jdbcTemplate.update(sql, xpToAdd, userId);
    }
}
