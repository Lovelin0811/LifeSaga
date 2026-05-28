package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.Saga;
import com.lovelin.lifesaga.model.SagaNode;
import com.lovelin.lifesaga.repository.SagaRepository;
import com.lovelin.lifesaga.repository.NodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SagaService {

    private final SagaRepository sagaRepository;
    private final NodeRepository nodeRepository;
    private final AchievementService achievementService;

    public SagaService(SagaRepository sagaRepository, NodeRepository nodeRepository,
                       AchievementService achievementService) {
        this.sagaRepository = sagaRepository;
        this.nodeRepository = nodeRepository;
        this.achievementService = achievementService;
    }

    public List<Saga> listByUserId(Long userId) {
        return sagaRepository.findByUserId(userId);
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
        sagaRepository.update(existing);
        return existing;
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        nodeRepository.deleteBySagaId(id);
        sagaRepository.delete(id);
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
