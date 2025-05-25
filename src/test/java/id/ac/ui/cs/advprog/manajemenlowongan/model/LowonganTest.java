package id.ac.ui.cs.advprog.manajemenlowongan.model;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LowonganTest {

    @Test
    void testBuildLowonganWithAllFields() {
        UUID id = UUID.randomUUID();

        Lowongan lowongan = new Lowongan.Builder()
                .matkul("Adpro")
                .year(2025)
                .term("Genap")
                .totalAsdosNeeded(3)
                .totalAsdosRegistered(25)
                .totalAsdosAccepted(2)
                .idDosen(id)
                .build();

        assertNotNull(lowongan.getId());
        assertEquals("Adpro", lowongan.getMatkul());
        assertEquals(2025, lowongan.getTahun());
        assertEquals("Genap", lowongan.getTerm());
        assertEquals(3, lowongan.getTotalAsdosNeeded());
        assertEquals(25, lowongan.getTotalAsdosRegistered());
        assertEquals(2, lowongan.getTotalAsdosAccepted());
        assertEquals(id, lowongan.getIdDosen());
    }
}