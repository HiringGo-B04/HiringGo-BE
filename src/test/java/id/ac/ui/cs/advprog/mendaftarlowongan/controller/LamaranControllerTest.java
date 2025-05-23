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

import java.util.Arrays;
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
    private UUID dummyLowonganId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(lamaranController).build();
        objectMapper = new ObjectMapper();

        dummyId = UUID.randomUUID();
        dummyUserId = UUID.fromString("1b59ff07-31b2-4e5d-af41-e2bb0535e1c6");
        dummyLowonganId = UUID.randomUUID();

        dummyLamaran = new Lamaran();
        dummyLamaran.setId(dummyId);
        dummyLamaran.setIpk(3.5f);
        dummyLamaran.setSks(20);
        dummyLamaran.setIdMahasiswa(dummyUserId);
        dummyLamaran.setIdLowongan(dummyLowonganId);
        dummyLamaran.setStatus(StatusLamaran.MENUNGGU);

        dummyLamaranDTO = new LamaranDTO(
                20,
                3.5f,
                dummyUserId,
                dummyLowonganId
        );
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummyLamaran.getId().toString()))
                .andExpect(jsonPath("$.ipk").value(dummyLamaran.getIpk()))
                .andExpect(jsonPath("$.sks").value(dummyLamaran.getSks()))
                .andExpect(jsonPath("$.idMahasiswa").value(dummyLamaran.getIdMahasiswa().toString()))
                .andExpect(jsonPath("$.idLowongan").value(dummyLamaran.getIdLowongan().toString()));

        verify(lamaranService).createLamaran(any(LamaranDTO.class), eq(dummyUserId));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testCreateLamaranWithException() throws Exception {
        when(jwtUtil.getUserIdFromToken("testToken")).thenReturn(dummyUserId.toString());
        when(lamaranService.createLamaran(any(LamaranDTO.class), any(UUID.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test exception")));

        mockMvc.perform(post("/api/lamaran/student/add")
                        .header("Authorization", "Bearer testToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyLamaranDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetLamaranById() throws Exception {
        when(lamaranService.getLamaranById(dummyId))
                .thenReturn(CompletableFuture.completedFuture(dummyLamaran));

        mockMvc.perform(get("/api/lamaran/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dummyLamaran.getId().toString()))
                .andExpect(jsonPath("$.ipk").value(dummyLamaran.getIpk()))
                .andExpect(jsonPath("$.sks").value(dummyLamaran.getSks()));

        verify(lamaranService).getLamaranById(eq(dummyId));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetLamaranByIdNotFound() throws Exception {
        when(lamaranService.getLamaranById(dummyId))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(get("/api/lamaran/" + dummyId))
                .andExpect(status().isNotFound());

        verify(lamaranService).getLamaranById(eq(dummyId));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetLamaranByLowonganId() throws Exception {
        List<Lamaran> lamaranList = Arrays.asList(dummyLamaran);
        when(lamaranService.getLamaranByLowonganId(dummyLowonganId))
                .thenReturn(CompletableFuture.completedFuture(lamaranList));

        mockMvc.perform(get("/api/lamaran/user/get-by-lowongan/" + dummyLowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(dummyLamaran.getId().toString()));

        verify(lamaranService).getLamaranByLowonganId(eq(dummyLowonganId));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllLamaran() throws Exception {
        List<Lamaran> lamaranList = Arrays.asList(dummyLamaran);
        when(lamaranService.getLamaran())
                .thenReturn(CompletableFuture.completedFuture(lamaranList));

        mockMvc.perform(get("/api/lamaran/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(dummyLamaran.getId().toString()));

        verify(lamaranService).getLamaran();
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void testAcceptLamaran() throws Exception {
        when(lamaranService.acceptLamaran(dummyId))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/api/lamaran/lecturer/accept/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(content().string("Lamaran accepted successfully"));

        verify(lamaranService).acceptLamaran(eq(dummyId));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void testAcceptLamaranWithException() throws Exception {
        when(lamaranService.acceptLamaran(dummyId))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test exception")));

        mockMvc.perform(post("/api/lamaran/lecturer/accept/" + dummyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to accept lamaran"));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void testRejectLamaran() throws Exception {
        when(lamaranService.rejectLamaran(dummyId))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(post("/api/lamaran/lecturer/reject/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(content().string("Lamaran rejected successfully"));

        verify(lamaranService).rejectLamaran(eq(dummyId));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void testRejectLamaranWithException() throws Exception {
        when(lamaranService.rejectLamaran(dummyId))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test exception")));

        mockMvc.perform(post("/api/lamaran/lecturer/reject/" + dummyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to reject lamaran"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateLamaran() throws Exception {
        Lamaran updatedLamaran = new Lamaran();
        updatedLamaran.setId(dummyId);
        updatedLamaran.setIpk(3.8f);
        updatedLamaran.setSks(22);
        updatedLamaran.setIdMahasiswa(dummyUserId);
        updatedLamaran.setIdLowongan(dummyLowonganId);
        updatedLamaran.setStatus(StatusLamaran.MENUNGGU);

        when(lamaranService.updateLamaran(eq(dummyId), any(Lamaran.class)))
                .thenReturn(CompletableFuture.completedFuture(updatedLamaran));

        mockMvc.perform(put("/api/lamaran/" + dummyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLamaran)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedLamaran.getId().toString()))
                .andExpect(jsonPath("$.ipk").value(updatedLamaran.getIpk()))
                .andExpect(jsonPath("$.sks").value(updatedLamaran.getSks()));

        verify(lamaranService).updateLamaran(eq(dummyId), any(Lamaran.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateLamaranNotFound() throws Exception {
        when(lamaranService.updateLamaran(eq(dummyId), any(Lamaran.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(put("/api/lamaran/" + dummyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummyLamaran)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteLamaran() throws Exception {
        when(lamaranService.deleteLamaran(dummyId))
                .thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(delete("/api/lamaran/" + dummyId))
                .andExpect(status().isOk())
                .andExpect(content().string("Lamaran deleted successfully"));

        verify(lamaranService).deleteLamaran(eq(dummyId));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteLamaranWithException() throws Exception {
        when(lamaranService.deleteLamaran(dummyId))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test exception")));

        mockMvc.perform(delete("/api/lamaran/" + dummyId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to delete lamaran"));
    }
}