package com.lovelin.lifesaga.identity.application.service;

import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserQueryApplicationService {

    private final UserRepository userRepository;

    public UserQueryApplicationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User getUserById(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户 ID 不能为空");
        }
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("用户不存在"));
    }
}
