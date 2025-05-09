package id.ac.ui.cs.advprog.log.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import id.ac.ui.cs.advprog.log.dto.LogDTO;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
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
import java.util.List;
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

        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    //API Mahasiswa

    @Test
    void testGetStudentLogs_Success() throws Exception {
        when(authentication.getName()).thenReturn(mahasiswaId.toString());
        List<Log> logs = Arrays.asList(sampleLog);
        when(logService.findByMahasiswaAndLowongan(mahasiswaId, lowonganId)).thenReturn(logs);

        mockMvc.perform(get("/api/hiringgo/student/logs/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Asistensi"));

        verify(logService).findByMahasiswaAndLowongan(mahasiswaId, lowonganId);
    }

    @Test
    void testGetStudentLog_Success() throws Exception {
        when(authentication.getName()).thenReturn(mahasiswaId.toString());
        when(logService.findById(logId)).thenReturn(sampleLog);

        mockMvc.perform(get("/api/hiringgo/student/logs/{logId}", logId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.judul").value("Asistensi"));

        verify(logService).findById(logId);
    }

    @Test
    void testGetStudentLog_Forbidden() throws Exception {
        UUID differentMahasiswaId = UUID.randomUUID();
        when(authentication.getName()).thenReturn(differentMahasiswaId.toString());
        when(logService.findById(logId)).thenReturn(sampleLog);

        mockMvc.perform(get("/api/hiringgo/student/logs/{logId}", logId))
                .andExpect(status().isForbidden());

        verify(logService).findById(logId);
    }

    @Test
    void testCreateStudentLog_Success() throws Exception {
        when(authentication.getName()).thenReturn(mahasiswaId.toString());
        when(logService.createLogForMahasiswa(any(LogDTO.class), eq(mahasiswaId), eq(dosenId)))
                .thenReturn(sampleLog);

        mockMvc.perform(post("/api/hiringgo/student/logs")
                        .param("dosenId", dosenId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.judul").value("Asistensi"));

        verify(logService).createLogForMahasiswa(any(LogDTO.class), eq(mahasiswaId), eq(dosenId));
    }

    @Test
    void testUpdateStudentLog_Success() throws Exception {
        when(authentication.getName()).thenReturn(mahasiswaId.toString());
        when(logService.updateLogForMahasiswa(eq(logId), any(LogDTO.class), eq(mahasiswaId)))
                .thenReturn(sampleLog);

        mockMvc.perform(put("/api/hiringgo/student/logs/{logId}", logId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.judul").value("Asistensi"));

        verify(logService).updateLogForMahasiswa(eq(logId), any(LogDTO.class), eq(mahasiswaId));
    }

    @Test
    void testDeleteStudentLog_Success() throws Exception {
        when(authentication.getName()).thenReturn(mahasiswaId.toString());
        doNothing().when(logService).deleteLogForMahasiswa(logId, mahasiswaId);

        mockMvc.perform(delete("/api/hiringgo/student/logs/{logId}", logId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Log berhasil dihapus"));

        verify(logService).deleteLogForMahasiswa(logId, mahasiswaId);
    }

    @Test
    void testGetStudentHonor_Success() throws Exception {
        when(authentication.getName()).thenReturn(mahasiswaId.toString());
        int tahun = 2025;
        int bulan = 5;
        double honor = 137500.0;

        when(logService.calculateHonor(mahasiswaId, lowonganId, tahun, bulan)).thenReturn(honor);

        mockMvc.perform(get("/api/hiringgo/student/honor")
                        .param("lowonganId", lowonganId.toString())
                        .param("tahun", String.valueOf(tahun))
                        .param("bulan", String.valueOf(bulan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.honor").value(honor))
                .andExpect(jsonPath("$.formattedHonor").exists());

        verify(logService).calculateHonor(mahasiswaId, lowonganId, tahun, bulan);
    }

    // API Dosen

    @Test
    void testGetLecturerLogs_Success() throws Exception {
        when(authentication.getName()).thenReturn(dosenId.toString());
        List<Log> logs = Arrays.asList(sampleLog);
        when(logService.findByDosen(dosenId)).thenReturn(logs);

        mockMvc.perform(get("/api/hiringgo/lecturer/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Asistensi"));

        verify(logService).findByDosen(dosenId);
    }

    @Test
    void testGetLecturerLogsByLowongan_Success() throws Exception {
        when(authentication.getName()).thenReturn(dosenId.toString());
        List<Log> logs = Arrays.asList(sampleLog);
        when(logService.findByLowonganAndDosen(lowonganId, dosenId)).thenReturn(logs);

        mockMvc.perform(get("/api/hiringgo/lecturer/logs/lowongan/{lowonganId}", lowonganId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].judul").value("Asistensi"));

        verify(logService).findByLowonganAndDosen(lowonganId, dosenId);
    }

    @Test
    void testUpdateLogStatus_Success() throws Exception {
        when(authentication.getName()).thenReturn(dosenId.toString());
        sampleLog.setStatus(StatusLog.DITERIMA);

        when(logService.verifyLog(eq(logId), eq(StatusLog.DITERIMA), eq(dosenId)))
                .thenReturn(sampleLog);

        mockMvc.perform(patch("/api/hiringgo/lecturer/logs/{logId}/status", logId)
                        .param("status", "DITERIMA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DITERIMA"));

        verify(logService).verifyLog(eq(logId), eq(StatusLog.DITERIMA), eq(dosenId));
    }

    @Test
    void testUpdateLogStatus_InvalidStatus() throws Exception {
        when(authentication.getName()).thenReturn(dosenId.toString());

        mockMvc.perform(patch("/api/hiringgo/lecturer/logs/{logId}/status", logId)
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Status tidak valid. Gunakan DITERIMA atau DITOLAK"));

        verify(logService, never()).verifyLog(any(), any(), any());
    }
}