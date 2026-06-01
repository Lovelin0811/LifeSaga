package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.SagaNode;
import com.lovelin.lifesaga.repository.NodeFavoriteRepository;
import com.lovelin.lifesaga.repository.NodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;
    private final NodeFavoriteRepository nodeFavoriteRepository;
    private final SagaService sagaService;
    private final AchievementService achievementService;

    public NodeService(NodeRepository nodeRepository, NodeFavoriteRepository nodeFavoriteRepository, SagaService sagaService,
                       AchievementService achievementService) {
        this.nodeRepository = nodeRepository;
        this.nodeFavoriteRepository = nodeFavoriteRepository;
        this.sagaService = sagaService;
        this.achievementService = achievementService;
    }

    public List<SagaNode> listBySagaId(Long sagaId) {
        return listBySagaId(sagaId, null);
    }

    public List<SagaNode> listBySagaId(Long sagaId, Long userId) {
        sagaService.getById(sagaId);
        List<SagaNode> nodes = nodeRepository.findBySagaId(sagaId);
        if (userId != null) {
            java.util.Set<Long> favoritedIds = new java.util.HashSet<>(nodeFavoriteRepository.findFavoritedNodeIds(
                    userId,
                    nodes.stream().map(SagaNode::getId).toList()
            ));
            for (SagaNode node : nodes) {
                node.setFavorited(favoritedIds.contains(node.getId()));
            }
        }
        return nodes;
    }

    public SagaNode getById(Long sagaId, Long nodeId) {
        return getById(sagaId, nodeId, null);
    }

    public SagaNode getById(Long sagaId, Long nodeId, Long userId) {
        sagaService.getById(sagaId);
        SagaNode node = nodeRepository.findById(nodeId).orElseThrow(() ->
                new RuntimeException("节点不存在"));
        if (!node.getSagaId().equals(sagaId)) {
            throw new RuntimeException("节点不属于该副本");
        }
        if (userId != null) {
            node.setFavorited(nodeFavoriteRepository.isFavorited(userId, nodeId));
        }
        return node;
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
        nodeFavoriteRepository.deleteByNodeId(nodeId);
        nodeRepository.delete(sagaId, nodeId);
        sagaService.updateSagaNodeCount(sagaId);
    }

    @Transactional
    public SagaNode toggleMilestone(Long sagaId, Long nodeId) {
        SagaNode node = getById(sagaId, nodeId);
        node.setMilestone(!node.isMilestone());
        nodeRepository.update(node);
        return nodeRepository.findById(nodeId).orElseThrow();
    }

    @Transactional
    public boolean toggleFavorite(Long sagaId, Long nodeId, Long userId) {
        getById(sagaId, nodeId, userId);
        return nodeFavoriteRepository.toggle(userId, nodeId);
    }
}
