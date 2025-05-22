//package id.ac.ui.cs.advprog.course.repository;
//
//import id.ac.ui.cs.advprog.course.model.MataKuliah;
//import jakarta.validation.ConstraintViolationException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@ActiveProfiles("test")
//@Transactional
//class JpaMataKuliahRepositoryTest {
//
//    @Autowired
//    private JpaMataKuliahRepository repo;
//
//    @Autowired
//    private TestEntityManager entityManager; // Useful for test setup and verification
//
//    @BeforeEach
//    void setUp() {
//        // Clear any test data between tests
//        repo.deleteAll();
//        entityManager.flush();
//    }
//
//    /* ---------- Helper ---------- */
//    private MataKuliah mk(String kode, String nama, int sks) {
//        return new MataKuliah(kode, nama, null, sks);
//    }
//
//    /* ---------- Create & Read ---------- */
//    @Test
//    @DisplayName("save lalu findById mengembalikan entitas")
//    void save_thenFindById_shouldReturnEntity() {
//        // Given
//        MataKuliah mataKuliah = mk("IF0001", "Algoritma", 3);
//
//        // When
//        MataKuliah saved = repo.save(mataKuliah);
//        entityManager.flush(); // Ensure data is written to the database
//
//        // Then
//        Optional<MataKuliah> found = repo.findById("IF0001");
//        assertTrue(found.isPresent(), "Entity should be found after saving");
//        assertEquals("Algoritma", found.get().getNama(), "Entity name should match");
//        assertEquals(3, found.get().getSks(), "Entity SKS should match");
//    }
//
//    /* ---------- FindAll ---------- */
//    @Test
//    @DisplayName("findAll memuat semua entitas")
//    void findAll_shouldReturnAllEntities() {
//        // Given
//        repo.save(mk("IF0002", "Logika", 2));
//        repo.save(mk("IF0003", "Basis Data", 3));
//        entityManager.flush();
//
//        // When
//        List<MataKuliah> result = repo.findAll();
//
//        // Then
//        assertEquals(2, result.size(), "Repository should contain exactly 2 entities");
//        assertTrue(result.stream().anyMatch(m -> m.getKode().equals("IF0002")), "Should contain first entity");
//        assertTrue(result.stream().anyMatch(m -> m.getKode().equals("IF0003")), "Should contain second entity");
//    }
//
//    /* ---------- Delete ---------- */
//    @Test
//    @DisplayName("deleteById menghapus entitas")
//    void deleteById_shouldRemoveEntity() {
//        // Given
//        repo.save(mk("IF0004", "Jaringan", 3));
//        entityManager.flush();
//
//        // Verify entity exists before deletion
//        assertTrue(repo.findById("IF0004").isPresent(), "Entity should exist before deletion");
//
//        // When
//        repo.deleteById("IF0004");
//        entityManager.flush();
//
//        // Then
//        assertFalse(repo.findById("IF0004").isPresent(), "Entity should be removed after deletion");
//    }
//
//    /* ---------- Unique constraint ---------- */
//    @Test
//    @DisplayName("kode duplikat menimbulkan DataIntegrityViolationException")
//    void saveDuplicateKode_shouldThrowException() {
//        // Given
//        repo.save(mk("IF0005", "Pemrograman 1", 3));
//        entityManager.flush();
//
//        // When & Then
//        MataKuliah duplicate = mk("IF0005", "Pemrograman 1 Revisi", 4);
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            repo.save(duplicate);
//            entityManager.flush(); // This will trigger the constraint violation
//        }, "Saving entity with duplicate code should throw exception");
//    }
//
//    /* ---------- Validation: null PK ---------- */
//    @Test
//    @DisplayName("menyimpan entitas tanpa kode gagal (null PK)")
//    void saveWithoutKode_shouldFailValidation() {
//        // Given
//        MataKuliah invalid = mk(null, "Tanpa Kode", 2);
//
//        // When & Then
//        assertThrows(ConstraintViolationException.class, () -> {
//            repo.save(invalid);
//            entityManager.flush(); // This will trigger the validation
//        }, "Saving entity with null primary key should throw exception");
//    }
//
//    /* ---------- Update ---------- */
//    @Test
//    @DisplayName("update updates entity correctly")
//    void update_shouldUpdateEntity() {
//        // Given
//        MataKuliah original = mk("IF0006", "Original Name", 3);
//        repo.save(original);
//        entityManager.flush();
//
//        // When
//        MataKuliah updated = repo.findById("IF0006").get();
//        updated.setNama("Updated Name");
//        updated.setSks(4);
//        repo.save(updated);
//        entityManager.flush();
//
//        // Then
//        MataKuliah retrieved = repo.findById("IF0006").get();
//        assertEquals("Updated Name", retrieved.getNama(), "Name should be updated");
//        assertEquals(4, retrieved.getSks(), "SKS should be updated");
//    }
//
//    /* ---------- Custom method ---------- */
//    @Test
//    @DisplayName("deleteByKode removes entity correctly")
//    void deleteByKode_shouldRemoveEntity() {
//        // Given
//        repo.save(mk("IF0007", "For Deletion", 3));
//        entityManager.flush();
//
//        // When
//        repo.deleteByKode("IF0007");
//        entityManager.flush();
//
//        // Then
//        assertFalse(repo.findById("IF0007").isPresent(), "Entity should be removed by custom delete method");
//    }
//}