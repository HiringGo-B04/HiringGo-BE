package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web‑layer tests untuk {@link MataKuliahController} versi DTO.
 */
@WebMvcTest(MataKuliahController.class)
@Import(MataKuliahControllerTest.MockConfig.class)
class MataKuliahControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MataKuliahService service;

    @TestConfiguration
    static class MockConfig {
        @Bean
        MataKuliahService mataKuliahService() {
            return Mockito.mock(MataKuliahService.class);
        }
    }

    /* ---------- GET ALL (PAGE) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_shouldReturnPage() throws Exception {
        var dto1 = new MataKuliahDto("MK001", "Algoritma", 3, "D", List.of());
        var dto2 = new MataKuliahDto("MK002", "Basis Data", 4, "D2", List.of());

        when(service.findAll(PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(dto1, dto2)));

        mockMvc.perform(get("/api/v1/matakuliah"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].kode").value("MK001"))
                .andExpect(jsonPath("$.content[1].kode").value("MK002"));
    }

    /* ---------- GET BY KODE ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getByKode_found() throws Exception {
        var dto = new MataKuliahDto("MK001", "Algoritma", 3, null, List.of());
        when(service.findByKode("MK001")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/matakuliah/MK001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("Algoritma"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByKode_notFound() throws Exception {
        when(service.findByKode("MK404")).thenReturn(null);

        mockMvc.perform(get("/api/v1/matakuliah/MK404"))
                .andExpect(status().isNotFound());
    }

    /* ---------- POST ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void create_success() throws Exception {
        var dto = new MataKuliahDto("MK001", "Algoritma", 3, "Desc", List.of());
        when(service.create(ArgumentMatchers.any(MataKuliahDto.class))).thenReturn(dto);

        String body = """
                {"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Desc","dosenPengampu":[]}
                """;

        mockMvc.perform(post("/api/v1/matakuliah").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/api/v1/matakuliah/MK001"))
                .andExpect(jsonPath("$.kode").value("MK001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_duplicate_shouldReturn400() throws Exception {
        doThrow(new RuntimeException("Kode sudah ada"))
                .when(service).create(any(MataKuliahDto.class));

        String body = """
                {"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Desc","dosenPengampu":[]}
                """;

        mockMvc.perform(post("/api/v1/matakuliah").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    /* ---------- PUT ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void update_success() throws Exception {
        var updated = new MataKuliahDto("MK001", "Algo Upd", 4, "D", List.of());
        when(service.update(eq("MK001"), any(MataKuliahDto.class))).thenReturn(updated);

        String body = """
                {"kode":"IGNORED","nama":"Algo Upd","sks":4,"deskripsi":"D","dosenPengampu":[]}
                """;

        mockMvc.perform(put("/api/v1/matakuliah/MK001").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sks").value(4));
    }

    /* ---------- PATCH ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void patch_shouldUpdateSksOnly() throws Exception {
        var patched = new MataKuliahDto("MK001", "Algoritma", 5, "Old", List.of());
        when(service.partialUpdate(eq("MK001"), any(MataKuliahPatch.class))).thenReturn(patched);

        String body = "{\"sks\":5}";

        mockMvc.perform(patch("/api/v1/matakuliah/MK001").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sks").value(5));
    }

    /* ---------- DELETE ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_success() throws Exception {
        doNothing().when(service).delete("MK001");

        mockMvc.perform(delete("/api/v1/matakuliah/MK001").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_notFound_shouldReturn400() throws Exception {
        doThrow(new RuntimeException("Not found")).when(service).delete("BAD");

        mockMvc.perform(delete("/api/v1/matakuliah/BAD").with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
