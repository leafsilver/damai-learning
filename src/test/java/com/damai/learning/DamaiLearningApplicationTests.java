package com.damai.learning;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc
class DamaiLearningApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void helloEndpointReturnsRunningMessage() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("damai-learning is running"));
    }

    @Test
    void helloEndpointReturnsGreetingForValidRequest() throws Exception {
        mockMvc.perform(post("/api/hello")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":"小明"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("你好，小明"));
    }

    @Test
    void helloEndpointReturnsValidationErrorForBlankName() throws Exception {
        mockMvc.perform(post("/api/hello")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"name":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(40001))
                .andExpect(jsonPath("$.message").value("name不能为空"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
