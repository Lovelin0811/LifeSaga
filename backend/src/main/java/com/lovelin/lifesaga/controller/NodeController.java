package com.lovelin.lifesaga.controller;

import com.lovelin.lifesaga.model.SagaNode;
import com.lovelin.lifesaga.service.NodeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sagas/{sagaId}/nodes")
public class NodeController {

    private final NodeService nodeService;

    public NodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping
    public Map<String, Object> list(@PathVariable Long sagaId, HttpServletRequest request) {
        List<SagaNode> nodes = nodeService.listBySagaId(sagaId);
        return Map.of("code", 200, "data", nodes, "message", "success");
    }

    @PostMapping
    public Map<String, Object> create(@PathVariable Long sagaId, @RequestBody SagaNode node, HttpServletRequest request) {
        node.setSagaId(sagaId);
        SagaNode created = nodeService.create(node);
        return Map.of("code", 200, "data", created, "message", "success");
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long sagaId, @PathVariable Long id, HttpServletRequest request) {
        SagaNode node = nodeService.getById(sagaId, id);
        return Map.of("code", 200, "data", node, "message", "success");
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long sagaId, @PathVariable Long id,
                                      @RequestBody SagaNode node, HttpServletRequest request) {
        node.setId(id);
        node.setSagaId(sagaId);
        SagaNode updated = nodeService.update(node);
        return Map.of("code", 200, "data", updated, "message", "success");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long sagaId, @PathVariable Long id, HttpServletRequest request) {
        nodeService.delete(sagaId, id);
        return Map.of("code", 200, "message", "success");
    }
}
