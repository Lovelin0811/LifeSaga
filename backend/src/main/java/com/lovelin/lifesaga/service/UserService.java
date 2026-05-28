package com.lovelin.lifesaga.service;

import com.lovelin.lifesaga.model.User;
import com.lovelin.lifesaga.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("用户不存在"));
    }

    public User update(Long userId, User user) {
        User existing = getById(userId);
        if (user.getNickname() != null) existing.setNickname(user.getNickname());
        if (user.getAvatarUrl() != null) existing.setAvatarUrl(user.getAvatarUrl());
        userRepository.update(existing);
        return getById(userId);
    }
}
