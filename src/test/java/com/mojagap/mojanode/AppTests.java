package com.mojagap.mojanode;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppTests {

    @Test
    void contextLoads() {
        System.out.println(System.getProperty("user.dir"));
        System.out.println(AppTests.class.getSimpleName());
        System.out.println(AppTests.class.getName());
        System.out.println(AppTests.class.getCanonicalName());
    }

}
