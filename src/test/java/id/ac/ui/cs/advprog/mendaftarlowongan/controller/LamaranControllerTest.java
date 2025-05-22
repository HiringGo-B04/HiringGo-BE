package id.ac.ui.cs.advprog.mendaftarlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.mendaftarlowongan.dto.LamaranDTO;
import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import id.ac.ui.cs.advprog.mendaftarlowongan.service.LamaranService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LamaranControllerTest {

    @Mock
    private LamaranService lamaranService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LamaranController lamaranController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Lamaran dummyLamaran;
    private LamaranDTO dummyLamaranDTO;
    private UUID dummyId;
    private UUID dummyUserId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lamaranController).build();
        objectMapper = new ObjectMapper();

        dummyId = UUID.randomUUID();
        dummyUserId = UUID.fromString("1b59ff07-31b2-4e5d-af41-e2bb0535e1c6");

        dummyLamaran = new Lamaran();
        dummyLamaran.setId(dummyId);
        dummyLamaran.setIpk(3.5f);
        dummyLamaran.setSks(20);
        dummyLamaran.setIdMahasiswa(dummyUserId);
        dummyLamaran.setIdLowongan(UUID.randomUUID());
        dummyLamaran.setStatus(StatusLamaran.MENUNGGU);

        dummyLamaranDTO = new LamaranDTO(
                20,
                3.5f,
                dummyUserId,
                UUID.randomUUID()
        );
    }

    @Test
    void testCreateLamaran() throws Exception {
        when(jwtUtil.getUserIdFromToken("testToken")).thenReturn(dummyUserId.toString());
        when(lamaranService.createLamaran(any(LamaranDTO.class), any(UUID.class)))
                .thenReturn(dummyLamaran);

        mockMvc.perform(post("/api/lamaran/student/add")
                        .header("Authorization", "Bearer testToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyLamaranDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummyLamaran.getId().toString()))
                .andExpect(jsonPath("$.ipk").value(dummyLamaran.getIpk()))
                .andExpect(jsonPath("$.sks").value(dummyLamaran.getSks()));
    }

    @Test
    void testGetLamaranByLowonganId() throws Exception {
        List<Lamaran> lamarans = Collections.singletonList(dummyLamaran);
        when(lamaranService.getLamaranByLowonganId(dummyId)).thenReturn(lamarans);

        mockMvc.perform(get("/api/lamaran/user/get-by-lowongan/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dummyLamaran.getId().toString()));
    }

    @Test
    void testAcceptLamaran() throws Exception {
        mockMvc.perform(post("/api/lamaran/lecturer/accept/" + dummyId))
                .andExpect(status().isOk());

        org.mockito.Mockito.verify(lamaranService).acceptLamaran(eq(dummyId));
    }

    @Test
    void testRejectLamaran() throws Exception {
        mockMvc.perform(post("/api/lamaran/lecturer/reject/" + dummyId))
                .andExpect(status().isOk());

        org.mockito.Mockito.verify(lamaranService).rejectLamaran(eq(dummyId));
    }
}