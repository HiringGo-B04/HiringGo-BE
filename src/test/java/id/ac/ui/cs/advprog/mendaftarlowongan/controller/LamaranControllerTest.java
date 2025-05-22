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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
    private CompletableFuture<Lamaran> dummyLamaranFuture;

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

        dummyLamaranFuture = CompletableFuture.completedFuture(dummyLamaran);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testCreateLamaran() throws Exception {
        when(jwtUtil.getUserIdFromToken("testToken")).thenReturn(dummyUserId.toString());
        when(lamaranService.createLamaran(any(LamaranDTO.class), any(UUID.class)))
                .thenReturn(CompletableFuture.completedFuture(dummyLamaran));

        mockMvc.perform(post("/api/lamaran/student/add")
                        .header("Authorization", "Bearer testToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyLamaranDTO)))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(dummyLamaran.getId().toString()))
                        .andExpect(jsonPath("$.ipk").value(dummyLamaran.getIpk()))
                        .andExpect(jsonPath("$.sks").value(dummyLamaran.getSks())));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetLamaranByLowonganId() throws Exception {
        when(lamaranService.getLamaranById(dummyId))
                .thenReturn(CompletableFuture.completedFuture(dummyLamaran));

        mockMvc.perform(get("/api/lamaran/" + dummyId))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(dummyLamaran.getId().toString())));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void testAcceptLamaran() throws Exception {
        // Mock service agar tidak return null
        when(lamaranService.acceptLamaran(dummyId)).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/api/lamaran/lecturer/accept/" + dummyId))
                .andExpect(status().isOk());

        verify(lamaranService).acceptLamaran(eq(dummyId));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testRejectLamaran() throws Exception {
        // Mock service agar tidak return null
        when(lamaranService.rejectLamaran(dummyId)).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/api/lamaran/lecturer/reject/" + dummyId))
                .andExpect(status().isOk());

        verify(lamaranService).rejectLamaran(eq(dummyId));
    }

}