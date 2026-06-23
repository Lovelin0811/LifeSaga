package com.lovelin.lifesaga.identity.application.service;

import com.lovelin.lifesaga.identity.application.command.UpdateUserProfileCommand;
import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class UpdateUserProfileApplicationService {

    private final UserRepository userRepository;
    private final Clock clock;

    public UpdateUserProfileApplicationService(UserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Transactional
    public User updateUserProfile(UpdateUserProfileCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("更新用户资料命令不能为空");
        }

        User user = userRepository.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalStateException("用户不存在"));
        user.updateProfile(
                command.userNickname(),
                command.userAvatarUrl(),
                LocalDateTime.now(clock)
        );
        return userRepository.save(user);
    }
}
