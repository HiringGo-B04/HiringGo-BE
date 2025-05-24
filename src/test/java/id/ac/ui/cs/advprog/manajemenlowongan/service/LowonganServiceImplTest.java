package id.ac.ui.cs.advprog.manajemenlowongan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.account.dto.get.GetAllUserDTO;
import id.ac.ui.cs.advprog.account.dto.update.*;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.controller.TestSecurityBeansConfig;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteRequestDTO;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteResponseDTO;
import id.ac.ui.cs.advprog.account.service.AccountService;
import id.ac.ui.cs.advprog.authjwt.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LowonganServiceImplTest {

    private LowonganRepository lowonganRepository;
    private LowonganServiceImpl lowonganService;
    private UserRepository userRepository;
    private Lowongan dummyLowongan;


    @BeforeEach
    void setUp() {
        lowonganRepository = mock(LowonganRepository.class);
        userRepository = mock(UserRepository.class);

        lowonganService = new LowonganServiceImpl(userRepository, lowonganRepository);
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

        when(userRepository.findByUserId(dosenId)).thenReturn(dummyUser);
        when(lowonganRepository.findLowonganByIdDosen(dosenId)).thenReturn(lowongans);

        ResponseEntity<Map<String, Object>> response = lowonganService.getLowonganByDosen(dosenId);

        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Success", body.get("message"));
        assertEquals(2, body.get("lowongan"));
        assertEquals(6, body.get("assistant")); // 2 + 4 accepted
        assertEquals(3, body.get("vacan"));     // (5-2) + (4-4) = 3
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

//    @Test
//    void testAddLowonganInvalidYearThrowsException() {
//        // Asumsikan ada validasi pada tahun (contoh: tahun tidak boleh kurang dari 2020)
//        Lowongan invalidLowongan = new Lowongan.Builder()
//                .matkul("Adpro")
//                .year(2000) // Invalid year
//                .term("Genap")
//                .totalAsdosNeeded(10)
//                .totalAsdosRegistered(0)
//                .totalAsdosAccepted(0)
//                .build();
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            lowonganService.addLowongan(invalidLowongan);
//        });
//
//        verify(lowonganRepository, never()).addLowongan(any());
//    }

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
    void testUpdateLowongan() {
        when(lowonganRepository.findById(dummyLowongan.getId())).thenReturn(Optional.ofNullable(dummyLowongan));
        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(i -> i.getArgument(0));

        dummyLowongan.setTotalAsdosNeeded(15);
        Lowongan updated = lowonganService.updateLowongan(dummyLowongan.getId(), dummyLowongan);

        assertEquals(15, updated.getTotalAsdosNeeded());
        verify(lowonganRepository).save(dummyLowongan);
    }
//    @Test
//    void testGetLowonganByIdNotFound() {
//        when(lowonganRepository.findById(any())).thenReturn(null);
//
//        Lowongan result = lowonganService.getLowonganById(UUID.randomUUID());
//
//        assertNull(result);
//    }
//
//    @Test
//    void testUpdateLowongan() {
//        when(lowonganRepository.findById(dummyLowongan.getId())).thenReturn(Optional.ofNullable(dummyLowongan));
//        when(lowonganRepository.save(any(Lowongan.class))).thenAnswer(i -> i.getArgument(1));
//
//        dummyLowongan.setTotalAsdosNeeded(15);
//        Lowongan updated = lowonganService.updateLowongan(dummyLowongan.getId(), dummyLowongan);
//
//        assertEquals(15, updated.getTotalAsdosNeeded());
//        verify(lowonganRepository).save(dummyLowongan);
//    }

    @Test
    void testDeleteLowongan() {
        UUID id = dummyLowongan.getId();
        lowonganService.deleteLowongan(id);
        verify(lowonganRepository, times(1)).deleteById(id);
    }

//    @Test
//    void testIsLowonganExistsTrue() {
//        when(lowonganRepository.getLowongan()).thenReturn(List.of(dummyLowongan));
//
//        boolean exists = lowonganService.isLowonganExists(dummyLowongan);
//
//        assertTrue(exists);
//    }

    @Test
    void testIsLowonganExistsFalse() {
        when(lowonganRepository.findAll()).thenReturn(List.of());

        boolean exists = lowonganService.isLowonganExists(dummyLowongan);

        assertFalse(exists);
    }
}