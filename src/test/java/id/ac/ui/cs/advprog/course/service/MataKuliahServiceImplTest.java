package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MataKuliahServiceImplTest {

    @Mock
    private MataKuliahRepository repo;

    @Mock
    private MataKuliahMapper mapper;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private MataKuliahServiceImpl service;

    private MataKuliah mataKuliah1;
    private MataKuliah mataKuliah2;
    private MataKuliahDto dto1;
    private MataKuliahDto dto2;
    private User dosen1;
    private User dosen2;
    private UUID dosen1Id;
    private UUID dosen2Id;

    @BeforeEach
    void setUp() {
        // Setup UUID for dosen
        dosen1Id = UUID.randomUUID();
        dosen2Id = UUID.randomUUID();

        // Setup User (Dosen) entities
        dosen1 = new User();
        dosen1.setUserId(dosen1Id);
        dosen1.setUsername("dosen1");

        dosen2 = new User();
        dosen2.setUserId(dosen2Id);
        dosen2.setUsername("dosen2");

        // Setup MataKuliah entities
        mataKuliah1 = new MataKuliah();
        mataKuliah1.setKode("CS101");
        mataKuliah1.setNama("Algoritma");
        mataKuliah1.setSks(3);
        mataKuliah1.setDeskripsi("Mata kuliah algoritma");
        mataKuliah1.setDosenPengampu(new HashSet<>());

        mataKuliah2 = new MataKuliah();
        mataKuliah2.setKode("CS102");
        mataKuliah2.setNama("Database");
        mataKuliah2.setSks(3);
        mataKuliah2.setDeskripsi("Mata kuliah database");
        mataKuliah2.setDosenPengampu(new HashSet<>());

        // Setup DTOs (using record constructor)
        dto1 = new MataKuliahDto("CS101", "Algoritma", 3, "Mata kuliah algoritma", List.of());
        dto2 = new MataKuliahDto("CS102", "Database", 3, "Mata kuliah database", List.of());
    }

    // ============== ASYNC FIND ALL TEST ==============
    @Test
    void testFindAll_Success() throws ExecutionException, InterruptedException {
        // Given
        List<MataKuliah> mataKuliahList = Arrays.asList(mataKuliah1, mataKuliah2);
        when(repo.findAll()).thenReturn(mataKuliahList);
        when(mapper.toDto(mataKuliah1)).thenReturn(dto1);
        when(mapper.toDto(mataKuliah2)).thenReturn(dto2);

        // When
        CompletableFuture<List<MataKuliahDto>> result = service.findAll();

        // Then
        assertNotNull(result);
        List<MataKuliahDto> resultList = result.get();
        assertEquals(2, resultList.size());
        assertEquals("CS101", resultList.get(0).kode());
        assertEquals("Algoritma", resultList.get(0).nama());
        assertEquals("CS102", resultList.get(1).kode());
        assertEquals("Database", resultList.get(1).nama());

        verify(repo, times(1)).findAll();
        verify(mapper, times(1)).toDto(mataKuliah1);
        verify(mapper, times(1)).toDto(mataKuliah2);
    }

    @Test
    void testFindAll_EmptyList() throws ExecutionException, InterruptedException {
        // Given
        when(repo.findAll()).thenReturn(new ArrayList<>());

        // When
        CompletableFuture<List<MataKuliahDto>> result = service.findAll();

        // Then
        assertNotNull(result);
        List<MataKuliahDto> resultList = result.get();
        assertTrue(resultList.isEmpty());

        verify(repo, times(1)).findAll();
        verifyNoInteractions(mapper);
    }

    // ============== FIND BY KODE TEST ==============
    @Test
    void testFindByKode_Found() {
        // Given
        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));
        when(mapper.toDto(mataKuliah1)).thenReturn(dto1);

        // When
        MataKuliahDto result = service.findByKode("CS101");

        // Then
        assertNotNull(result);
        assertEquals("CS101", result.kode());
        assertEquals("Algoritma", result.nama());
        assertEquals(3, result.sks());
        assertEquals("Mata kuliah algoritma", result.deskripsi());

        verify(repo, times(1)).findByKode("CS101");
        verify(mapper, times(1)).toDto(mataKuliah1);
    }

    @Test
    void testFindByKode_NotFound() {
        // Given
        when(repo.findByKode("CS999")).thenReturn(Optional.empty());

        // When
        MataKuliahDto result = service.findByKode("CS999");

        // Then
        assertNull(result);

        verify(repo, times(1)).findByKode("CS999");
        verifyNoInteractions(mapper);
    }

    // ============== CREATE TEST ==============
    @Test
    void testCreate_Success() {
        // Given
        when(mapper.toEntity(dto1)).thenReturn(mataKuliah1);
        when(repo.addMataKuliah(mataKuliah1)).thenReturn(mataKuliah1);
        when(mapper.toDto(mataKuliah1)).thenReturn(dto1);

        // When
        MataKuliahDto result = service.create(dto1);

        // Then
        assertNotNull(result);
        assertEquals("CS101", result.kode());
        assertEquals("Algoritma", result.nama());

        verify(mapper, times(1)).toEntity(dto1);
        verify(repo, times(1)).addMataKuliah(mataKuliah1);
        verify(mapper, times(1)).toDto(mataKuliah1);
    }

    @Test
    void testCreate_WithNullDosenPengampu() {
        // Given
        MataKuliah mkWithNullDosen = new MataKuliah();
        mkWithNullDosen.setKode("CS101");
        mkWithNullDosen.setNama("Algoritma");
        mkWithNullDosen.setSks(3);
        mkWithNullDosen.setDosenPengampu(null); // Null initially

        when(mapper.toEntity(dto1)).thenReturn(mkWithNullDosen);
        when(repo.addMataKuliah(mkWithNullDosen)).thenReturn(mkWithNullDosen);
        when(mapper.toDto(mkWithNullDosen)).thenReturn(dto1);

        // When
        MataKuliahDto result = service.create(dto1);

        // Then
        assertNotNull(result);
        assertNotNull(mkWithNullDosen.getDosenPengampu());
        assertTrue(mkWithNullDosen.getDosenPengampu().isEmpty());

        verify(repo, times(1)).addMataKuliah(mkWithNullDosen);
    }

    @Test
    void testCreate_WithDosenPengampu() {
        // Given - DTO with dosen
        MataKuliahDto dtoWithDosen = new MataKuliahDto(
                "CS101", "Algoritma", 3, "Desc", List.of(dosen1Id)
        );

        MataKuliah mkWithDosen = new MataKuliah();
        mkWithDosen.setKode("CS101");
        mkWithDosen.setDosenPengampu(Set.of(dosen1));

        when(mapper.toEntity(dtoWithDosen)).thenReturn(mkWithDosen);
        when(repo.addMataKuliah(mkWithDosen)).thenReturn(mkWithDosen);
        when(mapper.toDto(mkWithDosen)).thenReturn(dtoWithDosen);

        // When
        MataKuliahDto result = service.create(dtoWithDosen);

        // Then
        assertNotNull(result);
        verify(repo, times(1)).addMataKuliah(mkWithDosen);
    }

    // ============== UPDATE TEST ==============
    @Test
    void testUpdate_Success() {
        // Given
        MataKuliahDto updatedDto = new MataKuliahDto(
                "CS101", "Algoritma Updated", 4, "Updated desc", List.of()
        );

        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));
        when(repo.update(mataKuliah1)).thenReturn(mataKuliah1);
        when(mapper.toDto(mataKuliah1)).thenReturn(updatedDto);

        // When
        MataKuliahDto result = service.update("CS101", updatedDto);

        // Then
        assertNotNull(result);
        assertEquals("Algoritma Updated", result.nama());
        assertEquals(4, result.sks());
        assertEquals("Updated desc", result.deskripsi());

        // Verify entity was updated
        assertEquals("Algoritma Updated", mataKuliah1.getNama());
        assertEquals(4, mataKuliah1.getSks());
        assertEquals("Updated desc", mataKuliah1.getDeskripsi());

        verify(repo, times(1)).findByKode("CS101");
        verify(repo, times(1)).update(mataKuliah1);
        // userRepo.findAllById tidak dipanggil karena dosenPengampu adalah empty list
        verifyNoInteractions(userRepo);
    }

    @Test
    void testUpdate_WithDosenPengampu() {
        // Given
        MataKuliahDto dtoWithDosen = new MataKuliahDto(
                "CS101", "Algoritma", 3, "Desc", List.of(dosen1Id, dosen2Id)
        );

        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));
        when(userRepo.findAllById(List.of(dosen1Id, dosen2Id))).thenReturn(List.of(dosen1, dosen2));
        when(repo.update(mataKuliah1)).thenReturn(mataKuliah1);
        when(mapper.toDto(mataKuliah1)).thenReturn(dtoWithDosen);

        // When
        MataKuliahDto result = service.update("CS101", dtoWithDosen);

        // Then
        assertNotNull(result);
        assertEquals(2, mataKuliah1.getDosenPengampu().size());
        assertTrue(mataKuliah1.getDosenPengampu().contains(dosen1));
        assertTrue(mataKuliah1.getDosenPengampu().contains(dosen2));

        verify(userRepo, times(1)).findAllById(List.of(dosen1Id, dosen2Id));
        verify(repo, times(1)).update(mataKuliah1);
    }

    @Test
    void testUpdate_NotFound() {
        // Given
        when(repo.findByKode("CS999")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.update("CS999", dto1));

        assertEquals("Mata kuliah tidak ditemukan: CS999", exception.getMessage());

        verify(repo, times(1)).findByKode("CS999");
        verify(repo, never()).update(any());
        verifyNoInteractions(userRepo);
    }

    // ============== PARTIAL UPDATE TEST ==============
    @Test
    void testPartialUpdate_Success() {
        // Given
        MataKuliahPatch patch = new MataKuliahPatch(4, "Updated description", null);

        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));
        when(repo.update(mataKuliah1)).thenReturn(mataKuliah1);
        when(mapper.toDto(mataKuliah1)).thenReturn(dto1);

        // When
        MataKuliahDto result = service.partialUpdate("CS101", patch);

        // Then
        assertNotNull(result);

        verify(repo, times(1)).findByKode("CS101");
        verify(mapper, times(1)).patch(patch, mataKuliah1);
        verify(repo, times(1)).update(mataKuliah1);
    }

    @Test
    void testPartialUpdate_WithDosenPengampu() {
        // Given
        MataKuliahPatch patch = new MataKuliahPatch(null, null, List.of(dosen1Id));

        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));
        when(userRepo.findAllById(List.of(dosen1Id))).thenReturn(List.of(dosen1));
        when(repo.update(mataKuliah1)).thenReturn(mataKuliah1);
        when(mapper.toDto(mataKuliah1)).thenReturn(dto1);

        // When
        MataKuliahDto result = service.partialUpdate("CS101", patch);

        // Then
        assertNotNull(result);
        assertEquals(1, mataKuliah1.getDosenPengampu().size());
        assertTrue(mataKuliah1.getDosenPengampu().contains(dosen1));

        verify(userRepo, times(1)).findAllById(List.of(dosen1Id));
        verify(repo, times(1)).update(mataKuliah1);
    }

    @Test
    void testPartialUpdate_NotFound() {
        // Given
        MataKuliahPatch patch = new MataKuliahPatch(4, "Updated", null);
        when(repo.findByKode("CS999")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.partialUpdate("CS999", patch));

        assertEquals("Mata kuliah tidak ditemukan: CS999", exception.getMessage());

        verify(repo, times(1)).findByKode("CS999");
        verify(repo, never()).update(any());
        verifyNoInteractions(mapper);
    }

    // ============== DELETE TEST ==============
    @Test
    void testDelete_Success() {
        // Given
        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));

        // When
        assertDoesNotThrow(() -> service.delete("CS101"));

        // Then
        verify(repo, times(1)).findByKode("CS101");
        verify(repo, times(1)).deleteByKode("CS101");
    }

    @Test
    void testDelete_NotFound() {
        // Given
        when(repo.findByKode("CS999")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.delete("CS999"));

        assertEquals("Mata kuliah tidak ditemukan: CS999", exception.getMessage());

        verify(repo, times(1)).findByKode("CS999");
        verify(repo, never()).deleteByKode(any());
    }

    // ============== ADD LECTURER TEST ==============
    @Test
    void testAddLecturer_Success() {
        // Given
        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));
        when(userRepo.findById(dosen1Id)).thenReturn(Optional.of(dosen1));

        // When
        assertDoesNotThrow(() -> service.addLecturer("CS101", dosen1Id));

        // Then
        assertTrue(mataKuliah1.getDosenPengampu().contains(dosen1));
        verify(repo, times(1)).findByKode("CS101");
        verify(userRepo, times(1)).findById(dosen1Id);
        verify(repo, times(1)).update(mataKuliah1);
    }

    @Test
    void testAddLecturer_MataKuliahNotFound() {
        // Given
        when(repo.findByKode("CS999")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.addLecturer("CS999", dosen1Id));

        assertEquals("Mata kuliah tidak ditemukan: CS999", exception.getMessage());

        verify(repo, times(1)).findByKode("CS999");
        verifyNoInteractions(userRepo);
        verify(repo, never()).update(any());
    }

    @Test
    void testAddLecturer_DosenNotFound() {
        // Given
        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));
        when(userRepo.findById(dosen1Id)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.addLecturer("CS101", dosen1Id));

        assertEquals("Dosen tidak ditemukan: " + dosen1Id, exception.getMessage());

        verify(repo, times(1)).findByKode("CS101");
        verify(userRepo, times(1)).findById(dosen1Id);
        verify(repo, never()).update(any());
    }

    // ============== REMOVE LECTURER TEST ==============
    @Test
    void testRemoveLecturer_Success() {
        // Given
        mataKuliah1.getDosenPengampu().add(dosen1);
        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));

        // When
        assertDoesNotThrow(() -> service.removeLecturer("CS101", dosen1Id));

        // Then
        assertFalse(mataKuliah1.getDosenPengampu().contains(dosen1));
        verify(repo, times(1)).findByKode("CS101");
        verify(repo, times(1)).update(mataKuliah1);
    }

    @Test
    void testRemoveLecturer_MataKuliahNotFound() {
        // Given
        when(repo.findByKode("CS999")).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.removeLecturer("CS999", dosen1Id));

        assertEquals("Mata kuliah tidak ditemukan: CS999", exception.getMessage());

        verify(repo, times(1)).findByKode("CS999");
        verify(repo, never()).update(any());
    }

    @Test
    void testRemoveLecturer_DosenNotInMataKuliah() {
        // Given - mataKuliah1 kosong (tidak ada dosen)
        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.removeLecturer("CS101", dosen1Id));

        assertEquals("Dosen " + dosen1Id + " tidak terdaftar pada mata kuliah", exception.getMessage());

        verify(repo, times(1)).findByKode("CS101");
        verify(repo, never()).update(any());
    }

    @Test
    void testRemoveLecturer_MultipleDosenButRemoveSpecific() {
        // Given - Multiple dosen in mata kuliah
        mataKuliah1.getDosenPengampu().add(dosen1);
        mataKuliah1.getDosenPengampu().add(dosen2);
        when(repo.findByKode("CS101")).thenReturn(Optional.of(mataKuliah1));

        // When - Remove only dosen1
        assertDoesNotThrow(() -> service.removeLecturer("CS101", dosen1Id));

        // Then - Only dosen1 removed, dosen2 still there
        assertFalse(mataKuliah1.getDosenPengampu().contains(dosen1));
        assertTrue(mataKuliah1.getDosenPengampu().contains(dosen2));
        assertEquals(1, mataKuliah1.getDosenPengampu().size());

        verify(repo, times(1)).update(mataKuliah1);
    }
}