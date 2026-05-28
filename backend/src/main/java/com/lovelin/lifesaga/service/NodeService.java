package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.SagaNode;
import com.lovelin.lifesaga.repository.NodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;
    private final SagaService sagaService;
    private final AchievementService achievementService;

    public NodeService(NodeRepository nodeRepository, SagaService sagaService,
                       AchievementService achievementService) {
        this.nodeRepository = nodeRepository;
        this.sagaService = sagaService;
        this.achievementService = achievementService;
    }

    public List<SagaNode> listBySagaId(Long sagaId) {
        sagaService.getById(sagaId);
        return nodeRepository.findBySagaId(sagaId);
    }

    public SagaNode getById(Long sagaId, Long nodeId) {
        sagaService.getById(sagaId);
        return nodeRepository.findById(nodeId).orElseThrow(() ->
                new RuntimeException("节点不存在"));
    }

    @Transactional
    public SagaNode create(SagaNode node) {
        SagaNode saved = nodeRepository.save(node);
        sagaService.updateSagaNodeCount(node.getSagaId());
        // 获取 saga 的 userId 来检查成就
        var saga = sagaService.getById(node.getSagaId());
        achievementService.checkAchievementsOnNodeCreate(saga.getUserId(), node.getSagaId());
        return saved;
    }

    @Transactional
    public SagaNode update(SagaNode node) {
        getById(node.getSagaId(), node.getId());
        nodeRepository.update(node);
        return nodeRepository.findById(node.getId()).orElseThrow();
    }

    @Transactional
    public void delete(Long sagaId, Long nodeId) {
        getById(sagaId, nodeId);
        nodeRepository.delete(nodeId);
        sagaService.updateSagaNodeCount(sagaId);
    }
}
