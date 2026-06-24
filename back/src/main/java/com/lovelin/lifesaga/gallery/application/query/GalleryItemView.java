package com.lovelin.lifesaga.gallery.application.query;

import java.time.LocalDateTime;

public record GalleryItemView(
        String url,
        String title,
        String sagaName,
        LocalDateTime photoTime
) {
}
