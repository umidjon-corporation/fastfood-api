package com.project.fastfoodapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FastfoodApiApplicationTests {

    @Test
    void contextLoads() {
        assertEquals(ZoneId.systemDefault().getId(), "Asia/Tashkent");
    }

}
