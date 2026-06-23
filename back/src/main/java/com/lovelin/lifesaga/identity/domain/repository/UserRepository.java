package com.lovelin.lifesaga.identity.domain.repository;

import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUserId(UserId userId);

    Optional<User> findByUserOpenId(UserOpenId userOpenId);

    User save(User user);
}
