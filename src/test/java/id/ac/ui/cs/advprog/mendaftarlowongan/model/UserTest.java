package id.ac.ui.cs.advprog.mendaftarlowongan.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testCreateUser() {
        User user = new User("user@example.com", "password123");

        assertNotNull(user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetUserAttributes() {
        User user = new User();
        user.setEmail("anotheruser@example.com");
        user.setPassword("securepass");

        assertEquals("anotheruser@example.com", user.getEmail());
        assertEquals("securepass", user.getPassword());
    }

    @Test
    void testCreateMahasiswa() {
        Mahasiswa mahasiswa = new Mahasiswa("John Doe", "1906272821");

        assertNotNull(mahasiswa.getId());
        assertEquals("John Doe", mahasiswa.getFullName());
        assertEquals("1906272821", mahasiswa.getNim());
        assertNull(mahasiswa.getDaftarLamaran()); // defaultnya null
    }

    @Test
    void testSetMahasiswaAttributes() {
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setFullName("Jane Smith");
        mahasiswa.setNim("2006273831");

        assertEquals("Jane Smith", mahasiswa.getFullName());
        assertEquals("2006273831", mahasiswa.getNim());
    }

    @Test
    void testCreateDosen() {
        Dosen dosen = new Dosen("Dr. Ahmad", "123456789");

        assertNotNull(dosen.getId());
        assertEquals("Dr. Ahmad", dosen.getFullName());
        assertEquals("123456789", dosen.getNip());
    }

    @Test
    void testSetDosenAttributes() {
        Dosen dosen = new Dosen();
        dosen.setFullName("Prof. Budi");
        dosen.setNip("987654321");

        assertEquals("Prof. Budi", dosen.getFullName());
        assertEquals("987654321", dosen.getNip());
    }
}
