package com.lovelin.lifesaga.repository;

import com.lovelin.lifesaga.model.SagaNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class NodeRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String BASE_COLUMNS = "id, saga_id, title, content, location, latitude, longitude, node_time, photos, is_milestone, sort_order, created_at, updated_at";

    public NodeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private SagaNode mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        SagaNode node = new SagaNode();
        node.setId(rs.getLong("id"));
        node.setSagaId(rs.getLong("saga_id"));
        node.setTitle(rs.getString("title"));
        node.setContent(rs.getString("content"));
        node.setLocation(rs.getString("location"));
        node.setLatitude(rs.getBigDecimal("latitude"));
        node.setLongitude(rs.getBigDecimal("longitude"));
        java.sql.Timestamp nodeTime = rs.getTimestamp("node_time");
        if (nodeTime != null) node.setNodeTime(nodeTime.toLocalDateTime());
        node.setPhotos(rs.getString("photos"));
        node.setMilestone(rs.getBoolean("is_milestone"));
        node.setSortOrder(rs.getInt("sort_order"));
        node.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        node.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return node;
    }

    public List<SagaNode> findBySagaId(Long sagaId) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM saga_nodes WHERE saga_id = ? ORDER BY node_time ASC, sort_order ASC, created_at ASC";
        return jdbcTemplate.query(sql, this::mapRow, sagaId);
    }

    public Optional<SagaNode> findById(Long id) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM saga_nodes WHERE id = ?";
        List<SagaNode> list = jdbcTemplate.query(sql, this::mapRow, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public SagaNode save(SagaNode node) {
        String sql = "INSERT INTO saga_nodes (saga_id, title, content, location, latitude, longitude, node_time, photos, is_milestone, sort_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, node.getSagaId());
            ps.setString(2, node.getTitle());
            ps.setString(3, node.getContent());
            ps.setString(4, node.getLocation());
            if (node.getLatitude() != null) {
                ps.setBigDecimal(5, node.getLatitude());
            } else {
                ps.setNull(5, java.sql.Types.DECIMAL);
            }
            if (node.getLongitude() != null) {
                ps.setBigDecimal(6, node.getLongitude());
            } else {
                ps.setNull(6, java.sql.Types.DECIMAL);
            }
            if (node.getNodeTime() != null) {
                ps.setTimestamp(7, java.sql.Timestamp.valueOf(node.getNodeTime()));
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }
            ps.setString(8, node.getPhotos());
            ps.setBoolean(9, node.isMilestone());
            ps.setInt(10, node.getSortOrder());
            return ps;
        }, keyHolder);
        node.setId(keyHolder.getKey().longValue());
        return node;
    }

    public int update(SagaNode node) {
        String sql = "UPDATE saga_nodes SET title = ?, content = ?, location = ?, latitude = ?, longitude = ?, node_time = ?, photos = ?, is_milestone = ?, sort_order = ? WHERE id = ? AND saga_id = ?";
        return jdbcTemplate.update(sql,
                node.getTitle(), node.getContent(), node.getLocation(),
                node.getLatitude(), node.getLongitude(),
                node.getNodeTime() != null ? java.sql.Timestamp.valueOf(node.getNodeTime()) : null,
                node.getPhotos(), node.isMilestone(), node.getSortOrder(),
                node.getId(), node.getSagaId());
    }

    public int delete(Long sagaId, Long nodeId) {
        return jdbcTemplate.update("DELETE FROM saga_nodes WHERE id = ? AND saga_id = ?", nodeId, sagaId);
    }

    public int deleteBySagaId(Long sagaId) {
        return jdbcTemplate.update("DELETE FROM saga_nodes WHERE saga_id = ?", sagaId);
    }

    public int countBySagaId(Long sagaId) {
        String sql = "SELECT COUNT(*) FROM saga_nodes WHERE saga_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sagaId);
        return count != null ? count : 0;
    }

    public boolean hasPhotos(Long sagaId) {
        String sql = "SELECT COUNT(*) FROM saga_nodes WHERE saga_id = ? AND photos IS NOT NULL AND photos != ''";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sagaId);
        return count != null && count > 0;
    }

    public List<java.time.LocalDateTime> findRecentNodeTimes(Long userId, int limit) {
        String sql = "SELECT DISTINCT DATE(sn.node_time) FROM saga_nodes sn JOIN sagas s ON sn.saga_id = s.id WHERE s.user_id = ? AND sn.node_time IS NOT NULL ORDER BY DATE(sn.node_time) DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getTimestamp(1).toLocalDateTime().toLocalDate().atStartOfDay(), userId, limit);
    }
}
