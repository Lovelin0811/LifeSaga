package com.lovelin.lifesaga.dto;

import com.lovelin.lifesaga.model.Saga;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class PublicSagaVO {
    private Long id;
    private String name;
    private String type;
    private String coverUrl;
    private String description;
    private String status;
    private boolean isPublic;
    private int nodeCount;
    private String rarity;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime updatedAt;

    public static PublicSagaVO from(Saga saga) {
        PublicSagaVO vo = new PublicSagaVO();
        vo.id = saga.getId();
        vo.name = saga.getName();
        vo.type = saga.getType();
        vo.coverUrl = saga.getCoverUrl();
        vo.description = saga.getDescription();
        vo.status = saga.getStatus();
        vo.isPublic = saga.isPublic();
        vo.nodeCount = saga.getNodeCount();
        vo.rarity = saga.getRarity();
        vo.startedAt = saga.getStartedAt();
        vo.endedAt = saga.getEndedAt();
        vo.updatedAt = saga.getUpdatedAt();
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @JsonProperty("isPublic")
    public boolean isPublic() { return isPublic; }
    @JsonProperty("isPublic")
    public void setPublic(boolean aPublic) { isPublic = aPublic; }
    public int getNodeCount() { return nodeCount; }
    public void setNodeCount(int nodeCount) { this.nodeCount = nodeCount; }
    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
