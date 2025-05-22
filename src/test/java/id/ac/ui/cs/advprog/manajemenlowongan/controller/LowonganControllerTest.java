package id.ac.ui.cs.advprog.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.service.LowonganService;

import com.fasterxml.jackson.databind.ObjectMapper;

import id.ac.ui.cs.advprog.mendaftarlowongan.service.LamaranService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
        "jwt.secret=fakeTestSecretKeyThatIsLongEnoughForHmacSha",
        "jwt.expiration=3600000"
})
@Import({SecurityConfig.class, id.ac.ui.cs.advprog.authjwt.testconfig.TestSecurityBeansConfig.class})
@WebMvcTest(LowonganController.class)
class LowonganControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LowonganService lowonganService;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    private Lowongan dummyLowongan;
    private UUID dummyId;

    @BeforeEach
    void setUp() {
        dummyId = UUID.randomUUID();
        dummyLowongan = new Lowongan();
        dummyLowongan.setId(dummyId);
        dummyLowongan.setMatkul("Advanced Programming");
        dummyLowongan.setTahun(2025);
        dummyLowongan.setTerm("Genap");
        dummyLowongan.setTotalAsdosNeeded(5);
        dummyLowongan.setTotalAsdosRegistered(35);
        dummyLowongan.setTotalAsdosAccepted(3);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAddLowongan() throws Exception {
        when(lowonganService.addLowongan(any(Lowongan.class))).thenReturn(dummyLowongan);

        mockMvc.perform(post("/api/lowongan/all/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyLowongan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummyLowongan.getId().toString()))
                .andExpect(jsonPath("$.matkul").value(dummyLowongan.getMatkul()))
                .andExpect(jsonPath("$.tahun").value(dummyLowongan.getTahun()))
                .andExpect(jsonPath("$.term").value(dummyLowongan.getTerm()))
                .andExpect(jsonPath("$.totalAsdosNeeded").value(dummyLowongan.getTotalAsdosNeeded()))
                .andExpect(jsonPath("$.totalAsdosRegistered").value(dummyLowongan.getTotalAsdosRegistered()))
                .andExpect(jsonPath("$.totalAsdosAccepted").value(dummyLowongan.getTotalAsdosAccepted()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetLowonganById() throws Exception {
        when(lowonganService.getLowonganById(dummyId)).thenReturn(dummyLowongan);

        mockMvc.perform(get("/api/lowongan/user/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummyLowongan.getId().toString()));
    }

    @Test
    void testGetLowonganById_NotFound() throws Exception {
        when(lowonganService.getLowonganById(dummyId)).thenThrow(new RuntimeException("Lowongan tidak ditemukan"));

        mockMvc.perform(get("/api/lowongan/user/" + dummyId))
                .andExpect(status().isInternalServerError());
    }

    @TestConfiguration
    static class MockServiceConfig {
        @Bean
        public LowonganService lowonganService() {
            return mock(LowonganService.class);
        }
    }
}