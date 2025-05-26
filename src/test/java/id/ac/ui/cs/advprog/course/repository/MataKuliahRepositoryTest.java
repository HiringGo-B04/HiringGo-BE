package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MataKuliahRepositoryTest {

    @Mock
    private MataKuliahRepository mockRepository;

    private MataKuliah sampleMataKuliah;
    private MataKuliahRepositoryTestImpl testRepository;

    @BeforeEach
    void setUp() {
        sampleMataKuliah = new MataKuliah("IF1234", "Algoritma", "Deskripsi algoritma", 3);
        testRepository = new MataKuliahRepositoryTestImpl();
    }

    /* ---------- Test dengan Mock Repository ---------- */

    @Test
    void addMataKuliah_shouldCallImplementation() {
        // Given
        when(mockRepository.addMataKuliah(sampleMataKuliah)).thenReturn(sampleMataKuliah);

        // When
        MataKuliah result = mockRepository.addMataKuliah(sampleMataKuliah);

        // Then
        assertNotNull(result);
        assertEquals(sampleMataKuliah, result);
        verify(mockRepository, times(1)).addMataKuliah(sampleMataKuliah);
    }

    @Test
    void findAll_shouldCallImplementation() {
        // Given
        List<MataKuliah> expectedList = Arrays.asList(sampleMataKuliah);
        when(mockRepository.findAll()).thenReturn(expectedList);

        // When
        List<MataKuliah> result = mockRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(expectedList, result);
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    void findByKode_shouldCallImplementation() {
        // Given
        String kode = "IF1234";
        Optional<MataKuliah> expected = Optional.of(sampleMataKuliah);
        when(mockRepository.findByKode(kode)).thenReturn(expected);

        // When
        Optional<MataKuliah> result = mockRepository.findByKode(kode);

        // Then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(sampleMataKuliah, result.get());
        verify(mockRepository, times(1)).findByKode(kode);
    }

    @Test
    void findByKode_whenNotFound_shouldReturnEmpty() {
        // Given
        String kode = "NONEXISTENT";
        when(mockRepository.findByKode(kode)).thenReturn(Optional.empty());

        // When
        Optional<MataKuliah> result = mockRepository.findByKode(kode);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockRepository, times(1)).findByKode(kode);
    }

    @Test
    void deleteByKode_shouldCallImplementation() {
        // Given
        String kode = "IF1234";

        // When
        mockRepository.deleteByKode(kode);

        // Then
        verify(mockRepository, times(1)).deleteByKode(kode);
    }

    /* ---------- Test Default Method dengan Implementasi Konkret ---------- */

    @Test
    void update_defaultMethod_shouldCallAddMataKuliah() {
        // Given - menggunakan implementasi konkret untuk test default method

        // When
        MataKuliah result = testRepository.update(sampleMataKuliah);

        // Then
        assertNotNull(result);
        assertEquals(sampleMataKuliah, result);
        assertEquals("IF1234", result.getKode());
        assertTrue(testRepository.addMataKuliahCalled, "addMataKuliah should have been called");
    }

    @Test
    void update_defaultMethod_withNullParameter_shouldPassNullToAddMataKuliah() {
        // When
        MataKuliah result = testRepository.update(null);

        // Then
        assertNull(result);
        assertTrue(testRepository.addMataKuliahCalled, "addMataKuliah should have been called even with null");
    }

    /* ---------- Test dengan Mock untuk Default Method ---------- */

    @Test
    void update_defaultMethod_withMock_shouldCallAddMataKuliah() {
        // Given
        // Kita perlu menggunakan spy untuk test default method dengan mock
        MataKuliahRepository spyRepository = spy(MataKuliahRepository.class);
        when(spyRepository.addMataKuliah(sampleMataKuliah)).thenReturn(sampleMataKuliah);

        // When
        MataKuliah result = spyRepository.update(sampleMataKuliah);

        // Then
        assertNotNull(result);
        assertEquals(sampleMataKuliah, result);
        verify(spyRepository, times(1)).addMataKuliah(sampleMataKuliah);
        verify(spyRepository, times(1)).update(sampleMataKuliah);
    }

    /* ---------- Implementasi Sederhana untuk Test Default Method ---------- */

    /**
     * Implementasi sederhana dari MataKuliahRepository untuk test default method
     */
    private static class MataKuliahRepositoryTestImpl implements MataKuliahRepository {

        boolean addMataKuliahCalled = false;
        private MataKuliah lastAddedMataKuliah;

        @Override
        public MataKuliah addMataKuliah(MataKuliah mk) {
            addMataKuliahCalled = true;
            lastAddedMataKuliah = mk;
            return mk; // Simple implementation - just return the input
        }

        @Override
        public List<MataKuliah> findAll() {
            return Arrays.asList(); // Simple implementation
        }

        @Override
        public Optional<MataKuliah> findByKode(String kode) {
            if (lastAddedMataKuliah != null && kode.equals(lastAddedMataKuliah.getKode())) {
                return Optional.of(lastAddedMataKuliah);
            }
            return Optional.empty();
        }

        @Override
        public void deleteByKode(String kode) {
            // Simple implementation - do nothing
            if (lastAddedMataKuliah != null && kode.equals(lastAddedMataKuliah.getKode())) {
                lastAddedMataKuliah = null;
            }
        }
    }

    /* ---------- Integration Test dengan Implementasi Konkret ---------- */

    @Test
    void integrationTest_allMethods_shouldWorkTogether() {
        // Given
        MataKuliah mk1 = new MataKuliah("IF1001", "Pengantar Informatika", "Desc1", 2);
        MataKuliah mk2 = new MataKuliah("IF1002", "Pemrograman Dasar", "Desc2", 3);

        // When & Then - Add
        MataKuliah added1 = testRepository.addMataKuliah(mk1);
        assertEquals(mk1, added1);

        // When & Then - Update (using default method)
        mk1.setDeskripsi("Updated description");
        MataKuliah updated = testRepository.update(mk1);
        assertEquals(mk1, updated);
        assertEquals("Updated description", updated.getDeskripsi());

        // When & Then - Find by kode
        Optional<MataKuliah> found = testRepository.findByKode("IF1001");
        assertTrue(found.isPresent());
        assertEquals("IF1001", found.get().getKode());

        // When & Then - Find by non-existent kode
        Optional<MataKuliah> notFound = testRepository.findByKode("NONEXISTENT");
        assertTrue(notFound.isEmpty());

        // When & Then - Delete
        testRepository.deleteByKode("IF1001");
        Optional<MataKuliah> deletedCheck = testRepository.findByKode("IF1001");
        assertTrue(deletedCheck.isEmpty());
    }

    /* ---------- Edge Cases ---------- */

    @Test
    void addMataKuliah_withNullParameter_mock() {
        // Given
        when(mockRepository.addMataKuliah(null)).thenReturn(null);

        // When
        MataKuliah result = mockRepository.addMataKuliah(null);

        // Then
        assertNull(result);
        verify(mockRepository, times(1)).addMataKuliah(null);
    }

    @Test
    void findByKode_withNullParameter_mock() {
        // Given
        when(mockRepository.findByKode(null)).thenReturn(Optional.empty());

        // When
        Optional<MataKuliah> result = mockRepository.findByKode(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockRepository, times(1)).findByKode(null);
    }

    @Test
    void findByKode_withEmptyString_mock() {
        // Given
        when(mockRepository.findByKode("")).thenReturn(Optional.empty());

        // When
        Optional<MataKuliah> result = mockRepository.findByKode("");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockRepository, times(1)).findByKode("");
    }

    @Test
    void deleteByKode_withNullParameter_shouldNotThrow() {
        // When & Then
        assertDoesNotThrow(() -> mockRepository.deleteByKode(null));
        verify(mockRepository, times(1)).deleteByKode(null);
    }

    @Test
    void deleteByKode_withEmptyString_shouldNotThrow() {
        // When & Then
        assertDoesNotThrow(() -> mockRepository.deleteByKode(""));
        verify(mockRepository, times(1)).deleteByKode("");
    }
}