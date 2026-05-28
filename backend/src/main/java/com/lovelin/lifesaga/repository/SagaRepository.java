package com.lovelin.lifesaga.repository;

import com.lovelin.lifesaga.model.Saga;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class SagaRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String BASE_COLUMNS = "id, user_id, name, type, cover_url, description, status, node_count, rarity, started_at, ended_at, created_at, updated_at";

    public SagaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Saga mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Saga saga = new Saga();
        saga.setId(rs.getLong("id"));
        saga.setUserId(rs.getLong("user_id"));
        saga.setName(rs.getString("name"));
        saga.setType(rs.getString("type"));
        saga.setCoverUrl(rs.getString("cover_url"));
        saga.setDescription(rs.getString("description"));
        saga.setStatus(rs.getString("status"));
        saga.setNodeCount(rs.getInt("node_count"));
        saga.setRarity(rs.getString("rarity"));
        java.sql.Timestamp startedAt = rs.getTimestamp("started_at");
        if (startedAt != null) saga.setStartedAt(startedAt.toLocalDateTime());
        java.sql.Timestamp endedAt = rs.getTimestamp("ended_at");
        if (endedAt != null) saga.setEndedAt(endedAt.toLocalDateTime());
        saga.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        saga.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return saga;
    }

    public List<Saga> findByUserId(Long userId) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM sagas WHERE user_id = ? ORDER BY updated_at DESC";
        return jdbcTemplate.query(sql, this::mapRow, userId);
    }

    public Optional<Saga> findById(Long id) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM sagas WHERE id = ?";
        List<Saga> list = jdbcTemplate.query(sql, this::mapRow, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Saga save(Saga saga) {
        String sql = "INSERT INTO sagas (user_id, name, type, cover_url, description, status, node_count, rarity, started_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, saga.getUserId());
            ps.setString(2, saga.getName());
            ps.setString(3, saga.getType());
            ps.setString(4, saga.getCoverUrl());
            ps.setString(5, saga.getDescription());
            ps.setString(6, saga.getStatus());
            ps.setInt(7, saga.getNodeCount());
            ps.setString(8, saga.getRarity());
            if (saga.getStartedAt() != null) {
                ps.setTimestamp(9, java.sql.Timestamp.valueOf(saga.getStartedAt()));
            } else {
                ps.setNull(9, java.sql.Types.TIMESTAMP);
            }
            return ps;
        }, keyHolder);
        saga.setId(keyHolder.getKey().longValue());
        return saga;
    }

    public int update(Saga saga) {
        String sql = "UPDATE sagas SET name = ?, type = ?, cover_url = ?, description = ?, status = ?, node_count = ?, rarity = ?, ended_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                saga.getName(), saga.getType(), saga.getCoverUrl(), saga.getDescription(),
                saga.getStatus(), saga.getNodeCount(), saga.getRarity(),
                saga.getEndedAt() != null ? java.sql.Timestamp.valueOf(saga.getEndedAt()) : null,
                saga.getId());
    }

    public int updateNodeCountAndRarity(Long sagaId, int nodeCount, String rarity) {
        String sql = "UPDATE sagas SET node_count = ?, rarity = ? WHERE id = ?";
        return jdbcTemplate.update(sql, nodeCount, rarity, sagaId);
    }

    public int delete(Long id) {
        return jdbcTemplate.update("DELETE FROM sagas WHERE id = ?", id);
    }

    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM sagas WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null ? count : 0;
    }

    public List<String> findDistinctTypesByUserId(Long userId) {
        String sql = "SELECT DISTINCT type FROM sagas WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }

    public int countCompletedByType(Long userId, String type) {
        String sql = "SELECT COUNT(*) FROM sagas WHERE user_id = ? AND type = ? AND status = 'completed'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, type);
        return count != null ? count : 0;
    }

    public boolean hasLegendary(Long userId) {
        String sql = "SELECT COUNT(*) FROM sagas WHERE user_id = ? AND rarity = 'legendary'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }
}
