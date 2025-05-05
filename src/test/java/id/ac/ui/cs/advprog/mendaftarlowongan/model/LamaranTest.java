package id.ac.ui.cs.advprog.mendaftarlowongan.model;

import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LamaranTest {

    @Test
    void testBuildLamaranWithAllFields() {
        UUID mahasiswaId = UUID.randomUUID();
        UUID lowonganId = UUID.randomUUID();

        Lamaran lamaran = new Lamaran.Builder()
                .sks(20)
                .ipk(3.75f)
                .mahasiswa(mahasiswaId)
                .lowongan(lowonganId)
                .build();

        assertNotNull(lamaran.getId());
        assertEquals(20, lamaran.getSks());
        assertEquals(3.75f, lamaran.getIpk());
        assertEquals(StatusLamaran.MENUNGGU, lamaran.getStatus());
        assertEquals(mahasiswaId, lamaran.getIdMahasiswa());
        assertEquals(lowonganId, lamaran.getIdLowongan());
    }

    @Test
    void testDefaultStatusIfNotSet() {
        UUID mahasiswaId = UUID.randomUUID();
        UUID lowonganId = UUID.randomUUID();

        Lamaran lamaran = new Lamaran.Builder()
                .sks(18)
                .ipk(3.5f)
                .mahasiswa(mahasiswaId)
                .lowongan(lowonganId)
                .build();

        assertEquals(StatusLamaran.MENUNGGU, lamaran.getStatus(),
                "Default status harus MENUNGGU jika tidak di-set manual");
    }

    @Test
    void testSettersAndGetters() {
        Lamaran lamaran = new Lamaran.Builder()
                .sks(24)
                .ipk(3.9f)
                .build();

        UUID newMahasiswaId = UUID.randomUUID();
        UUID newLowonganId = UUID.randomUUID();

        lamaran.setIdMahasiswa(newMahasiswaId);
        lamaran.setIdLowongan(newLowonganId);
        lamaran.setStatus(StatusLamaran.DITERIMA);

        assertEquals(newMahasiswaId, lamaran.getIdMahasiswa());
        assertEquals(newLowonganId, lamaran.getIdLowongan());
        assertEquals(StatusLamaran.DITERIMA, lamaran.getStatus());
    }
}
