package com.lovelin.lifesaga.identity.infrastructure.wechat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;

@Component
public class HttpWechatOpenIdResolver implements WechatOpenIdResolver {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Environment environment;
    private final String appId;
    private final String appSecret;
    private final boolean devLoginEnabled;

    @Autowired
    public HttpWechatOpenIdResolver(
            Environment environment,
            @Value("${wechat.app-id:}") String appId,
            @Value("${wechat.app-secret:}") String appSecret,
            @Value("${auth.dev-login-enabled:false}") boolean devLoginEnabled,
            @Value("${wechat.connect-timeout:5s}") Duration connectTimeout,
            @Value("${wechat.read-timeout:5s}") Duration readTimeout
    ) {
        this(
                environment,
                appId,
                appSecret,
                devLoginEnabled,
                HttpClient.newBuilder().connectTimeout(connectTimeout).build(),
                new ObjectMapper(),
                readTimeout
        );
    }

    HttpWechatOpenIdResolver(
            Environment environment,
            String appId,
            String appSecret,
            boolean devLoginEnabled,
            HttpClient httpClient,
            ObjectMapper objectMapper,
            Duration readTimeout
    ) {
        this.environment = environment;
        this.appId = appId;
        this.appSecret = appSecret;
        this.devLoginEnabled = devLoginEnabled;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.readTimeout = readTimeout;
    }

    private final Duration readTimeout;

    @Override
    public String resolveOpenId(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code不能为空");
        }

        if (appId.isBlank() || appSecret.isBlank()) {
            boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");
            if (!isDev) {
                throw new IllegalStateException("生产环境未配置微信 appId/appSecret");
            }
            if (!devLoginEnabled) {
                throw new IllegalStateException("本地登录未启用");
            }
            return "dev_" + code;
        }

        try {
            String url = "https://api.weixin.qq.com/sns/jscode2session"
                    + "?appid=" + appId
                    + "&secret=" + appSecret
                    + "&js_code=" + code
                    + "&grant_type=authorization_code";
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(readTimeout)
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode jsonNode = objectMapper.readTree(httpResponse.body());
            if (jsonNode.has("errcode")) {
                throw new IllegalStateException("微信登录失败: " + jsonNode.path("errmsg").asText());
            }
            String openId = jsonNode.path("openid").asText("");
            if (openId.isBlank()) {
                throw new IllegalStateException("微信登录失败: 缺少 openid");
            }
            return openId;
        } catch (IllegalStateException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("微信登录异常: " + exception.getMessage(), exception);
        }
    }
}
