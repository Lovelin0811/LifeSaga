package com.lovelin.lifesaga.achievement.domain.repository;

import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaRarity;
import com.lovelin.lifesaga.saga.domain.model.SagaType;

import java.time.LocalDate;
import java.util.List;

public interface AchievementProgressRepository {

    int countSagasByUserId(UserId userId);

    List<SagaType> findDistinctSagaTypesByUserId(UserId userId);

    int countCompletedSagasByType(UserId userId, SagaType sagaType);

    boolean hasSagaWithRarity(UserId userId, SagaRarity sagaRarity);

    boolean sagaHasPhotos(SagaId sagaId);

    List<LocalDate> findRecentNodeDates(UserId userId, int limit);
}
