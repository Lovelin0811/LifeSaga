package com.lovelin.lifesaga.saga.application.service;

import com.lovelin.lifesaga.saga.application.command.DeleteSagaCommand;
import com.lovelin.lifesaga.saga.domain.model.Saga;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeFavoriteRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaNodeRepository;
import com.lovelin.lifesaga.saga.domain.repository.SagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteSagaApplicationService {

    private final SagaRepository sagaRepository;
    private final SagaNodeRepository sagaNodeRepository;
    private final SagaNodeFavoriteRepository sagaNodeFavoriteRepository;

    public DeleteSagaApplicationService(
            SagaRepository sagaRepository,
            SagaNodeRepository sagaNodeRepository,
            SagaNodeFavoriteRepository sagaNodeFavoriteRepository
    ) {
        this.sagaRepository = sagaRepository;
        this.sagaNodeRepository = sagaNodeRepository;
        this.sagaNodeFavoriteRepository = sagaNodeFavoriteRepository;
    }

    @Transactional
    public void deleteSaga(DeleteSagaCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("删除副本命令不能为空");
        }

        Saga saga = sagaRepository.findBySagaId(command.sagaId())
                .orElseThrow(() -> new IllegalStateException("副本不存在"));

        saga.requireOwner(command.sagaOwnerId());
        sagaNodeFavoriteRepository.deleteBySagaId(command.sagaId());
        sagaNodeRepository.deleteBySagaId(command.sagaId());
        sagaRepository.deleteBySagaId(command.sagaId());
    }
}
