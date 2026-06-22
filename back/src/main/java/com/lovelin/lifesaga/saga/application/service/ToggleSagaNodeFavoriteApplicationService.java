package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.ToggleSagaNodeFavoriteCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeFavoriteRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ToggleSagaNodeFavoriteApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;
    private final SagaNodeFavoriteRepository sagaNodeFavoriteRepository;

    public ToggleSagaNodeFavoriteApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository,
            SagaNodeFavoriteRepository sagaNodeFavoriteRepository
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
        this.sagaNodeFavoriteRepository = sagaNodeFavoriteRepository;
    }

    @Transactional
    public boolean toggleFavorite(ToggleSagaNodeFavoriteCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("切换节点收藏命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));
        saga.requireOwner(command.sagaOwnerId());

        sagaNodeRepository.findBySagaNodeId(command.sagaNodeId())
                .filter(foundSagaNode -> command.sagaId().equals(foundSagaNode.sagaId()))
                .orElseThrow(() -> new IllegalStateException("节点不存在"));

        return sagaNodeFavoriteRepository.toggle(command.sagaOwnerId(), command.sagaNodeId());
    }
}
