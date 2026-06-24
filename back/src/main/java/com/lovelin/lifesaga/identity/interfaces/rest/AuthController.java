package com.lovelin.lifesaga.identity.interfaces.rest;

import com.lovelin.lifesaga.identity.application.service.WechatLoginApplicationService;
import com.lovelin.lifesaga.identity.domain.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final WechatLoginApplicationService wechatLoginApplicationService;

    public AuthController(WechatLoginApplicationService wechatLoginApplicationService) {
        this.wechatLoginApplicationService = wechatLoginApplicationService;
    }

    @PostMapping("/wechat-login")
    public UserController.ApiResponse<LoginResponse> wechatLogin(@RequestBody WechatLoginRequest wechatLoginRequest) {
        WechatLoginApplicationService.LoginResult loginResult =
                wechatLoginApplicationService.wechatLogin(wechatLoginRequest.code());
        return UserController.ApiResponse.success(LoginResponse.from(loginResult.token(), loginResult.user()));
    }

    public record WechatLoginRequest(String code) {
    }

    public record LoginResponse(String token, UserController.UserResponse user) {

        static LoginResponse from(String token, User user) {
            return new LoginResponse(token, UserController.UserResponse.from(user));
        }
    }
}
