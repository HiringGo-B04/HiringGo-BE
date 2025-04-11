package id.ac.ui.cs.advprog.log.model;

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
        log.setStatus("DITERIMA");
        assertEquals("DITERIMA", log.getStatus());
    }
}
