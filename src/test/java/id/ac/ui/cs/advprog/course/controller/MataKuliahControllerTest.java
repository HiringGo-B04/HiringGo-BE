//package id.ac.ui.cs.advprog.course.controller;
//
//import id.ac.ui.cs.advprog.authjwt.config.JwtAuthFilter;
//import id.ac.ui.cs.advprog.authjwt.config.JwtUtil;
//import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
//import id.ac.ui.cs.advprog.authjwt.repository.TokenRepository;
//import id.ac.ui.cs.advprog.course.config.AsyncConfig;
//import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
//import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
//import id.ac.ui.cs.advprog.course.service.MataKuliahService;
//import id.ac.ui.cs.advprog.course.service.AsyncMataKuliahService;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.lang.NonNull;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(MataKuliahController.class)
//@Import({AsyncConfig.class, MataKuliahControllerTest.MockBeans.class})
//@TestPropertySource(properties = {
//        "jwt.secret=fakeTestSecretKeyThatIsLongEnoughForHmacSha",
//        "jwt.expiration=3600000"
//})
//class MataKuliahControllerTest {
//
//    @Autowired private MockMvc mockMvc;
//    @Autowired private MataKuliahService service;
//    @Autowired private AsyncMataKuliahService asyncService;
//
//    /* ------------------------------------------------------------------ */
//    /*  Mock / stub beans for the test context                            */
//    /* ------------------------------------------------------------------ */
//    @TestConfiguration
//    static class MockBeans {
//
//        @Bean MataKuliahService mataKuliahService() {
//            return Mockito.mock(MataKuliahService.class);
//        }
//
//        @Bean AsyncMataKuliahService asyncMataKuliahService() {
//            return Mockito.mock(AsyncMataKuliahService.class);
//        }
//
//        @Bean TokenRepository tokenRepository() {
//            return Mockito.mock(TokenRepository.class);
//        }
//
//        /** Dummy JwtUtil dengan field di-inject manual (tanpa constructor arg). */
//        @Bean JwtUtil jwtUtil() {
//            JwtUtil util = new JwtUtil();
//            ReflectionTestUtils.setField(util, "jwtSecret",
//                    "fakeTestSecretKeyThatIsLongEnoughForHmacSha");
//            ReflectionTestUtils.setField(util, "jwtExpirationMs", 3_600_000);
//            // Mock TokenRepository untuk JwtUtil
//            ReflectionTestUtils.setField(util, "tokenRepository", tokenRepository());
//            util.init();
//            return util;
//        }
//
//        /** Filter asli, tapi kini memakai JwtUtil dummy di atas. */
//        @Bean JwtAuthFilter jwtAuthFilter(@NonNull JwtUtil util) {
//            return new JwtAuthFilter(util);
//        }
//
//        /** SecurityConfig dengan JwtUtil yang sudah di-mock */
//        @Bean SecurityConfig securityConfig(@NonNull JwtUtil util) {
//            return new SecurityConfig(util);
//        }
//    }
//
//    /* ========== EXISTING SYNCHRONOUS TESTS (UNCHANGED) ========== */
//
//    /* ---------- GET ALL (PUBLIC) ---------- */
//    @Test void listPublic_shouldReturnPage() throws Exception {
//        var dto1 = new MataKuliahDto("MK001","Algoritma",3,"D",List.of());
//        var dto2 = new MataKuliahDto("MK002","Basis Data",4,"D2",List.of());
//
//        when(service.findAll(any(Pageable.class)))
//                .thenReturn(new PageImpl<>(List.of(dto1,dto2)));
//
//        mockMvc.perform(get("/api/course/public/matakuliah"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].kode").value("MK001"))
//                .andExpect(jsonPath("$.content[1].kode").value("MK002"));
//    }
//
//    /* ---------- GET BY KODE (PUBLIC) ---------- */
//    @Test void getByKode_found() throws Exception {
//        var dto = new MataKuliahDto("MK001","Algoritma",3,null,List.of());
//        when(service.findByKode("MK001")).thenReturn(dto);
//
//        mockMvc.perform(get("/api/course/public/matakuliah/MK001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nama").value("Algoritma"));
//    }
//
//    @Test void getByKode_notFound() throws Exception {
//        when(service.findByKode("MK404")).thenReturn(null);
//
//        mockMvc.perform(get("/api/course/public/matakuliah/MK404"))
//                .andExpect(status().isNotFound());
//    }
//
//    /* ---------- POST (ADMIN) ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void create_success() throws Exception {
//        var dto = new MataKuliahDto("MK001","Algoritma",3,"Desc",List.of());
//        when(service.create(any(MataKuliahDto.class))).thenReturn(dto);
//
//        String body = """
//            {"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Desc","dosenPengampu":[]}
//            """;
//
//        mockMvc.perform(post("/api/course/admin/matakuliah").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location",
//                        "http://localhost/api/course/admin/matakuliah/MK001"))
//                .andExpect(jsonPath("$.kode").value("MK001"));
//    }
//
//    @Test @WithMockUser(roles = "ADMIN")
//    void create_duplicate_shouldReturn400() throws Exception {
//        doThrow(new RuntimeException("Kode sudah ada"))
//                .when(service).create(any(MataKuliahDto.class));
//
//        String body = """
//            {"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Desc","dosenPengampu":[]}
//            """;
//
//        mockMvc.perform(post("/api/course/admin/matakuliah").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isBadRequest());
//    }
//
//    /* ---------- PUT ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void update_success() throws Exception {
//        var updated = new MataKuliahDto("MK001","Algo Upd",4,"D",List.of());
//        when(service.update(eq("MK001"), any(MataKuliahDto.class))).thenReturn(updated);
//
//        String body = """
//            {"kode":"IGNORED","nama":"Algo Upd","sks":4,"deskripsi":"D","dosenPengampu":[]}
//            """;
//
//        mockMvc.perform(put("/api/course/admin/matakuliah/MK001").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.sks").value(4));
//    }
//
//    /* ---------- PATCH ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void patch_shouldUpdateSksOnly() throws Exception {
//        var patched = new MataKuliahDto("MK001","Algoritma",5,"Old",List.of());
//        when(service.partialUpdate(eq("MK001"), any(MataKuliahPatch.class))).thenReturn(patched);
//
//        mockMvc.perform(patch("/api/course/admin/matakuliah/MK001").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"sks\":5}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.sks").value(5));
//    }
//
//    /* ---------- DELETE ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void delete_success() throws Exception {
//        doNothing().when(service).delete("MK001");
//
//        mockMvc.perform(delete("/api/course/admin/matakuliah/MK001").with(csrf()))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test @WithMockUser(roles = "ADMIN")
//    void delete_notFound_shouldReturn400() throws Exception {
//        doThrow(new RuntimeException("Not found")).when(service).delete("BAD");
//
//        mockMvc.perform(delete("/api/course/admin/matakuliah/BAD").with(csrf()))
//                .andExpect(status().isBadRequest());
//    }
//
//    /* ========== NEW ASYNCHRONOUS TESTS ========== */
//
//    /* ---------- GET ALL ASYNC (PUBLIC) ---------- */
//    @Test void listPublicAsync_shouldReturnPageAsynchronously() throws Exception {
//        var dto1 = new MataKuliahDto("MK001","Algoritma",3,"D",List.of());
//        var dto2 = new MataKuliahDto("MK002","Basis Data",4,"D2",List.of());
//        var page = new PageImpl<>(List.of(dto1, dto2));
//
//        when(asyncService.findAllAsync(any(Pageable.class)))
//                .thenReturn(CompletableFuture.completedFuture(page));
//
//        MvcResult result = mockMvc.perform(get("/api/course/public/matakuliah/async"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].kode").value("MK001"))
//                .andExpect(jsonPath("$.content[1].kode").value("MK002"));
//
//        verify(asyncService, times(1)).findAllAsync(any(Pageable.class));
//    }
//
//    /* ---------- GET BY KODE ASYNC (PUBLIC) ---------- */
//    @Test void getByKodeAsync_found() throws Exception {
//        var dto = new MataKuliahDto("MK001","Algoritma",3,"Description",List.of());
//        when(asyncService.findByKodeAsync("MK001"))
//                .thenReturn(CompletableFuture.completedFuture(dto));
//
//        MvcResult result = mockMvc.perform(get("/api/course/public/matakuliah/MK001/async"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nama").value("Algoritma"))
//                .andExpect(jsonPath("$.kode").value("MK001"));
//
//        verify(asyncService, times(1)).findByKodeAsync("MK001");
//    }
//
//    @Test void getByKodeAsync_notFound() throws Exception {
//        when(asyncService.findByKodeAsync("MK404"))
//                .thenReturn(CompletableFuture.completedFuture(null));
//
//        MvcResult result = mockMvc.perform(get("/api/course/public/matakuliah/MK404/async"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isNotFound());
//
//        verify(asyncService, times(1)).findByKodeAsync("MK404");
//    }
//
//    /* ---------- POST ASYNC (ADMIN) ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void createAsync_success() throws Exception {
//        var dto = new MataKuliahDto("MK001","Algoritma",3,"Desc",List.of());
//        when(asyncService.createAsync(any(MataKuliahDto.class)))
//                .thenReturn(CompletableFuture.completedFuture(dto));
//
//        String body = """
//            {"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Desc","dosenPengampu":[]}
//            """;
//
//        MvcResult result = mockMvc.perform(post("/api/course/admin/matakuliah/async").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isCreated())
//                .andExpect(header().string("Location",
//                        "http://localhost/api/course/admin/matakuliah/async/MK001"))
//                .andExpect(jsonPath("$.kode").value("MK001"));
//
//        verify(asyncService, times(1)).createAsync(any(MataKuliahDto.class));
//    }
//
//    @Test @WithMockUser(roles = "ADMIN")
//    void createAsync_failure_shouldReturn400() throws Exception {
//        CompletableFuture<MataKuliahDto> failedFuture = new CompletableFuture<>();
//        failedFuture.completeExceptionally(new RuntimeException("Creation failed"));
//
//        when(asyncService.createAsync(any(MataKuliahDto.class)))
//                .thenReturn(failedFuture);
//
//        String body = """
//            {"kode":"MK001","nama":"Algoritma","sks":3,"deskripsi":"Desc","dosenPengampu":[]}
//            """;
//
//        MvcResult result = mockMvc.perform(post("/api/course/admin/matakuliah/async").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isBadRequest());
//    }
//
//    /* ---------- PUT ASYNC ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void updateAsync_success() throws Exception {
//        var updated = new MataKuliahDto("MK001","Algo Updated",4,"Updated desc",List.of());
//        when(asyncService.updateAsync(eq("MK001"), any(MataKuliahDto.class)))
//                .thenReturn(CompletableFuture.completedFuture(updated));
//
//        String body = """
//            {"kode":"IGNORED","nama":"Algo Updated","sks":4,"deskripsi":"Updated desc","dosenPengampu":[]}
//            """;
//
//        MvcResult result = mockMvc.perform(put("/api/course/admin/matakuliah/MK001/async").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nama").value("Algo Updated"))
//                .andExpect(jsonPath("$.sks").value(4));
//
//        verify(asyncService, times(1)).updateAsync(eq("MK001"), any(MataKuliahDto.class));
//    }
//
//    /* ---------- PATCH ASYNC ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void patchAsync_shouldUpdateSksOnly() throws Exception {
//        var patched = new MataKuliahDto("MK001","Algoritma",5,"Original desc",List.of());
//        when(asyncService.partialUpdateAsync(eq("MK001"), any(MataKuliahPatch.class)))
//                .thenReturn(CompletableFuture.completedFuture(patched));
//
//        MvcResult result = mockMvc.perform(patch("/api/course/admin/matakuliah/MK001/async").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"sks\":5}"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.sks").value(5))
//                .andExpect(jsonPath("$.nama").value("Algoritma"));
//
//        verify(asyncService, times(1)).partialUpdateAsync(eq("MK001"), any(MataKuliahPatch.class));
//    }
//
//    /* ---------- DELETE ASYNC ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void deleteAsync_success() throws Exception {
//        // Mock deleteAsync to return completed future with null (Void)
//        CompletableFuture<Void> completedFuture = CompletableFuture.completedFuture(null);
//        when(asyncService.deleteAsync("MK001")).thenReturn(completedFuture);
//
//        MvcResult result = mockMvc.perform(delete("/api/course/admin/matakuliah/MK001/async").with(csrf()))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isNoContent());
//
//        verify(asyncService, times(1)).deleteAsync("MK001");
//    }
//
//    @Test @WithMockUser(roles = "ADMIN")
//    void deleteAsync_notFound_shouldReturn400() throws Exception {
//        CompletableFuture<Void> failedFuture = new CompletableFuture<>();
//        failedFuture.completeExceptionally(new RuntimeException("Not found"));
//
//        when(asyncService.deleteAsync("BAD")).thenReturn(failedFuture);
//
//        MvcResult result = mockMvc.perform(delete("/api/course/admin/matakuliah/BAD/async").with(csrf()))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isBadRequest());
//
//        verify(asyncService, times(1)).deleteAsync("BAD");
//    }
//
//    /* ---------- LECTURER MANAGEMENT ASYNC ---------- */
//    @Test @WithMockUser(roles = "ADMIN")
//    void addLecturerAsync_success() throws Exception {
//        UUID lecturerId = UUID.randomUUID();
//        when(asyncService.addLecturerAsync("MK001", lecturerId))
//                .thenReturn(CompletableFuture.completedFuture(null));
//
//        MvcResult result = mockMvc.perform(post("/api/course/admin/matakuliah/MK001/dosen/" + lecturerId + "/async").with(csrf()))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isNoContent());
//
//        verify(asyncService, times(1)).addLecturerAsync("MK001", lecturerId);
//    }
//
//    @Test @WithMockUser(roles = "ADMIN")
//    void removeLecturerAsync_success() throws Exception {
//        UUID lecturerId = UUID.randomUUID();
//        when(asyncService.removeLecturerAsync("MK001", lecturerId))
//                .thenReturn(CompletableFuture.completedFuture(null));
//
//        MvcResult result = mockMvc.perform(delete("/api/course/admin/matakuliah/MK001/dosen/" + lecturerId + "/async").with(csrf()))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isNoContent());
//
//        verify(asyncService, times(1)).removeLecturerAsync("MK001", lecturerId);
//    }
//
//    /* ---------- ADVANCED ASYNC FEATURES ---------- */
//    @Test @WithMockUser(roles = "STUDENT")
//    void searchCoursesAsync_shouldReturnResults() throws Exception {
//        var dto1 = new MataKuliahDto("CS101","Computer Science",3,"CS course",List.of());
//        var dto2 = new MataKuliahDto("CS102","Data Structures",3,"Advanced CS",List.of());
//
//        when(asyncService.searchCoursesAsync("Computer"))
//                .thenReturn(CompletableFuture.completedFuture(List.of(dto1, dto2)));
//
//        MvcResult result = mockMvc.perform(get("/api/course/user/matakuliah/search/async?q=Computer"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].kode").value("CS101"))
//                .andExpect(jsonPath("$[1].kode").value("CS102"));
//
//        verify(asyncService, times(1)).searchCoursesAsync("Computer");
//    }
//
//    @Test @WithMockUser(roles = "ADMIN")
//    void createMultipleAsync_shouldCreateBatch() throws Exception {
//        var dto1 = new MataKuliahDto("MK001","Course 1",3,"Desc 1",List.of());
//        var dto2 = new MataKuliahDto("MK002","Course 2",3,"Desc 2",List.of());
//
//        when(asyncService.createMultipleAsync(anyList()))
//                .thenReturn(CompletableFuture.completedFuture(List.of(dto1, dto2)));
//
//        String body = """
//            [
//                {"kode":"MK001","nama":"Course 1","sks":3,"deskripsi":"Desc 1","dosenPengampu":[]},
//                {"kode":"MK002","nama":"Course 2","sks":3,"deskripsi":"Desc 2","dosenPengampu":[]}
//            ]
//            """;
//
//        MvcResult result = mockMvc.perform(post("/api/course/admin/matakuliah/batch/async").with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(body))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].kode").value("MK001"))
//                .andExpect(jsonPath("$[1].kode").value("MK002"));
//
//        verify(asyncService, times(1)).createMultipleAsync(anyList());
//    }
//
//    /* ---------- COMPLETABLE FUTURE ENDPOINTS ---------- */
//    @Test @WithMockUser(roles = "STUDENT")
//    void listWithCompletableFuture_shouldReturnPage() throws Exception {
//        var dto1 = new MataKuliahDto("MK001","Algoritma",3,"D",List.of());
//        var page = new PageImpl<>(List.of(dto1));
//
//        when(asyncService.findAllAsync(any(Pageable.class)))
//                .thenReturn(CompletableFuture.completedFuture(page));
//
//        MvcResult result = mockMvc.perform(get("/api/course/user/matakuliah/future"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].kode").value("MK001"));
//
//        verify(asyncService, times(1)).findAllAsync(any(Pageable.class));
//    }
//
//    @Test @WithMockUser(roles = "LECTURER")
//    void getWithCompletableFuture_found() throws Exception {
//        var dto = new MataKuliahDto("MK001","Algoritma",3,"Description",List.of());
//        when(asyncService.findByKodeAsync("MK001"))
//                .thenReturn(CompletableFuture.completedFuture(dto));
//
//        MvcResult result = mockMvc.perform(get("/api/course/user/matakuliah/MK001/future"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nama").value("Algoritma"));
//
//        verify(asyncService, times(1)).findByKodeAsync("MK001");
//    }
//
//    @Test @WithMockUser(roles = "LECTURER")
//    void getWithCompletableFuture_notFound() throws Exception {
//        when(asyncService.findByKodeAsync("MK404"))
//                .thenReturn(CompletableFuture.completedFuture(null));
//
//        MvcResult result = mockMvc.perform(get("/api/course/user/matakuliah/MK404/future"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mockMvc.perform(asyncDispatch(result))
//                .andExpect(status().isNotFound());
//
//        verify(asyncService, times(1)).findByKodeAsync("MK404");
//    }
//}