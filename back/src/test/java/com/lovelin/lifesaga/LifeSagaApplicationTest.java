package com.lovelin.lifesaga;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("暂时跳过完整 Spring 上下文启动测试，避免依赖本地 MySQL 导致测试卡住")
class LifeSagaApplicationTest {

    @Test
    void contextLoads() {
    }
}
