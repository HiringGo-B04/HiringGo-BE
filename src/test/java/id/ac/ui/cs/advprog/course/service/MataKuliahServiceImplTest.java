package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.mapper.MataKuliahMapper;
import id.ac.ui.cs.advprog.course.mapper.MataKuliahMapper;
import org.mapstruct.factory.Mappers;
import id.ac.ui.cs.advprog.course.repository.InMemoryMataKuliahRepository;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MataKuliahServiceImplTest {

    private MataKuliahService service;

    @BeforeEach
    void setUp() {
        MataKuliahRepository repo  = new InMemoryMataKuliahRepository();
        MataKuliahMapper     mapper = Mappers.getMapper(MataKuliahMapper.class);
        UserRepository       userRepo = Mockito.mock(UserRepository.class);

        service = new MataKuliahServiceImpl(repo, mapper, userRepo);
    }

    /* ---------- CREATE SUCCESS ---------- */
    @Test
    void create_shouldStoreAndReturnDto() {
        MataKuliahDto dto = new MataKuliahDto(
                "IF1234", "Algoritma", 3, "Desc", List.of());

        MataKuliahDto saved = service.create(dto);

        assertEquals("IF1234", saved.kode());
        assertEquals("Algoritma", service.findByKode("IF1234").nama());
    }

    /* ---------- DUPLICATE CREATE ---------- */
    @Test
    void createDuplicate_shouldThrowException() {
        var dto = new MataKuliahDto("IF1234", "Algoritma", 3, null, List.of());
        service.create(dto);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.create(dto));

        assertTrue(ex.getMessage().contains("Kode mata kuliah"));
    }

    /* ---------- PAGING FINDALL ---------- */
    @Test
    void findAll_shouldReturnPage() {
        service.create(new MataKuliahDto("A", "MK‑A", 2, null, List.of()));
        service.create(new MataKuliahDto("B", "MK‑B", 3, null, List.of()));

        Page<MataKuliahDto> page = service.findAll(PageRequest.of(0, 1));

        assertEquals(1, page.getSize());
        assertEquals(2, page.getTotalElements());
    }

    /* ---------- UPDATE (FULL) ---------- */
    @Test
    void update_shouldReplaceAllFields() {
        service.create(new MataKuliahDto("IF1", "Old", 2, "Old", List.of()));

        MataKuliahDto updated = service.update("IF1",
                new MataKuliahDto("IGNORED", "New", 4, "NewDesc", List.of()));

        assertEquals(4, updated.sks());
        assertEquals("New", updated.nama());
    }

    /* ---------- PARTIAL UPDATE ---------- */
    @Test
    void partialUpdate_shouldChangeOnlyNonNull() {
        service.create(new MataKuliahDto("IF1", "Algo", 2, "Old", List.of()));

        MataKuliahPatch patch = new MataKuliahPatch(5, null);
        MataKuliahDto after   = service.partialUpdate("IF1", patch);

        assertEquals(5, after.sks());
        assertEquals("Old", after.deskripsi());          // field null → tidak berubah
    }

    /* ---------- DELETE ---------- */
    @Test
    void delete_shouldRemoveEntity() {
        service.create(new MataKuliahDto("IF1", "Algo", 3, null, List.of()));
        service.delete("IF1");

        assertNull(service.findByKode("IF1"));
    }
}
