package com.lovelin.lifesaga.controller;

import com.lovelin.lifesaga.model.Saga;
import com.lovelin.lifesaga.service.SagaService;
import com.lovelin.lifesaga.service.NodeService;
import com.lovelin.lifesaga.dto.PublicSagaVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sagas")
public class SagaController {

    private final SagaService sagaService;
    private final NodeService nodeService;

    public SagaController(SagaService sagaService, NodeService nodeService) {
        this.sagaService = sagaService;
        this.nodeService = nodeService;
    }

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @GetMapping
    public Map<String, Object> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Saga> sagas = sagaService.listByUserId(userId);
        return Map.of("code", 200, "data", sagas, "message", "success");
    }

    @GetMapping("/public")
    public Map<String, Object> publicList() {
        List<PublicSagaVO> sagas = sagaService.listPublic().stream()
                .map(PublicSagaVO::from)
                .toList();
        return Map.of("code", 200, "data", sagas, "message", "success");
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Saga saga, HttpServletRequest request) {
        Long userId = getUserId(request);
        saga.setUserId(userId);
        Saga created = sagaService.create(saga);
        return Map.of("code", 200, "data", created, "message", "success");
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        Saga saga = sagaService.getById(id);
        if (!saga.getUserId().equals(userId) && !saga.isPublic()) {
            return Map.of("code", 403, "message", "无权访问");
        }
        var nodes = nodeService.listBySagaId(id, userId);
        return Map.of("code", 200, "data", Map.of("saga", saga, "nodes", nodes), "message", "success");
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Saga saga, HttpServletRequest request) {
        Long userId = getUserId(request);
        Saga existing = sagaService.getById(id);
        if (!existing.getUserId().equals(userId)) {
            return Map.of("code", 403, "message", "无权修改");
        }
        saga.setId(id);
        Saga updated = sagaService.update(saga);
        return Map.of("code", 200, "data", updated, "message", "success");
    }

    @PutMapping("/{id}/complete")
    public Map<String, Object> complete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        Saga existing = sagaService.getById(id);
        if (!existing.getUserId().equals(userId)) {
            return Map.of("code", 403, "message", "无权修改");
        }
        Saga completed = sagaService.complete(id);
        return Map.of("code", 200, "data", completed, "message", "success");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getUserId(request);
        Saga existing = sagaService.getById(id);
        if (!existing.getUserId().equals(userId)) {
            return Map.of("code", 403, "message", "无权删除");
        }
        sagaService.delete(id);
        return Map.of("code", 200, "message", "success");
    }
}
