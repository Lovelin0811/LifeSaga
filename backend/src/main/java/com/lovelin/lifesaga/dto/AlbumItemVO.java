package com.lovelin.lifesaga.dto;

import java.time.LocalDateTime;

public class AlbumItemVO {
    private String url;
    private String title;
    private String sagaName;
    private LocalDateTime nodeTime;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSagaName() { return sagaName; }
    public void setSagaName(String sagaName) { this.sagaName = sagaName; }
    public LocalDateTime getNodeTime() { return nodeTime; }
    public void setNodeTime(LocalDateTime nodeTime) { this.nodeTime = nodeTime; }
}
