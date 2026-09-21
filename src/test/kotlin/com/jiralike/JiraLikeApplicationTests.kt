package com.jiralike

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class JiraLikeApplicationTests {

    @Test
    fun contextLoads() {
        // Verifies that the Spring application context loads successfully
    }
}
