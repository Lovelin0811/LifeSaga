package com.lovelin.lifesaga.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lovelin.lifesaga.dto.UserVO;
import com.lovelin.lifesaga.model.User;
import com.lovelin.lifesaga.repository.UserRepository;
import com.lovelin.lifesaga.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Environment environment;

    @Value("${wechat.app-id:}")
    private String appId;

    @Value("${wechat.app-secret:}")
    private String appSecret;

    @Value("${auth.dev-login-enabled:false}")
    private boolean devLoginEnabled;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, Environment environment) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.environment = environment;
    }

    public Map<String, Object> wechatLogin(String code) {
        String openid;

        if (appId.isEmpty() || appSecret.isEmpty()) {
            boolean isDev = java.util.Arrays.asList(environment.getActiveProfiles()).contains("dev");
            if (!isDev) {
                throw new RuntimeException("生产环境未配置微信 appId/appSecret");
            }
            if (!devLoginEnabled) {
                throw new RuntimeException("本地登录未启用");
            }
            openid = "dev_" + code;
        } else {
            // 生产环境：调用微信 jscode2session 接口
            try {
                String url = "https://api.weixin.qq.com/sns/jscode2session"
                        + "?appid=" + appId
                        + "&secret=" + appSecret
                        + "&js_code=" + code
                        + "&grant_type=authorization_code";
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();
                HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
                JsonNode json = objectMapper.readTree(resp.body());

                if (json.has("errcode")) {
                    throw new RuntimeException("微信登录失败: " + json.get("errmsg").asText());
                }
                openid = json.get("openid").asText();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("微信登录异常: " + e.getMessage());
            }
        }

        User user = userRepository.findByOpenid(openid)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setOpenid(openid);
                    newUser.setNickname("");
                    newUser.setAvatarUrl("");
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getId());
        return Map.of(
                "token", token,
                "user", UserVO.from(user)
        );
    }
}
