package id.ac.ui.cs.advprog.manajemenlowongan.controller;

import id.ac.ui.cs.advprog.manajemenlowongan.controller.LowonganController;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.service.LowonganService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LowonganControllerTest {

    @Mock
    private LowonganService lowonganService;

    @InjectMocks
    private LowonganController lowonganController;

    private List<Lowongan> dummyLowongans;

    @BeforeEach
    void setUp() {
        Lowongan lowongan1 = new Lowongan();
        lowongan1.setId(UUID.randomUUID());
        lowongan1.setMatkul("Sistem Interakso");

        Lowongan lowongan2 = new Lowongan();
        lowongan2.setId(UUID.randomUUID());
        lowongan2.setMatkul("Pemrograman Lanjut");

        dummyLowongans = Arrays.asList(lowongan1, lowongan2);
    }

    @Test
    void testGetLowonganSuccess() {
        when(lowonganService.getLowongan()).thenReturn(dummyLowongans);

        ResponseEntity<List<Lowongan>> response = lowonganController.getLowongan();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        Mockito.verify(lowonganService, times(1)).getLowongan();
    }
}
