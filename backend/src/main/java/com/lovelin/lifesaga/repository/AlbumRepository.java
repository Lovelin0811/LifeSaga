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
                    CONVERT(jt.url USING utf8mb4) COLLATE utf8mb4_unicode_ci AS url,
                    n.title,
                    s.name AS saga_name,
                    COALESCE(n.node_time, n.created_at) AS photo_time,
                    jt.photo_index
                FROM saga_nodes n
                JOIN sagas s ON s.id = n.saga_id
                JOIN JSON_TABLE(
                    CASE WHEN JSON_VALID(n.photos) THEN n.photos ELSE JSON_ARRAY() END,
                    '$[*]' COLUMNS (
                        photo_index FOR ORDINALITY,
                        url VARCHAR(1024) PATH '$'
                    )
                ) jt
                WHERE s.user_id = ?
                  AND n.photos IS NOT NULL
                  AND n.photos != ''
                  AND n.photos != '[]'
                  AND jt.url IS NOT NULL
                  AND jt.url != ''
                UNION ALL
                SELECT
                    CONVERT(n.photos USING utf8mb4) COLLATE utf8mb4_unicode_ci AS url,
                    n.title,
                    s.name AS saga_name,
                    COALESCE(n.node_time, n.created_at) AS photo_time,
                    1 AS photo_index
                FROM saga_nodes n
                JOIN sagas s ON s.id = n.saga_id
                WHERE s.user_id = ?
                  AND n.photos IS NOT NULL
                  AND n.photos != ''
                  AND n.photos != '[]'
                  AND NOT JSON_VALID(n.photos)
                UNION ALL
                SELECT
                    CONVERT(s.cover_url USING utf8mb4) COLLATE utf8mb4_unicode_ci AS url,
                    s.name AS title,
                    s.name AS saga_name,
                    COALESCE(s.started_at, s.created_at) AS photo_time,
                    0 AS photo_index
                FROM sagas s
                WHERE s.user_id = ?
                  AND s.cover_url IS NOT NULL
                  AND s.cover_url != ''
                ORDER BY photo_time DESC, photo_index ASC
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            AlbumItemVO item = new AlbumItemVO();
            item.setUrl(rs.getString("url"));
            item.setTitle(rs.getString("title"));
            item.setSagaName(rs.getString("saga_name"));
            java.sql.Timestamp photoTime = rs.getTimestamp("photo_time");
            if (photoTime != null) item.setNodeTime(photoTime.toLocalDateTime());
            return item;
        }, userId, userId, userId);
    }
}
