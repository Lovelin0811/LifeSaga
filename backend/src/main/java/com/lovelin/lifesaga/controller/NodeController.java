package com.lovelin.lifesaga.controller;

import com.lovelin.lifesaga.model.Saga;
import com.lovelin.lifesaga.model.SagaNode;
import com.lovelin.lifesaga.service.NodeService;
import com.lovelin.lifesaga.service.SagaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sagas/{sagaId}/nodes")
public class NodeController {

    private final NodeService nodeService;
    private final SagaService sagaService;

    public NodeController(NodeService nodeService, SagaService sagaService) {
        this.nodeService = nodeService;
        this.sagaService = sagaService;
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /** 校验 sagaId 归属当前用户 */
    private void verifyOwnership(Long sagaId, Long userId) {
        Saga saga = sagaService.getById(sagaId);
        if (!saga.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该副本");
        }
    }

    @GetMapping
    public Map<String, Object> list(@PathVariable Long sagaId, HttpServletRequest request) {
        Long userId = getUserId(request);
        verifyOwnership(sagaId, userId);
        List<SagaNode> nodes = nodeService.listBySagaId(sagaId, userId);
        return Map.of("code", 200, "data", nodes, "message", "success");
    }

    @PostMapping
    public Map<String, Object> create(@PathVariable Long sagaId, @RequestBody SagaNode node, HttpServletRequest request) {
        Long userId = getUserId(request);
        verifyOwnership(sagaId, userId);
        node.setSagaId(sagaId);
        SagaNode created = nodeService.create(node);
        return Map.of("code", 200, "data", created, "message", "success");
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long sagaId, @PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        verifyOwnership(sagaId, userId);
        SagaNode node = nodeService.getById(sagaId, id, userId);
        return Map.of("code", 200, "data", node, "message", "success");
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long sagaId, @PathVariable Long id,
                                      @RequestBody SagaNode node, HttpServletRequest request) {
        Long userId = getUserId(request);
        verifyOwnership(sagaId, userId);
        node.setId(id);
        node.setSagaId(sagaId);
        SagaNode updated = nodeService.update(node);
        return Map.of("code", 200, "data", updated, "message", "success");
    }

    @PutMapping("/{id}/toggle-milestone")
    public Map<String, Object> toggleMilestone(@PathVariable Long sagaId, @PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        verifyOwnership(sagaId, userId);
        SagaNode updated = nodeService.toggleMilestone(sagaId, id);
        return Map.of("code", 200, "data", updated, "message", "success");
    }

    @PutMapping("/{id}/favorite")
    public Map<String, Object> toggleFavorite(@PathVariable Long sagaId, @PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        verifyOwnership(sagaId, userId);
        boolean favorited = nodeService.toggleFavorite(sagaId, id, userId);
        return Map.of("code", 200, "data", Map.of("favorited", favorited), "message", "success");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long sagaId, @PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        verifyOwnership(sagaId, userId);
        nodeService.delete(sagaId, id);
        return Map.of("code", 200, "message", "success");
    }
}
