package com.codewithadel.platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired MockMvc mvc;

    @Test void healthIsUp() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test void returnsThreeProducts() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(3))
           .andExpect(jsonPath("$[0].name").value("Ceramic Vase"));
    }

    @Test void exposesVersion() throws Exception {
        mvc.perform(get("/api/version")).andExpect(status().isOk())
           .andExpect(jsonPath("$.application").value("product-api"));
    }
}
