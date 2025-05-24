package id.ac.ui.cs.advprog.manajemenlowongan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.service.LowonganServiceImpl;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LowonganControllerTest {

    @Mock
    private LowonganServiceImpl lowonganService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LowonganController lowonganController;

    private MockMvc mockMvc;
    private UUID sampleUUID;
    private ObjectMapper objectMapper;
    private Lowongan sampleLowongan;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(lowonganController).build();
        sampleUUID = UUID.randomUUID();
        sampleLowongan = new Lowongan.Builder()
                .matkul("Pemrograman Lanjut")
                .year(2025)
                .term("Genap")
                .idDosen(sampleUUID)
                .totalAsdosNeeded(3)
                .build();
    }

    @Test
    @WithMockUser(username = "LECTURER")
    public void testGetLecturerDataById_Success() throws Exception {
        UUID lecturerId = UUID.randomUUID();
        String token = "header.payload.signature"; // Must have exactly 2 dots
        String authHeader = "Bearer " + token;

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Success");
        responseMap.put("lowongan", 3);
        responseMap.put("assistant", 5);
        responseMap.put("vacan", 2);

        when(jwtUtil.getUserIdFromToken(token)).thenReturn(lecturerId.toString());
        when(lowonganService.getLowonganByDosen(lecturerId))
                .thenReturn(ResponseEntity.ok(responseMap));

        mockMvc.perform(get("/api/lowongan/lecturer/get")
                        .header("Authorization", authHeader)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.lowongan").value(3))
                .andExpect(jsonPath("$.assistant").value(5))
                .andExpect(jsonPath("$.vacan").value(2));
    }

    @Test
    void testAddLowongan() throws Exception {
        when(lowonganService.addLowongan(any(Lowongan.class))).thenReturn(sampleLowongan);

        mockMvc.perform(post("/api/lowongan/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleLowongan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matkul").value("Pemrograman Lanjut"));
    }

    @Test
    void testGetLowongan() throws Exception {
        when(lowonganService.getLowongan()).thenReturn(List.of(sampleLowongan));

        mockMvc.perform(get("/api/lowongan/user/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matkul").value("Pemrograman Lanjut"));
    }

    @Test
    void testGetLowonganById() throws Exception {
        when(lowonganService.getLowonganById(sampleUUID)).thenReturn(sampleLowongan);

        mockMvc.perform(get("/api/lowongan/user/get/" + sampleUUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matkul").value("Pemrograman Lanjut"));
    }

    @Test
    void testGetLowonganById_NotFound() throws Exception {
        when(lowonganService.getLowonganById(any(UUID.class))).thenThrow(new LowonganController.LowonganNotFoundException("Lowongan dengan ID tidak ditemukan"));

        mockMvc.perform(get("/api/lowongan/user/get/" + UUID.randomUUID()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "LECTURER")
    void testGetLowonganByDosen_ReturnsExpectedValues() throws Exception {
        UUID lecturerId = UUID.randomUUID();
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Success");
        expectedResponse.put("course", 4);
        expectedResponse.put("assistant", 6);
        expectedResponse.put("vacan", 2);

        when(jwtUtil.getUserIdFromToken(token)).thenReturn(lecturerId.toString());
        when(lowonganService.getLowonganByDosen(eq(lecturerId)))
                .thenReturn(ResponseEntity.ok(expectedResponse));

        mockMvc.perform(get("/api/lowongan/lecturer/get")
                        .header("Authorization", authHeader)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.course").value(4))
                .andExpect(jsonPath("$.assistant").value(6))
                .andExpect(jsonPath("$.vacan").value(2));
    }

    @Test
    void testGetLecturerDataById_InvalidToken() throws Exception {
        String token = "invalid.jwt.token";
        String authHeader = "Bearer " + token;

        when(jwtUtil.getUserIdFromToken(token)).thenThrow(new IllegalArgumentException("Invalid token"));

        mockMvc.perform(get("/api/lowongan/lecturer/get")
                        .header("Authorization", authHeader))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid token"));
    }
}