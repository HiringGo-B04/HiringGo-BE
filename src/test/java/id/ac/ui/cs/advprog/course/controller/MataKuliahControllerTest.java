package id.ac.ui.cs.advprog.course.controller;

import id.ac.ui.cs.advprog.authjwt.config.JwtAuthFilter;
import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.service.MataKuliahService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MataKuliahController.class)
@Import({SecurityConfig.class, MataKuliahControllerTest.MockBeans.class})
@TestPropertySource(properties = {
        "jwt.secret=fakeTestSecretKeyThatIsLongEnoughForHmacSha",
        "jwt.expiration=3600000"
})
class MataKuliahControllerTest {

    @Autowired private MockMvc             mockMvc;
    @Autowired private MataKuliahService   service;

    /* ------------------------------------------------------------------ */
    /*  Mock / stub beans for the test context                            */
    /* ------------------------------------------------------------------ */
    @TestConfiguration
    static class MockBeans {

        @Bean MataKuliahService mataKuliahService() {
            return Mockito.mock(MataKuliahService.class);
        }

        /** Dummy JwtUtil dengan field di-inject manual (tanpa constructor arg). */
        @Bean JwtUtil jwtUtil() {
            JwtUtil util = new JwtUtil();
            ReflectionTestUtils.setField(util, "jwtSecret",
                    "fakeTestSecretKeyThatIsLongEnoughForHmacSha");
            ReflectionTestUtils.setField(util, "jwtExpirationMs", 3_600_000);
            util.init();           // panggil @PostConstruct secara manual
            return util;
        }

        /** Filter asli, tapi kini memakai JwtUtil dummy di atas. */
        @Bean JwtAuthFilter jwtAuthFilter(@NonNull JwtUtil util) {
            return new JwtAuthFilter(util);
        }
    }

    /* ---------- GET ALL (PUBLIC) ---------- */
    @Test void listPublic_shouldReturnPage() throws Exception {
        var dto1 = new MataKuliahDto("MK001","Algoritma",3,"D",List.of());
        var dto2 = new MataKuliahDto("MK002","Basis Data",4,"D2",List.of());

        when(service.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto1,dto2)));

        mockMvc.perform(get("/api/course/public/matakuliah"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].kode").value("MK001"))
                .andExpect(jsonPath("$.content[1].kode").value("MK002"));
    }

    /* ---------- GET BY KODE (PUBLIC) ---------- */
    @Test void getByKode_found() throws Exception {
        var dto = new MataKuliahDto("MK001","Algoritma",3,null,List.of());
        when(service.findByKode("MK001")).thenReturn(dto);

        mockMvc.perform(get("/api/course/public/matakuliah/MK001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nama").value("Algoritma"));
    }

    @Test void getByKode_notFound() throws Exception {
        when(service.findByKode("MK404")).thenReturn(null);

        mockMvc.perform(get("/api/course/public/matakuliah/MK404"))
                .andExpect(status().isNotFound());
    }

    /* ---------- POST (ADMIN) ---------- */
    @Test @WithMockUser(roles = "ADMIN")
    void create_success() throws Exception {
        var dto = new MataKuliahDto("MK001","Algoritma",3,"Desc",List.of());
        when(service.create(any(MataKuliahDto.class))).thenReturn(dto);

        String body = """
            {"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Desc","dosenPengampu":[]}
            """;

        mockMvc.perform(post("/api/course/admin/matakuliah").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "http://localhost/api/course/admin/matakuliah/MK001"))
                .andExpect(jsonPath("$.kode").value("MK001"));
    }

    @Test @WithMockUser(roles = "ADMIN")
    void create_duplicate_shouldReturn400() throws Exception {
        doThrow(new RuntimeException("Kode sudah ada"))
                .when(service).create(any(MataKuliahDto.class));

        String body = """
            {"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Desc","dosenPengampu":[]}
            """;

        mockMvc.perform(post("/api/course/admin/matakuliah").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    /* ---------- PUT ---------- */
    @Test @WithMockUser(roles = "ADMIN")
    void update_success() throws Exception {
        var updated = new MataKuliahDto("MK001","Algo Upd",4,"D",List.of());
        when(service.update(eq("MK001"), any(MataKuliahDto.class))).thenReturn(updated);

        String body = """
            {"kode":"IGNORED","nama":"Algo Upd","sks":4,"deskripsi":"D","dosenPengampu":[]}
            """;

        mockMvc.perform(put("/api/course/admin/matakuliah/MK001").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sks").value(4));
    }

    /* ---------- PATCH ---------- */
    @Test @WithMockUser(roles = "ADMIN")
    void patch_shouldUpdateSksOnly() throws Exception {
        var patched = new MataKuliahDto("MK001","Algoritma",5,"Old",List.of());
        when(service.partialUpdate(eq("MK001"), any(MataKuliahPatch.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/course/admin/matakuliah/MK001").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sks\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sks").value(5));
    }

    /* ---------- DELETE ---------- */
    @Test @WithMockUser(roles = "ADMIN")
    void delete_success() throws Exception {
        doNothing().when(service).delete("MK001");

        mockMvc.perform(delete("/api/course/admin/matakuliah/MK001").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test @WithMockUser(roles = "ADMIN")
    void delete_notFound_shouldReturn400() throws Exception {
        doThrow(new RuntimeException("Not found")).when(service).delete("BAD");

        mockMvc.perform(delete("/api/course/admin/matakuliah/BAD").with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
