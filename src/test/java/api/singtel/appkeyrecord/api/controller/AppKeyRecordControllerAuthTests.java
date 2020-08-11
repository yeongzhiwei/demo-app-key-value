package api.singtel.appkeyrecord.api.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import com.google.gson.Gson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import api.singtel.appkeyrecord.api.model.AppKeyRecord;
import api.singtel.appkeyrecord.api.service.AppKeyRecordService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AppKeyRecordControllerAuthTests {

    @MockBean AppKeyRecordService service;    
    @Autowired private MockMvc mockMvc;
    Gson gson = new Gson();

    @BeforeEach
    public void mockServiceMethods() {
        AppKeyRecord record = new AppKeyRecord("app1", "key1", "value1", 10);
        when(service.get("app1", "key1")).thenReturn(record);
        when(service.create(eq("app1"), any(AppKeyRecordDTO.class))).thenReturn(record);
    }

    @Test
    public void getValidCredentialsShouldReturnOk() throws Exception {
        this.mockMvc.perform(get("/apps/app1/keys/key1")
                .with(httpBasic("user", "pass")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.value").value("value1"));
    }

    @Test
    public void getInvalidUserShouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(get("/apps/app1/keys/key1")
                .with(httpBasic("wrongUser", "pass")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getInvalidPassShouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(get("/apps/app1/keys/key1")
                .with(httpBasic("user", "wrongPass")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void postValidCredentialsShouldReturnCreated() throws Exception {       
        this.mockMvc.perform(post("/apps/app1/keys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(Map.of("key", "key1", "value", "value1")))
                .with(httpBasic("user", "pass")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.value").value("value1"));
    }

    @Test
    public void postInvalidCredentialsShouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(post("/apps/app1/keys")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(Map.of("key", "key1", "value", "value1")))
                .with(httpBasic("wrongUser", "wrongPass")))
            .andExpect(status().isUnauthorized());
    }
    
}