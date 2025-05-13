package id.ac.ui.cs.advprog.mendaftarlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false) // 🔑 Matikan filter keamanan saat testing
@Import({LamaranControllerTest.MockServiceConfig.class, SecurityConfig.class})
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
    void testCreateLamaran() throws Exception {
        Mockito.when(lamaranService.createLamaran(any(Lamaran.class)))
                .thenReturn(dummyLamaran);

        mockMvc.perform(post("/lamaran")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyLamaran)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummyLamaran.getId().toString()))
                .andExpect(jsonPath("$.ipk").value(dummyLamaran.getIpk()))
                .andExpect(jsonPath("$.sks").value(dummyLamaran.getSks()));
    }

    @Test
    void testGetLamaranByLowonganId() throws Exception {
        List<Lamaran> lamarans = Collections.singletonList(dummyLamaran);
        Mockito.when(lamaranService.getLamaranByLowonganId(dummyId)).thenReturn(lamarans);

        mockMvc.perform(get("/lamaran/lowongan/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dummyLamaran.getId().toString()));
    }

    @Test
    void testAcceptLamaran() throws Exception {
        mockMvc.perform(post("/lamaran/" + dummyId + "/accept"))
                .andExpect(status().isOk());

        Mockito.verify(lamaranService).acceptLamaran(eq(dummyId));
    }

    @Test
    void testRejectLamaran() throws Exception {
        mockMvc.perform(post("/lamaran/" + dummyId + "/reject"))
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
