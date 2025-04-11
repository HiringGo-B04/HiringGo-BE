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
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MataKuliahController.class)
@Import(MataKuliahControllerTest.MataKuliahServiceMockConfiguration.class)
class MataKuliahControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MataKuliahService mataKuliahService;

    // Konfigurasi Test khusus untuk menyediakan bean mock dari MataKuliahService
    @TestConfiguration
    static class MataKuliahServiceMockConfiguration {
        @Bean
        MataKuliahService mataKuliahService() {
            return Mockito.mock(MataKuliahService.class);
        }
    }

    // GET /api/matakuliah - Mengembalikan semua mata kuliah
    @Test
    void testGetAllMataKuliah() throws Exception {
        MataKuliah mk1 = new MataKuliah("MK001", "Algoritma", "Deskripsi 1", 3);
        MataKuliah mk2 = new MataKuliah("MK002", "Basis Data", "Deskripsi 2", 4);

        when(mataKuliahService.findAll()).thenReturn(Arrays.asList(mk1, mk2));

        mockMvc.perform(get("/api/matakuliah"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].kode").value("MK001"))
                .andExpect(jsonPath("$[1].kode").value("MK002"));
    }

    // GET /api/matakuliah/{kode} - Data ditemukan
    @Test
    void testGetMataKuliahByKodeFound() throws Exception {
        MataKuliah mk = new MataKuliah("MK001", "Algoritma", "Deskripsi", 3);
        when(mataKuliahService.findByKode("MK001")).thenReturn(mk);

        mockMvc.perform(get("/api/matakuliah/MK001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.kode").value("MK001"))
                .andExpect(jsonPath("$.nama").value("Algoritma"));
    }

    // GET /api/matakuliah/{kode} - Data tidak ditemukan
    @Test
    void testGetMataKuliahByKodeNotFound() throws Exception {
        when(mataKuliahService.findByKode("MK003")).thenReturn(null);

        mockMvc.perform(get("/api/matakuliah/MK003"))
                .andExpect(status().isNotFound());
    }

    // POST /api/matakuliah - Pembuatan berhasil
    @Test
    void testCreateMataKuliahSuccess() throws Exception {
        String mkJson = "{\"kode\":\"MK001\",\"nama\":\"Algoritma\",\"deskripsi\":\"Deskripsi\",\"sks\":3}";
        doNothing().when(mataKuliahService).create(any(MataKuliah.class));

        mockMvc.perform(post("/api/matakuliah")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mkJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Mata kuliah berhasil dibuat"));
    }

    // POST /api/matakuliah - Pembuatan gagal karena duplikat
    @Test
    void testCreateMataKuliahFailure() throws Exception {
        String mkJson = "{\"kode\":\"MK001\",\"nama\":\"Algoritma\",\"deskripsi\":\"Deskripsi\",\"sks\":3}";
        doThrow(new RuntimeException("Kode mata kuliah sudah terdaftar: MK001"))
                .when(mataKuliahService).create(any(MataKuliah.class));

        mockMvc.perform(post("/api/matakuliah")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mkJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Kode mata kuliah sudah terdaftar: MK001"));
    }

    // PUT /api/matakuliah/{kode} - Update berhasil
    @Test
    void testUpdateMataKuliahSuccess() throws Exception {
        String mkJson = "{\"kode\":\"MK001\",\"nama\":\"Algoritma Updated\",\"deskripsi\":\"Deskripsi Updated\",\"sks\":3}";
        doNothing().when(mataKuliahService).update(any(MataKuliah.class));

        mockMvc.perform(put("/api/matakuliah/MK001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mkJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Mata kuliah berhasil diupdate"));
    }

    // PUT /api/matakuliah/{kode} - Update gagal (data tidak ditemukan)
    @Test
    void testUpdateMataKuliahFailure() throws Exception {
        String mkJson = "{\"kode\":\"MK001\",\"nama\":\"Algoritma Updated\",\"deskripsi\":\"Deskripsi Updated\",\"sks\":3}";
        doThrow(new RuntimeException("Mata kuliah tidak ditemukan: MK001"))
                .when(mataKuliahService).update(any(MataKuliah.class));

        mockMvc.perform(put("/api/matakuliah/MK001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mkJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mata kuliah tidak ditemukan: MK001"));
    }

    // DELETE /api/matakuliah/{kode} - Delete berhasil
    @Test
    void testDeleteMataKuliahSuccess() throws Exception {
        doNothing().when(mataKuliahService).delete("MK001");

        mockMvc.perform(delete("/api/matakuliah/MK001"))
                .andExpect(status().isOk())
                .andExpect(content().string("Mata kuliah berhasil dihapus"));
    }

    // DELETE /api/matakuliah/{kode} - Delete gagal (data tidak ditemukan)
    @Test
    void testDeleteMataKuliahFailure() throws Exception {
        doThrow(new RuntimeException("Mata kuliah tidak ditemukan: MK002"))
                .when(mataKuliahService).delete("MK002");

        mockMvc.perform(delete("/api/matakuliah/MK002"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mata kuliah tidak ditemukan: MK002"));
    }
}
