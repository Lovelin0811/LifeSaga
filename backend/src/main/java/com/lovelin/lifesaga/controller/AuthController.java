package com.lovelin.lifesaga.controller;

import com.lovelin.lifesaga.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/wechat-login")
    public Map<String, Object> wechatLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return Map.of("code", 400, "message", "code不能为空");
        }
        Map<String, Object> result = authService.wechatLogin(code);
        return Map.of("code", 200, "data", result, "message", "success");
    }
}
