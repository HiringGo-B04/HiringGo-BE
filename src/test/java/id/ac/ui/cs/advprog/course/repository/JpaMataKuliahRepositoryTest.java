package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaMataKuliahRepositoryTest {

    @Mock
    private JpaMataKuliahRepository mockRepository;

    private MataKuliah sampleMataKuliah;
    private JpaMataKuliahRepositoryTestImpl testRepository;

    @BeforeEach
    void setUp() {
        sampleMataKuliah = new MataKuliah("IF1234", "Algoritma", "Deskripsi algoritma", 3);
        testRepository = new JpaMataKuliahRepositoryTestImpl();
    }

    /* ---------- Test Default Method: addMataKuliah() ---------- */

    @Test
    void addMataKuliah_defaultMethod_shouldCallSave() {
        // Given
        when(mockRepository.save(sampleMataKuliah)).thenReturn(sampleMataKuliah);
        when(mockRepository.addMataKuliah(sampleMataKuliah)).thenCallRealMethod();

        // When
        MataKuliah result = mockRepository.addMataKuliah(sampleMataKuliah);

        // Then
        assertNotNull(result);
        assertEquals(sampleMataKuliah, result);
        verify(mockRepository, times(1)).save(sampleMataKuliah);
        verify(mockRepository, times(1)).addMataKuliah(sampleMataKuliah);
    }

    @Test
    void addMataKuliah_defaultMethod_withTestImpl_shouldCallSave() {
        // When
        MataKuliah result = testRepository.addMataKuliah(sampleMataKuliah);

        // Then
        assertNotNull(result);
        assertEquals(sampleMataKuliah, result);
        assertTrue(testRepository.saveCalled, "save method should have been called");
        assertEquals(sampleMataKuliah, testRepository.lastSavedEntity);
    }

    /* ---------- Test Default Method: findByKode() ---------- */

    @Test
    void findByKode_defaultMethod_shouldCallFindById() {
        // Given
        String kode = "IF1234";
        Optional<MataKuliah> expected = Optional.of(sampleMataKuliah);
        when(mockRepository.findById(kode)).thenReturn(expected);
        when(mockRepository.findByKode(kode)).thenCallRealMethod();

        // When
        Optional<MataKuliah> result = mockRepository.findByKode(kode);

        // Then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(sampleMataKuliah, result.get());
        verify(mockRepository, times(1)).findById(kode);
        verify(mockRepository, times(1)).findByKode(kode);
    }

    @Test
    void findByKode_defaultMethod_whenNotFound_shouldReturnEmpty() {
        // Given
        String kode = "NONEXISTENT";
        when(mockRepository.findById(kode)).thenReturn(Optional.empty());
        when(mockRepository.findByKode(kode)).thenCallRealMethod();

        // When
        Optional<MataKuliah> result = mockRepository.findByKode(kode);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockRepository, times(1)).findById(kode);
        verify(mockRepository, times(1)).findByKode(kode);
    }

    @Test
    void findByKode_defaultMethod_withTestImpl_shouldCallFindById() {
        // Given
        testRepository.addMataKuliah(sampleMataKuliah); // Add to mock storage

        // When
        Optional<MataKuliah> result = testRepository.findByKode("IF1234");

        // Then
        assertTrue(result.isPresent());
        assertEquals(sampleMataKuliah, result.get());
        assertTrue(testRepository.findByIdCalled, "findById method should have been called");
    }

    /* ---------- Test Default Method: deleteByKode() ---------- */

    @Test
    void deleteByKode_defaultMethod_shouldCallDeleteById() {
        // Given
        String kode = "IF1234";
        doNothing().when(mockRepository).deleteById(kode);
        doCallRealMethod().when(mockRepository).deleteByKode(kode);

        // When
        mockRepository.deleteByKode(kode);

        // Then
        verify(mockRepository, times(1)).deleteById(kode);
        verify(mockRepository, times(1)).deleteByKode(kode);
    }

    @Test
    void deleteByKode_defaultMethod_withTestImpl_shouldCallDeleteById() {
        // Given
        testRepository.addMataKuliah(sampleMataKuliah);

        // When
        testRepository.deleteByKode("IF1234");

        // Then
        assertTrue(testRepository.deleteByIdCalled, "deleteById method should have been called");
        assertEquals("IF1234", testRepository.lastDeletedId);
    }

    /* ---------- Test Inherited update() Method ---------- */

    @Test
    void update_inheritedDefaultMethod_shouldCallAddMataKuliah() {
        // Given - update() comes from MataKuliahRepository interface
        when(mockRepository.save(sampleMataKuliah)).thenReturn(sampleMataKuliah);
        when(mockRepository.addMataKuliah(sampleMataKuliah)).thenCallRealMethod();
        when(mockRepository.update(sampleMataKuliah)).thenCallRealMethod();

        // When
        MataKuliah result = mockRepository.update(sampleMataKuliah);

        // Then
        assertNotNull(result);
        assertEquals(sampleMataKuliah, result);
        verify(mockRepository, times(1)).addMataKuliah(sampleMataKuliah);
        verify(mockRepository, times(1)).save(sampleMataKuliah);
        verify(mockRepository, times(1)).update(sampleMataKuliah);
    }

    /* ---------- Test JpaRepository Methods (Inherited) ---------- */

    @Test
    void findAll_inheritedMethod_shouldWork() {
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
    void save_inheritedMethod_shouldWork() {
        // Given
        when(mockRepository.save(sampleMataKuliah)).thenReturn(sampleMataKuliah);

        // When
        MataKuliah result = mockRepository.save(sampleMataKuliah);

        // Then
        assertNotNull(result);
        assertEquals(sampleMataKuliah, result);
        verify(mockRepository, times(1)).save(sampleMataKuliah);
    }

    /* ---------- Edge Cases ---------- */

    @Test
    void addMataKuliah_withNull_shouldPassNullToSave() {
        // Given
        when(mockRepository.save(null)).thenReturn(null);
        when(mockRepository.addMataKuliah(null)).thenCallRealMethod();

        // When
        MataKuliah result = mockRepository.addMataKuliah(null);

        // Then
        assertNull(result);
        verify(mockRepository, times(1)).save(null);
    }

    @Test
    void findByKode_withNull_shouldPassNullToFindById() {
        // Given
        when(mockRepository.findById(null)).thenReturn(Optional.empty());
        when(mockRepository.findByKode(null)).thenCallRealMethod();

        // When
        Optional<MataKuliah> result = mockRepository.findByKode(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mockRepository, times(1)).findById(null);
    }

    @Test
    void deleteByKode_withNull_shouldPassNullToDeleteById() {
        // Given
        doNothing().when(mockRepository).deleteById(null);
        doCallRealMethod().when(mockRepository).deleteByKode(null);

        // When
        assertDoesNotThrow(() -> mockRepository.deleteByKode(null));

        // Then
        verify(mockRepository, times(1)).deleteById(null);
    }

    /* ---------- Test Implementation untuk Default Methods ---------- */

    /**
     * Test implementation that simulates JpaRepository behavior
     * untuk test default methods secara konkret
     */
    private static class JpaMataKuliahRepositoryTestImpl implements JpaMataKuliahRepository {

        boolean saveCalled = false;
        boolean findByIdCalled = false;
        boolean deleteByIdCalled = false;

        MataKuliah lastSavedEntity;
        String lastDeletedId;

        private MataKuliah storedEntity;

        // Simulate JpaRepository.save()
        @Override
        public <S extends MataKuliah> S save(S entity) {
            saveCalled = true;
            lastSavedEntity = entity;
            storedEntity = entity;
            return entity;
        }

        // Simulate JpaRepository.findById()
        @Override
        public Optional<MataKuliah> findById(String id) {
            findByIdCalled = true;
            if (storedEntity != null && storedEntity.getKode().equals(id)) {
                return Optional.of(storedEntity);
            }
            return Optional.empty();
        }

        // Simulate JpaRepository.deleteById()
        @Override
        public void deleteById(String id) {
            deleteByIdCalled = true;
            lastDeletedId = id;
            if (storedEntity != null && storedEntity.getKode().equals(id)) {
                storedEntity = null;
            }
        }

        // Other required JpaRepository methods (minimal implementation)
        @Override
        public List<MataKuliah> findAll() {
            return storedEntity != null ? Arrays.asList(storedEntity) : Arrays.asList();
        }

        @Override
        public List<MataKuliah> findAllById(Iterable<String> strings) {
            return Arrays.asList();
        }

        @Override
        public long count() {
            return storedEntity != null ? 1 : 0;
        }

        @Override
        public boolean existsById(String s) {
            return storedEntity != null && storedEntity.getKode().equals(s);
        }

        @Override
        public void delete(MataKuliah entity) {
            if (entity != null) {
                deleteById(entity.getKode());
            }
        }

        @Override
        public void deleteAllById(Iterable<? extends String> strings) {
            // Simple implementation
        }

        @Override
        public void deleteAll(Iterable<? extends MataKuliah> entities) {
            // Simple implementation
        }

        @Override
        public void deleteAll() {
            storedEntity = null;
        }

        @Override
        public <S extends MataKuliah> List<S> saveAll(Iterable<S> entities) {
            return Arrays.asList();
        }

        @Override
        public void flush() {
            // Simple implementation
        }

        @Override
        public <S extends MataKuliah> S saveAndFlush(S entity) {
            return save(entity);
        }

        @Override
        public <S extends MataKuliah> List<S> saveAllAndFlush(Iterable<S> entities) {
            return Arrays.asList();
        }

        @Override
        public void deleteAllInBatch(Iterable<MataKuliah> entities) {
            // Simple implementation
        }

        @Override
        public void deleteAllByIdInBatch(Iterable<String> strings) {
            // Simple implementation
        }

        @Override
        public void deleteAllInBatch() {
            // Simple implementation
        }

        @Override
        public MataKuliah getOne(String s) {
            return storedEntity;
        }

        @Override
        public MataKuliah getById(String s) {
            return storedEntity;
        }

        @Override
        public MataKuliah getReferenceById(String s) {
            return storedEntity;
        }

        @Override
        public <S extends MataKuliah> Optional<S> findOne(org.springframework.data.domain.Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends MataKuliah> List<S> findAll(org.springframework.data.domain.Example<S> example) {
            return Arrays.asList();
        }

        @Override
        public <S extends MataKuliah> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) {
            return Arrays.asList();
        }

        @Override
        public <S extends MataKuliah> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) {
            return null;
        }

        @Override
        public <S extends MataKuliah> long count(org.springframework.data.domain.Example<S> example) {
            return 0;
        }

        @Override
        public <S extends MataKuliah> boolean exists(org.springframework.data.domain.Example<S> example) {
            return false;
        }

        @Override
        public <S extends MataKuliah, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        @Override
        public List<MataKuliah> findAll(org.springframework.data.domain.Sort sort) {
            return Arrays.asList();
        }

        @Override
        public org.springframework.data.domain.Page<MataKuliah> findAll(org.springframework.data.domain.Pageable pageable) {
            return null;
        }
    }

    /* ---------- Integration Test ---------- */

    @Test
    void integrationTest_allDefaultMethods_shouldWorkTogether() {
        // Given
        MataKuliah mk = new MataKuliah("IF2001", "Structure Data", "Advanced DS", 4);

        // When & Then - Add
        MataKuliah added = testRepository.addMataKuliah(mk);
        assertEquals(mk, added);
        assertTrue(testRepository.saveCalled);

        // When & Then - Find
        Optional<MataKuliah> found = testRepository.findByKode("IF2001");
        assertTrue(found.isPresent());
        assertEquals("IF2001", found.get().getKode());
        assertTrue(testRepository.findByIdCalled);

        // When & Then - Update (inherited from MataKuliahRepository)
        mk.setDeskripsi("Updated Advanced DS");
        MataKuliah updated = testRepository.update(mk);
        assertEquals("Updated Advanced DS", updated.getDeskripsi());

        // When & Then - Delete
        testRepository.deleteByKode("IF2001");
        assertTrue(testRepository.deleteByIdCalled);
        assertEquals("IF2001", testRepository.lastDeletedId);

        // Verify deletion
        Optional<MataKuliah> deletedCheck = testRepository.findByKode("IF2001");
        assertTrue(deletedCheck.isEmpty());
    }
}