//package id.ac.ui.cs.advprog.course.service;
//
//import id.ac.ui.cs.advprog.authjwt.model.User;
//import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
//import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
//import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
//import id.ac.ui.cs.advprog.course.mapper.MataKuliahMapper;
//import id.ac.ui.cs.advprog.course.model.MataKuliah;
//import id.ac.ui.cs.advprog.course.repository.JpaMataKuliahRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//// Ubah ke LENIENT untuk mengatasi strict stubbing match
//@ExtendWith(MockitoExtension.class)
//class MataKuliahServiceImplTest {
//
//    @Mock
//    private JpaMataKuliahRepository repository;
//
//    @Mock
//    private MataKuliahMapper mapper;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private MataKuliahServiceImpl service;
//
//    @BeforeEach
//    void setUp() {
//        // Gunakan lenient mocks untuk mengatasi issue stubbing
//        Mockito.lenient().when(mapper.toDto(any())).thenReturn(null);
//    }
//
//    // Helper methods
//    private MataKuliah createMataKuliah(String kode, String nama, int sks, String deskripsi) {
//        MataKuliah mk = new MataKuliah(kode, nama, deskripsi, sks);
//        mk.setDosenPengampu(new HashSet<>());
//        return mk;
//    }
//
//    private MataKuliahDto createMataKuliahDto(String kode, String nama, int sks, String deskripsi) {
//        return new MataKuliahDto(kode, nama, sks, deskripsi, Collections.emptyList());
//    }
//
//    /* ---------- CREATE SUCCESS ---------- */
//    @Test
//    void create_shouldStoreAndReturnDto() {
//        // Arrange
//        MataKuliahDto inputDto = createMataKuliahDto("IF1234", "Algoritma", 3, "Desc");
//        MataKuliah entity = createMataKuliah("IF1234", "Algoritma", 3, "Desc");
//
//        // Gunakan doReturn...when pattern untuk lebih fleksibel
//        doReturn(entity).when(mapper).toEntity(any());
//        doReturn(entity).when(repository).save(any());
//        doReturn(inputDto).when(mapper).toDto(any());
//
//        // Act
//        MataKuliahDto result = service.create(inputDto);
//
//        // Assert
//        assertEquals("IF1234", result.kode());
//        assertEquals("Algoritma", result.nama());
//    }
//
//    /* ---------- DUPLICATE CREATE ---------- */
//    @Test
//    void createDuplicate_shouldThrowException() {
//        // Arrange
//        MataKuliahDto inputDto = createMataKuliahDto("IF1234", "Algoritma", 3, null);
//        MataKuliah entity = createMataKuliah("IF1234", "Algoritma", 3, null);
//
//        // Setup dengan doReturn/when pattern untuk mocks
//        doReturn(entity).when(mapper).toEntity(any());
//
//        // PENTING: Gunakan doThrow untuk mocking exception
//        doThrow(new RuntimeException("Kode mata kuliah sudah terdaftar"))
//                .when(repository).save(any());
//
//        // Act & Assert
//        Exception exception = assertThrows(RuntimeException.class,
//                () -> service.create(inputDto));
//
//        assertTrue(exception.getMessage().contains("Kode mata kuliah"));
//    }
//
//    /* ---------- PAGING FINDALL ---------- */
//    @Test
//    void findAll_shouldReturnPage() {
//        // Arrange
//        Pageable pageable = PageRequest.of(0, 1);
//        MataKuliah mkA = createMataKuliah("A", "MK-A", 2, null);
//        List<MataKuliah> entityList = List.of(mkA);
//
//        MataKuliahDto dtoA = createMataKuliahDto("A", "MK-A", 2, null);
//
//        Page<MataKuliah> entityPage = new PageImpl<>(entityList, pageable, 2);
//
//        doReturn(entityPage).when(repository).findAll(any(Pageable.class));
//        doReturn(dtoA).when(mapper).toDto(any());
//
//        // Act
//        Page<MataKuliahDto> result = service.findAll(pageable);
//
//        // Assert
//        assertEquals(1, result.getContent().size());
//        assertEquals(2, result.getTotalElements());
//    }
//
//    /* ---------- FIND BY KODE ---------- */
//    @Test
//    void findByKode_shouldReturnDto() {
//        // Arrange
//        String kode = "IF1";
//        MataKuliah entity = createMataKuliah(kode, "Algoritma", 3, "Desc");
//        MataKuliahDto dto = createMataKuliahDto(kode, "Algoritma", 3, "Desc");
//
//        // PENTING: Pastikan method yang benar dipanggil
//        doReturn(Optional.of(entity)).when(repository).findByKode(kode);
//        doReturn(dto).when(mapper).toDto(entity);
//
//        // Act
//        MataKuliahDto result = service.findByKode(kode);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(kode, result.kode());
//        assertEquals("Algoritma", result.nama());
//    }
//
//    /* ---------- UPDATE (FULL) ---------- */
//    @Test
//    void update_shouldReplaceAllFields() {
//        // Arrange
//        String kode = "IF1";
//        MataKuliah oldEntity = createMataKuliah(kode, "Old", 2, "Old");
//        MataKuliahDto updateDto = createMataKuliahDto("IGNORED", "New", 4, "NewDesc");
//        MataKuliah updatedEntity = createMataKuliah(kode, "New", 4, "NewDesc");
//        MataKuliahDto resultDto = createMataKuliahDto(kode, "New", 4, "NewDesc");
//
//        doReturn(Optional.of(oldEntity)).when(repository).findByKode(kode);
//        doReturn(updatedEntity).when(repository).update(any());
//        doReturn(resultDto).when(mapper).toDto(any());
//
//        // Act
//        MataKuliahDto result = service.update(kode, updateDto);
//
//        // Assert
//        assertEquals(4, result.sks());
//        assertEquals("New", result.nama());
//    }
//
//    /* ---------- PARTIAL UPDATE ---------- */
//    @Test
//    void partialUpdate_shouldChangeOnlyNonNull() {
//        // Arrange
//        String kode = "IF1";
//        MataKuliah oldEntity = createMataKuliah(kode, "Algo", 2, "Old");
//        MataKuliahPatch patch = new MataKuliahPatch(5, null, null);
//        MataKuliah updatedEntity = createMataKuliah(kode, "Algo", 5, "Old");
//        MataKuliahDto resultDto = createMataKuliahDto(kode, "Algo", 5, "Old");
//
//        doReturn(Optional.of(oldEntity)).when(repository).findByKode(kode);
//        doNothing().when(mapper).patch(any(), any());
//        doReturn(updatedEntity).when(repository).update(any());
//        doReturn(resultDto).when(mapper).toDto(any());
//
//        // Act
//        MataKuliahDto result = service.partialUpdate(kode, patch);
//
//        // Assert
//        assertEquals(5, result.sks());
//        assertEquals("Old", result.deskripsi());
//    }
//
//    /* ---------- DELETE ---------- */
//    @Test
//    void delete_shouldRemoveEntity() {
//        // Arrange
//        String kode = "IF1";
//        MataKuliah entity = createMataKuliah(kode, "Algo", 3, null);
//
//        doReturn(Optional.of(entity)).when(repository).findByKode(kode);
//        doNothing().when(repository).deleteByKode(kode);
//
//        // Act
//        service.delete(kode);
//
//        // Assert
//        verify(repository).deleteByKode(kode);
//    }
//
//    /* ---------- DELETE NOT FOUND ---------- */
//    @Test
//    void delete_whenNotFound_shouldThrowException() {
//        // Arrange
//        String kode = "NOTFOUND";
//        doReturn(Optional.empty()).when(repository).findByKode(kode);
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class,
//                () -> service.delete(kode));
//
//        verify(repository, never()).deleteByKode(anyString());
//    }
//
//    /* ---------- ADD LECTURER ---------- */
//    @Test
//    void addLecturer_shouldAddToSet() {
//        // Arrange
//        String kode = "MK001";
//        UUID userId = UUID.randomUUID();
//        MataKuliah entity = createMataKuliah(kode, "Test", 3, "Desc");
//        User dosen = new User();
//        dosen.setUserId(userId);
//
//        doReturn(Optional.of(entity)).when(repository).findByKode(kode);
//        doReturn(Optional.of(dosen)).when(userRepository).findById(userId);
//        doReturn(entity).when(repository).update(any());
//
//        // Act
//        service.addLecturer(kode, userId);
//
//        // Assert
//        verify(repository).update(any());
//    }
//
//    /* ---------- REMOVE LECTURER ---------- */
//    @Test
//    void removeLecturer_shouldRemoveFromSet() {
//        // Arrange
//        String kode = "MK001";
//        UUID userId = UUID.randomUUID();
//        MataKuliah entity = createMataKuliah(kode, "Test", 3, "Desc");
//        User dosen = new User();
//        dosen.setUserId(userId);
//        entity.addDosenPengampu(dosen);
//
//        doReturn(Optional.of(entity)).when(repository).findByKode(kode);
//        doReturn(entity).when(repository).update(any());
//
//        // Act
//        service.removeLecturer(kode, userId);
//
//        // Assert
//        verify(repository).update(any());
//    }
//}