package id.ac.ui.cs.advprog.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.service.LowonganService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LowonganControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LowonganService lowonganService;

    @InjectMocks
    private LowonganController lowonganController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(lowonganController).build();
    }

    @Test
    void testAddLowongan() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        Lowongan lowongan = new Lowongan.Builder()
                .matkul("Pemrograman Berorientasi Objek")
                .year(2025)
                .term("Genap")
                .totalAsdosNeeded(5)
                .build();
        lowongan.setId(id);

        when(lowonganService.addLowongan(any(Lowongan.class))).thenReturn(lowongan);

        // Act & Assert
        mockMvc.perform(post("/lowongan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowongan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.matkul").value("Pemrograman Berorientasi Objek"))
                .andExpect(jsonPath("$.year").value(2025))
                .andExpect(jsonPath("$.term").value("Genap"))
                .andExpect(jsonPath("$.totalAsdosNeeded").value(5))
                .andExpect(jsonPath("$.totalAsdosRegistered").value(0))
                .andExpect(jsonPath("$.totalAsdosAccepted").value(0));
    }

    @Test
    void testGetLowonganById() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        Lowongan lowongan = new Lowongan.Builder()
                .matkul("Metode Numerik")
                .year(2025)
                .term("Ganjil")
                .totalAsdosNeeded(3)
                .totalAsdosRegistered(2)
                .totalAsdosAccepted(1)
                .build();
        lowongan.setId(id);

        when(lowonganService.getLowonganById(id)).thenReturn(lowongan);

        // Act & Assert
        mockMvc.perform(get("/lowongan/lowongan/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.matkul").value("Metode Numerik"))
                .andExpect(jsonPath("$.year").value(2025))
                .andExpect(jsonPath("$.term").value("Ganjil"))
                .andExpect(jsonPath("$.totalAsdosNeeded").value(3))
                .andExpect(jsonPath("$.totalAsdosRegistered").value(2))
                .andExpect(jsonPath("$.totalAsdosAccepted").value(1));
    }

    @Test
    void testGetLowonganById_NotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(lowonganService.getLowonganById(id)).thenThrow(new RuntimeException("Lowongan tidak ditemukan"));

        // Act & Assert
        mockMvc.perform(get("/lowongan/lowongan/" + id))
                .andExpect(status().isInternalServerError());
    }
}