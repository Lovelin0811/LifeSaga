package com.lovelin.lifesaga.identity.application.service;

import com.lovelin.lifesaga.identity.domain.model.User;
import com.lovelin.lifesaga.identity.domain.model.UserOpenId;
import com.lovelin.lifesaga.identity.domain.repository.UserRepository;
import com.lovelin.lifesaga.identity.infrastructure.security.JwtTokenService;
import com.lovelin.lifesaga.identity.infrastructure.wechat.WechatOpenIdResolver;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class WechatLoginApplicationService {

    private final UserRepository userRepository;
    private final WechatOpenIdResolver wechatOpenIdResolver;
    private final JwtTokenService jwtTokenService;
    private final Clock clock;

    public WechatLoginApplicationService(
            UserRepository userRepository,
            WechatOpenIdResolver wechatOpenIdResolver,
            JwtTokenService jwtTokenService,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.wechatOpenIdResolver = wechatOpenIdResolver;
        this.jwtTokenService = jwtTokenService;
        this.clock = clock;
    }

    public LoginResult wechatLogin(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code不能为空");
        }

        UserOpenId userOpenId = new UserOpenId(wechatOpenIdResolver.resolveOpenId(code));
        User user = userRepository.findByUserOpenId(userOpenId)
                .orElseGet(() -> userRepository.save(User.create(userOpenId, LocalDateTime.now(clock))));

        if (user.userId() == null) {
            throw new IllegalStateException("用户登录后缺少用户 ID");
        }

        return new LoginResult(
                jwtTokenService.generateToken(user.userId().value()),
                user
        );
    }

    public record LoginResult(String token, User user) {
    }
}
