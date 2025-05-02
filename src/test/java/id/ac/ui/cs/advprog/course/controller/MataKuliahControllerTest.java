package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MataKuliahController.class)
@Import(MataKuliahControllerTest.MataKuliahServiceMockConfiguration.class)
class MataKuliahControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MataKuliahService mataKuliahService;

    /** menyediakan bean mock untuk service */
    @TestConfiguration
    static class MataKuliahServiceMockConfiguration {
        @Bean
        MataKuliahService mataKuliahService() {
            return Mockito.mock(MataKuliahService.class);
        }
    }

    /* ---------- GET ALL ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllMataKuliah_shouldReturnList() throws Exception {
        var mk1 = new MataKuliah("MK001", "Algoritma", "Desc1", 3);
        var mk2 = new MataKuliah("MK002", "Basis Data", "Desc2", 4);
        when(mataKuliahService.findAll()).thenReturn(Arrays.asList(mk1, mk2));

        mockMvc.perform(get("/api/v1/matakuliah"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].kode").value("MK001"))
                .andExpect(jsonPath("$[1].kode").value("MK002"));
    }

    /* ---------- GET BY KODE (FOUND) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getMataKuliahByKode_found() throws Exception {
        var mk = new MataKuliah("MK001", "Algoritma", "Desc", 3);
        when(mataKuliahService.findByKode("MK001")).thenReturn(mk);

        mockMvc.perform(get("/api/v1/matakuliah/MK001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kode").value("MK001"));
    }

    /* ---------- GET BY KODE (NOT FOUND) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getMataKuliahByKode_notFound() throws Exception {
        when(mataKuliahService.findByKode("MK404")).thenReturn(null);

        mockMvc.perform(get("/api/v1/matakuliah/MK404"))
                .andExpect(status().isNotFound());
    }

    /* ---------- POST (SUCCESS) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void createMataKuliah_success() throws Exception {
        String mkJson = """
                {"kode":"MK001","nama":"Algoritma","deskripsi":"Desc","sks":3}
                """;
        doNothing().when(mataKuliahService).create(any(MataKuliah.class));

        mockMvc.perform(post("/api/v1/matakuliah").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mkJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/api/v1/matakuliah/MK001"))
                .andExpect(jsonPath("$.kode").value("MK001"));
    }

    /* ---------- POST (DUPLICATE → BAD REQUEST) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void createMataKuliah_duplicate() throws Exception {
        String mkJson = """
                {"kode":"MK001","nama":"Algoritma","deskripsi":"Desc","sks":3}
                """;
        doThrow(new RuntimeException("Kode sudah ada"))
                .when(mataKuliahService).create(any(MataKuliah.class));

        mockMvc.perform(post("/api/v1/matakuliah").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mkJson))
                .andExpect(status().isBadRequest());
    }

    /* ---------- PUT (SUCCESS) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMataKuliah_success() throws Exception {
        String mkJson = """
                {"kode":"MK001","nama":"Algoritma Updated","deskripsi":"D","sks":4}
                """;
        doNothing().when(mataKuliahService).update(any(MataKuliah.class));

        mockMvc.perform(put("/api/v1/matakuliah/MK001").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mkJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("Algoritma Updated"));
    }

    /* ---------- PATCH (PARTIAL UPDATE) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void patchMataKuliah_updateSksOnly() throws Exception {
        var existing = new MataKuliah("MK001", "Algoritma", "Desc", 3);
        when(mataKuliahService.findByKode("MK001")).thenReturn(existing);
        doNothing().when(mataKuliahService).update(any(MataKuliah.class));

        String patchJson = """
                {"sks":5}
                """;

        mockMvc.perform(patch("/api/v1/matakuliah/MK001").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sks").value(5));
    }

    /* ---------- DELETE (SUCCESS) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMataKuliah_success() throws Exception {
        doNothing().when(mataKuliahService).delete("MK001");

        mockMvc.perform(delete("/api/v1/matakuliah/MK001").with(csrf()))
                .andExpect(status().isOk());
    }

    /* ---------- DELETE (NOT FOUND) ---------- */
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMataKuliah_notFound() throws Exception {
        doThrow(new RuntimeException("Not found"))
                .when(mataKuliahService).delete("MKX");

        mockMvc.perform(delete("/api/v1/matakuliah/MKX").with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
