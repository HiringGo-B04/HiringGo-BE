package id.ac.ui.cs.advprog.manajemenlowongan.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.JpaMataKuliahRepository;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LowonganServiceImplTest {

    private LowonganRepository lowonganRepository;
    private LowonganServiceImpl lowonganService;
    private UserRepository userRepository;
    private JpaMataKuliahRepository mataKuliahRepository;
    private Lowongan dummyLowongan;


    @BeforeEach
    void setUp() {
        mataKuliahRepository = mock(JpaMataKuliahRepository.class);
        lowonganRepository = mock(LowonganRepository.class);
        userRepository = mock(UserRepository.class);

        lowonganService = new LowonganServiceImpl(userRepository, lowonganRepository, mataKuliahRepository);
        dummyLowongan = new Lowongan.Builder()
                .matkul("Adpro")
                .year(2025)
                .term("Genap")
                .totalAsdosNeeded(10)
                .totalAsdosRegistered(0)
                .totalAsdosAccepted(0)
                .build();
    }

    @Test
    void testGetLowonganByDosen_WithStats_Success() {
        UUID dosenId = UUID.randomUUID();

        // Mock user with role "LECTURER"
        User dummyUser = new User();
        dummyUser.setUserId(dosenId);
        dummyUser.setRole("LECTURER");

        // Mock lowongan list
        Lowongan l1 = new Lowongan.Builder()
                .totalAsdosNeeded(5)
                .totalAsdosAccepted(2)
                .build();

        Lowongan l2 = new Lowongan.Builder()
                .totalAsdosNeeded(4)
                .totalAsdosAccepted(4)
                .build();

        List<Lowongan> lowongans = List.of(l1, l2);

        MataKuliah mataKuliah = new MataKuliah();
        mataKuliah.addDosenPengampu(dummyUser);
        MataKuliah mataKuliah1 = new MataKuliah();
        mataKuliah1.addDosenPengampu(dummyUser);

        when(userRepository.findByUserId(dosenId)).thenReturn(dummyUser);
        when(mataKuliahRepository.findAll()).thenReturn(List.of(mataKuliah1, mataKuliah)); // Correct mock
        when(lowonganRepository.findAll()).thenReturn(lowongans);
        ResponseEntity<Map<String, Object>> response = lowonganService.getLowonganByDosen(dosenId);

        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Success", body.get("message"));
        assertEquals(2L, body.get("course"));
        assertEquals(6, body.get("assistant")); // 2 + 4 accepted
        assertEquals(1, body.get("vacan"));     // (5-2) + (4-4) = 3
    }

    @Test
    void testGetLowonganByDosen_UserNotFoundOrNotLecturer() {
        UUID dosenId = UUID.randomUUID();

        when(userRepository.findByUserId(dosenId)).thenReturn(null); // or mock a user with non-LECTURER role

        ResponseEntity<Map<String, Object>> response = lowonganService.getLowonganByDosen(dosenId);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(((String) body.get("message")).startsWith("Gagal mengambil lowongan"));
        assertEquals(new ArrayList<>(), body.get("data"));
    }

    @Test
    void testGetAllLowongan() {
        List<Lowongan> list = List.of(dummyLowongan);
        when(lowonganRepository.findAll()).thenReturn(list);

        List<Lowongan> result = lowonganService.getLowongan();

        assertEquals(1, result.size());
        assertEquals(dummyLowongan, result.get(0));
    }

    @Test
    void testGetLowonganByIdFound() {
        when(lowonganRepository.findById(dummyLowongan.getId())).thenReturn(Optional.ofNullable(dummyLowongan));

        Lowongan found = lowonganService.getLowonganById(dummyLowongan.getId());

        assertNotNull(found);
        assertEquals(dummyLowongan, found);
    }

    @Test
    void testGetLowonganByIdNotFound() {
        when(lowonganRepository.findById(any())).thenReturn(Optional.empty());

        Lowongan result = lowonganService.getLowonganById(UUID.randomUUID());

        assertNull(result);
    }


    @Test
    void testDeleteLowongan() {
        UUID id = dummyLowongan.getId();
        lowonganService.deleteLowongan(id);
        verify(lowonganRepository, times(1)).deleteById(id);
    }

    @Test
    void testIsLowonganExistsFalse() {
        when(lowonganRepository.findAll()).thenReturn(List.of());

        boolean exists = lowonganService.isLowonganExists(dummyLowongan);

        assertFalse(exists);
    }

    @Test
    void testAddLowonganWithInvalidYear() {
        dummyLowongan.setTahun(2024); // invalid year
        when(mataKuliahRepository.existsByKode("Adpro")).thenReturn(true);
        when(lowonganRepository.findAll()).thenReturn(List.of());
        when(lowonganRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.existsById(any())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.addLowongan(dummyLowongan);
        });

        assertEquals("Tahun ajaran harus lebih dari atau sama dengan 2025", exception.getMessage());
    }

    @Test
    void testAddLowonganWithValidInput() {
        when(mataKuliahRepository.existsByKode("Adpro")).thenReturn(true);
        when(lowonganRepository.findAll()).thenReturn(List.of());
        when(lowonganRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.existsById(any())).thenReturn(true);
        Lowongan saved = lowonganService.addLowongan(dummyLowongan);

        assertEquals(dummyLowongan, saved);
        verify(lowonganRepository, times(1)).save(dummyLowongan);
    }

    @Test
    void testValidateLowonganFailsWithInvalidMatkul() {
        dummyLowongan.setMatkul("InvalidMatkul");
        when(mataKuliahRepository.existsByKode("InvalidMatkul")).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.validateLowongan(dummyLowongan);
        });

        assertEquals("Nama Mata Kuliah tidak valid", exception.getMessage());
    }

    @Test
    void testValidateLowonganFailsWithInvalidSemester() {
        dummyLowongan.setTerm("Spring"); // invalid
        when(mataKuliahRepository.existsByKode("Adpro")).thenReturn(true);
        when(userRepository.existsById(any())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.validateLowongan(dummyLowongan);
        });

        assertEquals("Semester harus Genap atau Ganjil", exception.getMessage());
    }

    @Test
    void testValidateLowonganFailsWithDuplicate() {
        when(mataKuliahRepository.existsByKode("Adpro")).thenReturn(true);
        when(userRepository.existsById(any())).thenReturn(true);
        Lowongan existing = new Lowongan.Builder()
                .matkul("Adpro")
                .year(2025)
                .term("Genap")
                .totalAsdosNeeded(10)
                .build();

        when(lowonganRepository.findAll()).thenReturn(List.of(existing));

        // New lowongan with same matkul/year/term
        Lowongan newLowongan = new Lowongan.Builder()
                .matkul("Adpro")
                .year(2025)
                .term("Genap")
                .totalAsdosNeeded(5)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            lowonganService.validateLowongan(newLowongan);
        });

        assertEquals("Lowongan dengan kombinasi mata kuliah, semester, dan tahun ajaran yang sama sudah ada", exception.getMessage());
    }

    @Test
    void testIsLowonganExistsTrue() {
        Lowongan existing = new Lowongan.Builder()
                .matkul("Adpro")
                .year(2025)
                .term("Genap")
                .build();

        when(lowonganRepository.findAll()).thenReturn(List.of(existing));

        boolean result = lowonganService.isLowonganExists(dummyLowongan);

        assertTrue(result);
    }

    @Test
    void testUpdateLowonganWithDosenId_Success() {
        UUID dosenId = UUID.randomUUID();

        when(mataKuliahRepository.existsByKode(any())).thenReturn(true);
        when(lowonganRepository.findById(dummyLowongan.getId())).thenReturn(Optional.of(dummyLowongan));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.existsById(any())).thenReturn(true);

        dummyLowongan.setTotalAsdosNeeded(15);
        ResponseEntity<Map<String, Object>> response = lowonganService.updateLowongan(dummyLowongan.getId(), dummyLowongan);
        System.out.println(response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Lowongan berhasil diperbarui", body.get("message"));
        Lowongan updated = (Lowongan) body.get("data");
        assertEquals(15, updated.getTotalAsdosNeeded());
    }

    @Test
    void testUpdateLowonganNotFound_ReturnsNotFound() {
        UUID id = UUID.randomUUID();
        UUID dosenId = UUID.randomUUID();

        when(lowonganRepository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, Object>> response = lowonganService.updateLowongan(id, dummyLowongan);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Lowongan dengan ID tersebut tidak ditemukan", response.getBody().get("message"));
    }

    @Test
    void testUpdateLowonganThrowsException_ReturnsBadRequest() {
        UUID dosenId = UUID.randomUUID();

        when(lowonganRepository.findById(dummyLowongan.getId())).thenReturn(Optional.of(dummyLowongan));
        doThrow(new RuntimeException("Unexpected error")).when(lowonganRepository).save(any(Lowongan.class));

        ResponseEntity<Map<String, Object>> response = lowonganService.updateLowongan(dummyLowongan.getId(), dummyLowongan);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Nama Mata Kuliah tidak valid", response.getBody().get("error"));
    }


}