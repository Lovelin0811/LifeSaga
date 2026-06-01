package com.lovelin.lifesaga.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NodeFavoriteRepository {

    private final JdbcTemplate jdbcTemplate;

    public NodeFavoriteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isFavorited(Long userId, Long nodeId) {
        String sql = "SELECT COUNT(*) FROM node_favorites WHERE user_id = ? AND node_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, nodeId);
        return count != null && count > 0;
    }

    public boolean toggle(Long userId, Long nodeId) {
        if (isFavorited(userId, nodeId)) {
            jdbcTemplate.update("DELETE FROM node_favorites WHERE user_id = ? AND node_id = ?", userId, nodeId);
            return false;
        }
        jdbcTemplate.update("INSERT INTO node_favorites (user_id, node_id) VALUES (?, ?)", userId, nodeId);
        return true;
    }

    public int deleteByNodeId(Long nodeId) {
        return jdbcTemplate.update("DELETE FROM node_favorites WHERE node_id = ?", nodeId);
    }

    public int deleteBySagaId(Long sagaId) {
        String sql = "DELETE nf FROM node_favorites nf JOIN saga_nodes sn ON nf.node_id = sn.id WHERE sn.saga_id = ?";
        return jdbcTemplate.update(sql, sagaId);
    }

    public List<Long> findFavoritedNodeIds(Long userId, List<Long> nodeIds) {
        if (nodeIds == null || nodeIds.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(nodeIds.size(), "?"));
        String sql = "SELECT node_id FROM node_favorites WHERE user_id = ? AND node_id IN (" + placeholders + ")";
        Object[] params = new Object[nodeIds.size() + 1];
        params[0] = userId;
        for (int i = 0; i < nodeIds.size(); i++) {
            params[i + 1] = nodeIds.get(i);
        }
        return jdbcTemplate.queryForList(sql, Long.class, params);
    }
}
