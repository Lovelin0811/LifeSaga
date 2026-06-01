package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.Saga;
import com.lovelin.lifesaga.model.SagaNode;
import com.lovelin.lifesaga.repository.NodeFavoriteRepository;
import com.lovelin.lifesaga.repository.SagaRepository;
import com.lovelin.lifesaga.repository.NodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SagaService {

    private final SagaRepository sagaRepository;
    private final NodeRepository nodeRepository;
    private final NodeFavoriteRepository nodeFavoriteRepository;
    private final AchievementService achievementService;

    public SagaService(SagaRepository sagaRepository, NodeRepository nodeRepository,
                       NodeFavoriteRepository nodeFavoriteRepository,
                       AchievementService achievementService) {
        this.sagaRepository = sagaRepository;
        this.nodeRepository = nodeRepository;
        this.nodeFavoriteRepository = nodeFavoriteRepository;
        this.achievementService = achievementService;
    }

    public List<Saga> listByUserId(Long userId) {
        return sagaRepository.findByUserId(userId);
    }

    public List<Saga> listByUserId(Long userId, String keyword) {
        return sagaRepository.findByUserId(userId, keyword);
    }

    public List<Saga> listPublic(String keyword) {
        return sagaRepository.findPublic(keyword);
    }

    public Saga getById(Long id) {
        return sagaRepository.findById(id).orElseThrow(() ->
                new RuntimeException("副本不存在"));
    }

    public Saga getDetail(Long id) {
        Saga saga = getById(id);
        // 不在这里加载节点列表，由 controller 层调用 nodeService
        return saga;
    }

    @Transactional
    public Saga create(Saga saga) {
        saga.setStatus("active");
        saga.setPublic(saga.isPublic());
        if (saga.getStartedAt() == null) {
            saga.setStartedAt(LocalDateTime.now());
        }
        saga.setNodeCount(0);
        saga.setRarity("common");
        Saga saved = sagaRepository.save(saga);
        achievementService.checkAchievementsOnSagaCreate(saved.getUserId());
        return saved;
    }

    @Transactional
    public Saga update(Saga saga) {
        Saga existing = getById(saga.getId());
        // 只允许修改安全字段，status/endedAt/nodeCount/rarity/userId 不允许通过此接口修改
        if (saga.getName() != null) existing.setName(saga.getName());
        if (saga.getType() != null) existing.setType(saga.getType());
        if (saga.getCoverUrl() != null) existing.setCoverUrl(saga.getCoverUrl());
        if (saga.getDescription() != null) existing.setDescription(saga.getDescription());
        existing.setPublic(saga.isPublic());
        sagaRepository.update(existing);
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        nodeFavoriteRepository.deleteBySagaId(id);
        nodeRepository.deleteBySagaId(id);
        sagaRepository.delete(id);
    }

    @Transactional
    public Saga complete(Long id) {
        Saga existing = getById(id);
        if (!"completed".equals(existing.getStatus())) {
            existing.setStatus("completed");
            existing.setEndedAt(LocalDateTime.now());
            sagaRepository.update(existing);
            achievementService.checkAchievementsOnSagaComplete(existing.getUserId(), existing.getType());
        } else if (existing.getEndedAt() == null) {
            existing.setEndedAt(LocalDateTime.now());
            sagaRepository.update(existing);
        }
        return existing;
    }

    public String calculateRarity(int nodeCount) {
        if (nodeCount >= 30) return "mythic";
        if (nodeCount >= 21) return "legendary";
        if (nodeCount >= 11) return "epic";
        if (nodeCount >= 6) return "rare";
        if (nodeCount >= 3) return "uncommon";
        return "common";
    }

    @Transactional
    public void updateSagaNodeCount(Long sagaId) {
        int count = nodeRepository.countBySagaId(sagaId);
        String rarity = calculateRarity(count);
        sagaRepository.updateNodeCountAndRarity(sagaId, count, rarity);
    }
}
