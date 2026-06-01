package com.lovelin.lifesaga.repository;

import com.lovelin.lifesaga.dto.AlbumItemVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AlbumRepository {

    private final JdbcTemplate jdbcTemplate;

    public AlbumRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AlbumItemVO> findByUserId(Long userId) {
        String sql = """
                SELECT
                    COALESCE(n.cover_url, JSON_UNQUOTE(JSON_EXTRACT(n.photos, '$[0]'))) AS url,
                    n.title,
                    s.name AS saga_name,
                    n.node_time
                FROM saga_nodes n
                JOIN sagas s ON s.id = n.saga_id
                WHERE s.user_id = ?
                  AND (
                    n.cover_url IS NOT NULL AND n.cover_url != ''
                    OR n.photos IS NOT NULL AND n.photos != ''
                  )
                ORDER BY n.node_time DESC, n.created_at DESC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AlbumItemVO item = new AlbumItemVO();
            item.setUrl(rs.getString("url"));
            item.setTitle(rs.getString("title"));
            item.setSagaName(rs.getString("saga_name"));
            java.sql.Timestamp nodeTime = rs.getTimestamp("node_time");
            if (nodeTime != null) item.setNodeTime(nodeTime.toLocalDateTime());
            return item;
        }, userId);
    }
}
