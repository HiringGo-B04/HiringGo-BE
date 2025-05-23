package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MataKuliahControllerTest {

    @Mock
    private MataKuliahService service;

    @InjectMocks
    private MataKuliahController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private MataKuliahDto dto1;
    private MataKuliahDto dto2;
    private List<MataKuliahDto> dtoList;
    private CompletableFuture<List<MataKuliahDto>> dtoListFuture;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        dto1 = new MataKuliahDto("CS101", "Algoritma", 3, "Mata kuliah algoritma", List.of());
        dto2 = new MataKuliahDto("CS102", "Database", 3, "Mata kuliah database", List.of());
        dtoList = Arrays.asList(dto1, dto2);
        dtoListFuture = CompletableFuture.completedFuture(dtoList);
    }

    // ============== PUBLIC ENDPOINTS TEST ==============

    @Test
    void testListPublic_Success() throws Exception {
        // Given
        when(service.findAll()).thenReturn(dtoListFuture);

        // When & Then
        mockMvc.perform(get("/api/course/public/matakuliah"))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0].kode").value("CS101"))
                        .andExpect(jsonPath("$[0].nama").value("Algoritma"))
                        .andExpect(jsonPath("$[1].kode").value("CS102"))
                        .andExpect(jsonPath("$[1].nama").value("Database")));

        verify(service, times(1)).findAll();
    }

    @Test
    void testListPublic_EmptyList() throws Exception {
        // Given
        CompletableFuture<List<MataKuliahDto>> emptyFuture = CompletableFuture.completedFuture(List.of());
        when(service.findAll()).thenReturn(emptyFuture);

        // When & Then
        mockMvc.perform(get("/api/course/public/matakuliah"))
                .andExpect(request().asyncStarted())
                .andDo(result -> mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$.length()").value(0)));

        verify(service, times(1)).findAll();
    }

    @Test
    void testGetPublic_Success() throws Exception {
        // Given
        when(service.findByKode(eq("CS101"))).thenReturn(dto1);

        // When & Then
        mockMvc.perform(get("/api/course/public/matakuliah/CS101"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.kode").value("CS101"))
                .andExpect(jsonPath("$.nama").value("Algoritma"))
                .andExpect(jsonPath("$.sks").value(3))
                .andExpect(jsonPath("$.deskripsi").value("Mata kuliah algoritma"));

        verify(service, times(1)).findByKode(eq("CS101"));
    }

    @Test
    void testGetPublic_NotFound() throws Exception {
        // Given
        when(service.findByKode(eq("CS999"))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/course/public/matakuliah/CS999"))
                .andExpect(status().isNotFound());

        verify(service, times(1)).findByKode(eq("CS999"));
    }

    // ============== ADMIN ENDPOINTS TEST ==============

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_Success() throws Exception {
        // Given
        when(service.create(any(MataKuliahDto.class))).thenReturn(dto1);

        // When & Then
        mockMvc.perform(post("/api/course/admin/matakuliah")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.kode").value("CS101"))
                .andExpect(jsonPath("$.nama").value("Algoritma"));

        verify(service, times(1)).create(any(MataKuliahDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreate_ValidationError() throws Exception {
        // Given - Invalid DTO (empty kode)
        MataKuliahDto invalidDto = new MataKuliahDto("", "Algoritma", 3, "Desc", List.of());

        // When & Then
        mockMvc.perform(post("/api/course/admin/matakuliah")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(service, never()).create(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReplace_Success() throws Exception {
        // Given
        MataKuliahDto updatedDto = new MataKuliahDto("CS101", "Algoritma Updated", 4, "Updated desc", List.of());
        when(service.update(eq("CS101"), any(MataKuliahDto.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(put("/api/course/admin/matakuliah/CS101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.kode").value("CS101"))
                .andExpect(jsonPath("$.nama").value("Algoritma Updated"))
                .andExpect(jsonPath("$.sks").value(4));

        verify(service, times(1)).update(eq("CS101"), any(MataKuliahDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPatch_Success() throws Exception {
        // Given
        MataKuliahPatch patch = new MataKuliahPatch(4, "Updated description", null);
        MataKuliahDto updatedDto = new MataKuliahDto("CS101", "Algoritma", 4, "Updated description", List.of());
        when(service.partialUpdate(eq("CS101"), any(MataKuliahPatch.class))).thenReturn(updatedDto);

        // When & Then
        mockMvc.perform(patch("/api/course/admin/matakuliah/CS101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sks").value(4))
                .andExpect(jsonPath("$.deskripsi").value("Updated description"));

        verify(service, times(1)).partialUpdate(eq("CS101"), any(MataKuliahPatch.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDelete_Success() throws Exception {
        // Given
        doNothing().when(service).delete(eq("CS101"));

        // When & Then
        mockMvc.perform(delete("/api/course/admin/matakuliah/CS101"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(eq("CS101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddLecturer_Success() throws Exception {
        // Given
        UUID dosenId = UUID.randomUUID();
        doNothing().when(service).addLecturer(eq("CS101"), eq(dosenId));

        // When & Then
        mockMvc.perform(post("/api/course/admin/matakuliah/CS101/dosen/" + dosenId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).addLecturer(eq("CS101"), eq(dosenId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRemoveLecturer_Success() throws Exception {
        // Given
        UUID dosenId = UUID.randomUUID();
        doNothing().when(service).removeLecturer(eq("CS101"), eq(dosenId));

        // When & Then
        mockMvc.perform(delete("/api/course/admin/matakuliah/CS101/dosen/" + dosenId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).removeLecturer(eq("CS101"), eq(dosenId));
    }

    // ============== INTEGRATION TESTS ==============

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFullCRUDWorkflow() throws Exception {
        // Given
        MataKuliahDto createDto = new MataKuliahDto("CS999", "New Course", 3, "New course description", List.of());
        MataKuliahDto createdDto = new MataKuliahDto("CS999", "New Course", 3, "New course description", List.of());
        MataKuliahDto updatedDto = new MataKuliahDto("CS999", "Updated Course", 4, "Updated description", List.of());

        when(service.create(any(MataKuliahDto.class))).thenReturn(createdDto);
        when(service.findByKode(eq("CS999"))).thenReturn(createdDto);
        when(service.update(eq("CS999"), any(MataKuliahDto.class))).thenReturn(updatedDto);
        doNothing().when(service).delete(eq("CS999"));

        // When & Then - Create
        mockMvc.perform(post("/api/course/admin/matakuliah")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());

        // When & Then - Read
        mockMvc.perform(get("/api/course/public/matakuliah/CS999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kode").value("CS999"));

        // When & Then - Update
        mockMvc.perform(put("/api/course/admin/matakuliah/CS999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk());

        // When & Then - Delete
        mockMvc.perform(delete("/api/course/admin/matakuliah/CS999"))
                .andExpect(status().isNoContent());

        // Verify all interactions
        verify(service, times(1)).create(any(MataKuliahDto.class));
        verify(service, times(1)).findByKode(eq("CS999"));
        verify(service, times(1)).update(eq("CS999"), any(MataKuliahDto.class));
        verify(service, times(1)).delete(eq("CS999"));
    }
}