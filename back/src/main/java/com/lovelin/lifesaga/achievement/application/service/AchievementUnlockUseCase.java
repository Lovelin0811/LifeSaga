package com.lovelin.lifesaga.achievement.application.service;

import com.lovelin.lifesaga.achievement.domain.model.Achievement;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.saga.domain.model.SagaId;
import com.lovelin.lifesaga.saga.domain.model.SagaType;

import java.util.List;

public interface AchievementUnlockUseCase {

    List<Achievement> checkOnSagaCreate(UserId userId);

    List<Achievement> checkOnSagaComplete(UserId userId, SagaType sagaType);

    List<Achievement> checkOnNodeCreate(UserId userId, SagaId sagaId);
}
