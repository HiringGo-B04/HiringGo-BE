package id.ac.ui.cs.advprog.mendaftarlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
import id.ac.ui.cs.advprog.mendaftarlowongan.dto.LamaranDTO;
import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.service.LamaranService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestPropertySource(properties = {
        "jwt.secret=fakeTestSecretKeyThatIsLongEnoughForHmacSha",
        "jwt.expiration=3600000"
})
@Import({SecurityConfig.class, id.ac.ui.cs.advprog.authjwt.testconfig.TestSecurityBeansConfig.class})
@WebMvcTest(LamaranController.class)
class LamaranControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LamaranService lamaranService;

    private Lamaran dummyLamaran;
    private UUID dummyId;

    @BeforeEach
    void setUp() {
        dummyId = UUID.randomUUID();
        dummyLamaran = new Lamaran();
        dummyLamaran.setId(dummyId);
        dummyLamaran.setIpk(3.5f);
        dummyLamaran.setSks(20);
        dummyLamaran.setIdMahasiswa(UUID.randomUUID());
        dummyLamaran.setIdLowongan(UUID.randomUUID());
        dummyLamaran.setStatus(StatusLamaran.MENUNGGU);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testCreateLamaran() throws Exception {
        Mockito.when(lamaranService.createLamaran(any(LamaranDTO.class)))
                .thenReturn(dummyLamaran);

        mockMvc.perform(post("/api/lamaran/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyLamaran)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummyLamaran.getId().toString()))
                .andExpect(jsonPath("$.ipk").value(dummyLamaran.getIpk()))
                .andExpect(jsonPath("$.sks").value(dummyLamaran.getSks()));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetLamaranByLowonganId() throws Exception {
        List<Lamaran> lamarans = Collections.singletonList(dummyLamaran);
        Mockito.when(lamaranService.getLamaranByLowonganId(dummyId)).thenReturn(lamarans);

        mockMvc.perform(get("/api/lamaran/user/lowongan/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dummyLamaran.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void testAcceptLamaran() throws Exception {
        mockMvc.perform(post("/api/lamaran/lecturer/" + dummyId + "/accept"))
                .andExpect(status().isOk());

        Mockito.verify(lamaranService).acceptLamaran(eq(dummyId));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void testRejectLamaran() throws Exception {
        mockMvc.perform(post("/api/lamaran/lecturer/" + dummyId + "/reject"))
                .andExpect(status().isOk());

        Mockito.verify(lamaranService).rejectLamaran(eq(dummyId));
    }

    @TestConfiguration
    static class MockServiceConfig {
        @Bean
        public LamaranService lamaranService() {
            return mock(LamaranService.class);
        }
    }
}
