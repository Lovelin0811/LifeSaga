package com.lovelin.lifesaga.gallery.infrastructure.persistence.record;

import java.time.LocalDateTime;

public class GalleryItemRecord {

    private String url;
    private String title;
    private String sagaName;
    private LocalDateTime photoTime;
    private Integer photoIndex;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSagaName() {
        return sagaName;
    }

    public void setSagaName(String sagaName) {
        this.sagaName = sagaName;
    }

    public LocalDateTime getPhotoTime() {
        return photoTime;
    }

    public void setPhotoTime(LocalDateTime photoTime) {
        this.photoTime = photoTime;
    }

    public Integer getPhotoIndex() {
        return photoIndex;
    }

    public void setPhotoIndex(Integer photoIndex) {
        this.photoIndex = photoIndex;
    }
}
