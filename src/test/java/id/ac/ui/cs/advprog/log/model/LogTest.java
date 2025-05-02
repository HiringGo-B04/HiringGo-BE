package id.ac.ui.cs.advprog.log.model;

import id.ac.ui.cs.advprog.log.enums.StatusLog;
import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest {

    @Test
    void testSetAndGetJudul() {
        Log log = new Log();
        log.setJudul("Asistensi");
        assertEquals("Asistensi", log.getJudul());
    }

    @Test
    void testSetAndGetStatus() {
        Log log = new Log();
        log.setStatus(StatusLog.DITERIMA);
        assertEquals(StatusLog.DITERIMA, log.getStatus());
    }

    @Test
    void testSetAndGetKategori() {
        Log log = new Log();
        log.setKategori(KategoriLog.ASISTENSI);
        assertEquals(KategoriLog.ASISTENSI, log.getKategori());
    }
}
