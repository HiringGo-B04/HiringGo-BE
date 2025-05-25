package id.ac.ui.cs.advprog.log.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.exception.BadRequestException;
import id.ac.ui.cs.advprog.log.exception.ForbiddenException;
import id.ac.ui.cs.advprog.log.exception.ResourceNotFoundException;
import id.ac.ui.cs.advprog.log.model.Log;
import id.ac.ui.cs.advprog.log.model.LogBuilder;
import id.ac.ui.cs.advprog.log.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LogControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private LogService logService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LogController logController;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private UUID lowonganId;
    private UUID mahasiswaId;
    private UUID dosenId;
    private UUID logId;
    private Log sampleLog;
    private LogDTO logDTO;
    private User mockMahasiswaUser;
    private User mockDosenUser;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(logController)
                .build();

        lowonganId = UUID.randomUUID();
        mahasiswaId = UUID.randomUUID();
        dosenId = UUID.randomUUID();
        logId = UUID.randomUUID();

        sampleLog = new LogBuilder()
                .id(logId)
                .judul("Asistensi")
                .keterangan("Membantu kelas")
                .kategori(KategoriLog.ASISTENSI)
                .tanggalLog(LocalDate.now())
                .waktuMulai(LocalTime.of(9, 0))
                .waktuSelesai(LocalTime.of(11, 0))
                .status(StatusLog.MENUNGGU)
                .idLowongan(lowonganId)
                .idMahasiswa(mahasiswaId)
                .idDosen(dosenId)
                .build();

        logDTO = new LogDTO();
        logDTO.setJudul("Asistensi");
        logDTO.setKeterangan("Membantu kelas");
        logDTO.setKategori("ASISTENSI");
        logDTO.setTanggalLog(LocalDate.now());
        logDTO.setWaktuMulai(LocalTime.of(9, 0));
        logDTO.setWaktuSelesai(LocalTime.of(11, 0));
        logDTO.setIdLowongan(lowonganId);

        mockMahasiswaUser = new User();
        mockMahasiswaUser.setUserId(mahasiswaId);
        mockMahasiswaUser.setUsername("mahasiswa@example.com");
        mockMahasiswaUser.setRole("STUDENT");

        mockDosenUser = new User();
        mockDosenUser.setUserId(dosenId);
        mockDosenUser.setUsername("dosen@example.com");
        mockDosenUser.setRole("LECTURER");

        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetStudentLogs_Success() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        List<Log> logs = Arrays.asList(sampleLog);
        when(logService.findByMahasiswaAndLowongan(mahasiswaId, lowonganId)).thenReturn(logs);

        mockMvc.perform(get("/api/log/student/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Asistensi"));

        verify(logService).findByMahasiswaAndLowongan(mahasiswaId, lowonganId);
    }

    @Test
    void testGetStudentLog_Success() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.findByIdForMahasiswa(logId, mahasiswaId)).thenReturn(sampleLog);

        mockMvc.perform(get("/api/log/student/{logId}", logId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.judul").value("Asistensi"));

        verify(logService).findByIdForMahasiswa(logId, mahasiswaId);
    }

    @Test
    void testCreateStudentLog_Success() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.createLogForMahasiswa(any(LogDTO.class), eq(mahasiswaId)))
                .thenReturn(sampleLog);

        mockMvc.perform(post("/api/log/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.judul").value("Asistensi"));

        verify(logService).createLogForMahasiswa(any(LogDTO.class), eq(mahasiswaId));
    }

    @Test
    void testUpdateStudentLog_Success() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.updateLogForMahasiswa(eq(logId), any(LogDTO.class), eq(mahasiswaId)))
                .thenReturn(sampleLog);

        mockMvc.perform(put("/api/log/student/{logId}", logId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.judul").value("Asistensi"));

        verify(logService).updateLogForMahasiswa(eq(logId), any(LogDTO.class), eq(mahasiswaId));
    }

    @Test
    void testDeleteStudentLog_Success() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        doNothing().when(logService).deleteLogForMahasiswa(logId, mahasiswaId);

        mockMvc.perform(delete("/api/log/student/{logId}", logId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Log berhasil dihapus"));

        verify(logService).deleteLogForMahasiswa(logId, mahasiswaId);
    }

    @Test
    void testGetStudentHonor_Success() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        int tahun = 2025;
        int bulan = 5;

        Map<String, Object> honorData = new HashMap<>();
        honorData.put("bulan", bulan);
        honorData.put("tahun", tahun);
        honorData.put("lowonganId", lowonganId);
        honorData.put("honor", 137500.0);
        honorData.put("formattedHonor", "Rp 137,500.00");

        when(logService.calculateHonorData(mahasiswaId, lowonganId, tahun, bulan)).thenReturn(honorData);

        mockMvc.perform(get("/api/log/student/honor")
                        .param("lowonganId", lowonganId.toString())
                        .param("tahun", String.valueOf(tahun))
                        .param("bulan", String.valueOf(bulan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.honor").value(137500.0))
                .andExpect(jsonPath("$.formattedHonor").value("Rp 137,500.00"));

        verify(logService).calculateHonorData(mahasiswaId, lowonganId, tahun, bulan);
    }

    @Test
    void testGetStudentLogs_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.findByMahasiswaAndLowongan(mahasiswaId, lowonganId))
                .thenThrow(new ResourceNotFoundException("Mahasiswa tidak ditemukan"));

        mockMvc.perform(get("/api/log/student/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Mahasiswa tidak ditemukan"));
    }

    @Test
    void testGetStudentLogs_Forbidden() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.findByMahasiswaAndLowongan(mahasiswaId, lowonganId))
                .thenThrow(new ForbiddenException("Anda belum diterima pada lowongan ini"));

        mockMvc.perform(get("/api/log/student/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Anda belum diterima pada lowongan ini"));
    }

    @Test
    void testGetStudentLogs_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.findByMahasiswaAndLowongan(mahasiswaId, lowonganId))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/log/student/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }

    @Test
    void testGetStudentLog_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.findByIdForMahasiswa(logId, mahasiswaId))
                .thenThrow(new ResourceNotFoundException("Log tidak ditemukan"));

        mockMvc.perform(get("/api/log/student/{logId}", logId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Log tidak ditemukan"));
    }

    @Test
    void testGetStudentLog_Forbidden() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.findByIdForMahasiswa(logId, mahasiswaId))
                .thenThrow(new ForbiddenException("Anda tidak dapat mengakses log milik mahasiswa lain"));

        mockMvc.perform(get("/api/log/student/{logId}", logId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Anda tidak dapat mengakses log milik mahasiswa lain"));
    }

    @Test
    void testGetStudentLog_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.findByIdForMahasiswa(logId, mahasiswaId))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/log/student/{logId}", logId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }

    @Test
    void testCreateStudentLog_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.createLogForMahasiswa(any(LogDTO.class), eq(mahasiswaId)))
                .thenThrow(new ResourceNotFoundException("Lowongan tidak ditemukan"));

        mockMvc.perform(post("/api/log/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Lowongan tidak ditemukan"));
    }

    @Test
    void testCreateStudentLog_BadRequest() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.createLogForMahasiswa(any(LogDTO.class), eq(mahasiswaId)))
                .thenThrow(new BadRequestException("Tanggal log tidak boleh di masa depan"));

        mockMvc.perform(post("/api/log/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tanggal log tidak boleh di masa depan"));
    }

    @Test
    void testCreateStudentLog_IllegalArgument() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.createLogForMahasiswa(any(LogDTO.class), eq(mahasiswaId)))
                .thenThrow(new IllegalArgumentException("Invalid enum value"));

        mockMvc.perform(post("/api/log/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid enum value"));
    }

    @Test
    void testCreateStudentLog_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.createLogForMahasiswa(any(LogDTO.class), eq(mahasiswaId)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/log/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }


    @Test
    void testUpdateStudentLog_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.updateLogForMahasiswa(eq(logId), any(LogDTO.class), eq(mahasiswaId)))
                .thenThrow(new ResourceNotFoundException("Log tidak ditemukan"));

        mockMvc.perform(put("/api/log/student/{logId}", logId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Log tidak ditemukan"));
    }

    @Test
    void testUpdateStudentLog_Forbidden() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.updateLogForMahasiswa(eq(logId), any(LogDTO.class), eq(mahasiswaId)))
                .thenThrow(new ForbiddenException("Anda tidak dapat mengubah log milik mahasiswa lain"));

        mockMvc.perform(put("/api/log/student/{logId}", logId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Anda tidak dapat mengubah log milik mahasiswa lain"));
    }

    @Test
    void testUpdateStudentLog_BadRequest() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.updateLogForMahasiswa(eq(logId), any(LogDTO.class), eq(mahasiswaId)))
                .thenThrow(new BadRequestException("Tidak dapat mengubah log yang sudah diverifikasi"));

        mockMvc.perform(put("/api/log/student/{logId}", logId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tidak dapat mengubah log yang sudah diverifikasi"));
    }

    @Test
    void testUpdateStudentLog_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.updateLogForMahasiswa(eq(logId), any(LogDTO.class), eq(mahasiswaId)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(put("/api/log/student/{logId}", logId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }

    @Test
    void testDeleteStudentLog_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        doThrow(new ResourceNotFoundException("Log tidak ditemukan"))
                .when(logService).deleteLogForMahasiswa(logId, mahasiswaId);

        mockMvc.perform(delete("/api/log/student/{logId}", logId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Log tidak ditemukan"));
    }

    @Test
    void testDeleteStudentLog_BadRequest() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        doThrow(new BadRequestException("Tidak dapat menghapus log yang sudah diverifikasi"))
                .when(logService).deleteLogForMahasiswa(logId, mahasiswaId);

        mockMvc.perform(delete("/api/log/student/{logId}", logId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Tidak dapat menghapus log yang sudah diverifikasi"));
    }

    @Test
    void testDeleteStudentLog_Forbidden() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        doThrow(new ForbiddenException("Anda tidak dapat menghapus log milik mahasiswa lain"))
                .when(logService).deleteLogForMahasiswa(logId, mahasiswaId);

        mockMvc.perform(delete("/api/log/student/{logId}", logId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Anda tidak dapat menghapus log milik mahasiswa lain"));
    }

    @Test
    void testDeleteStudentLog_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        doThrow(new RuntimeException("Database error"))
                .when(logService).deleteLogForMahasiswa(logId, mahasiswaId);

        mockMvc.perform(delete("/api/log/student/{logId}", logId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }


    @Test
    void testGetStudentHonor_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.calculateHonorData(mahasiswaId, lowonganId, 2025, 5))
                .thenThrow(new ResourceNotFoundException("Lowongan tidak ditemukan"));

        mockMvc.perform(get("/api/log/student/honor")
                        .param("lowonganId", lowonganId.toString())
                        .param("tahun", "2025")
                        .param("bulan", "5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Lowongan tidak ditemukan"));
    }

    @Test
    void testGetStudentHonor_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("mahasiswa@example.com");
        when(userRepository.findByUsername("mahasiswa@example.com")).thenReturn(mockMahasiswaUser);
        when(logService.calculateHonorData(mahasiswaId, lowonganId, 2025, 5))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/log/student/honor")
                        .param("lowonganId", lowonganId.toString())
                        .param("tahun", "2025")
                        .param("bulan", "5"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }

    // ========== Lecturer API Tests - Success Cases ==========

    @Test
    void testGetLecturerLogs_Success() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        List<Log> logs = Arrays.asList(sampleLog);
        when(logService.findByDosen(dosenId)).thenReturn(logs);

        mockMvc.perform(get("/api/log/lecturer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Asistensi"));

        verify(logService).findByDosen(dosenId);
    }

    @Test
    void testGetLecturerLogsByLowongan_Success() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        List<Log> logs = Arrays.asList(sampleLog);
        when(logService.findByLowonganAndDosen(lowonganId, dosenId)).thenReturn(logs);

        mockMvc.perform(get("/api/log/lecturer/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Asistensi"));

        verify(logService).findByLowonganAndDosen(lowonganId, dosenId);
    }

    @Test
    void testUpdateLogStatus_Success() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);

        sampleLog.setStatus(StatusLog.DITERIMA);
        when(logService.verifyLog(eq(logId), eq(StatusLog.DITERIMA), eq(dosenId)))
                .thenReturn(sampleLog);

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", "DITERIMA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Log berhasil diterima"))
                .andExpect(jsonPath("$.log.status").value("DITERIMA"));

        verify(logService).verifyLog(eq(logId), eq(StatusLog.DITERIMA), eq(dosenId));
    }

    @Test
    void testUpdateLogStatus_Reject_Success() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);

        sampleLog.setStatus(StatusLog.DITOLAK);
        when(logService.verifyLog(eq(logId), eq(StatusLog.DITOLAK), eq(dosenId)))
                .thenReturn(sampleLog);

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", "DITOLAK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Log berhasil ditolak"))
                .andExpect(jsonPath("$.log.status").value("DITOLAK"));

        verify(logService).verifyLog(eq(logId), eq(StatusLog.DITOLAK), eq(dosenId));
    }


    @Test
    void testGetLecturerLogs_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.findByDosen(dosenId))
                .thenThrow(new ResourceNotFoundException("Dosen tidak ditemukan"));

        mockMvc.perform(get("/api/log/lecturer"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Dosen tidak ditemukan"));
    }

    @Test
    void testGetLecturerLogs_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.findByDosen(dosenId))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/log/lecturer"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }

    @Test
    void testGetLecturerLogsByLowongan_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.findByLowonganAndDosen(lowonganId, dosenId))
                .thenThrow(new ResourceNotFoundException("Lowongan tidak ditemukan"));

        mockMvc.perform(get("/api/log/lecturer/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Lowongan tidak ditemukan"));
    }

    @Test
    void testGetLecturerLogsByLowongan_BadRequest() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.findByLowonganAndDosen(lowonganId, dosenId))
                .thenThrow(new BadRequestException("Invalid request"));

        mockMvc.perform(get("/api/log/lecturer/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request"));
    }

    @Test
    void testGetLecturerLogsByLowongan_Forbidden() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.findByLowonganAndDosen(lowonganId, dosenId))
                .thenThrow(new ForbiddenException("Dosen tidak dapat mengakses log untuk lowongan ini"));

        mockMvc.perform(get("/api/log/lecturer/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Dosen tidak dapat mengakses log untuk lowongan ini"));
    }

    @Test
    void testGetLecturerLogsByLowongan_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.findByLowonganAndDosen(lowonganId, dosenId))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/log/lecturer/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }

    @Test
    void testUpdateLogStatus_InvalidStatus() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Status tidak valid. Gunakan DITERIMA atau DITOLAK"));

        verify(logService, never()).verifyLog(any(), any(), any());
    }

    @Test
    void testUpdateLogStatus_EmptyStatus() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Status tidak boleh kosong"));

        verify(logService, never()).verifyLog(any(), any(), any());
    }

    @Test
    void testUpdateLogStatus_MenungguStatus() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", "MENUNGGU"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Status tidak valid. Gunakan DITERIMA atau DITOLAK"));

        verify(logService, never()).verifyLog(any(), any(), any());
    }

    @Test
    void testUpdateLogStatus_ResourceNotFound() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.verifyLog(eq(logId), eq(StatusLog.DITERIMA), eq(dosenId)))
                .thenThrow(new ResourceNotFoundException("Log tidak ditemukan"));

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", "DITERIMA"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Log tidak ditemukan"));
    }

    @Test
    void testUpdateLogStatus_BadRequest() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.verifyLog(eq(logId), eq(StatusLog.DITERIMA), eq(dosenId)))
                .thenThrow(new BadRequestException("Log ini sudah diverifikasi sebelumnya"));

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", "DITERIMA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Log ini sudah diverifikasi sebelumnya"));
    }

    @Test
    void testUpdateLogStatus_Forbidden() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.verifyLog(eq(logId), eq(StatusLog.DITERIMA), eq(dosenId)))
                .thenThrow(new ForbiddenException("Anda tidak dapat memverifikasi log untuk lowongan ini"));

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", "DITERIMA"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Anda tidak dapat memverifikasi log untuk lowongan ini"));
    }

    @Test
    void testUpdateLogStatus_InternalServerError() throws Exception {
        when(authentication.getName()).thenReturn("dosen@example.com");
        when(userRepository.findByUsername("dosen@example.com")).thenReturn(mockDosenUser);
        when(logService.verifyLog(eq(logId), eq(StatusLog.DITERIMA), eq(dosenId)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(patch("/api/log/lecturer/{logId}/status", logId)
                        .param("status", "DITERIMA"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Terjadi kesalahan: Database error"));
    }
}