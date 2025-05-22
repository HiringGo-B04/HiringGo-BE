package id.ac.ui.cs.advprog.course.mapper;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyIterable;

class MataKuliahMapperTest {

    private MataKuliahMapper mapper;
    private UserRepository   userRepoMock;

    @BeforeEach
    void setUp() {
        userRepoMock = Mockito.mock(UserRepository.class);

        // Ambil mapper dari MapStruct dan injeksikan mock repository
        mapper = Mappers.getMapper(MataKuliahMapper.class);
        ReflectionTestUtils.setField(mapper, "userRepository", userRepoMock);
    }

    private User dummyLecturer(String fullName) {
        User u = new User();
        u.setUserId(UUID.randomUUID());
        u.setUsername(fullName.toLowerCase().replace(' ', '.') + "@mail.com");
        u.setFullName(fullName);
        return u;
    }

    /* ---------- ENTITY → DTO ---------- */
    @Test
    void toDto_shouldCopyAllFieldsAndLecturers() {
        User dosenA = dummyLecturer("Dosen A");

        MataKuliah entity = new MataKuliah(
                "MK001", "Algoritma", "Desc", 3);   // konstruktor 4 argumen
        entity.addDosenPengampu(dosenA);

        MataKuliahDto dto = mapper.toDto(entity);

        assertThat(dto.kode()).isEqualTo("MK001");
        assertThat(dto.dosenPengampu()).containsExactly(dosenA.getUserId());
    }

    /* ---------- DTO → ENTITY ---------- */
    @Test
    void toEntity_shouldConvertUuidListToUserSet() {
        Mockito.when(userRepoMock.findAllById(anyIterable()))
                .thenAnswer(inv -> {
                    List<User> result = new ArrayList<>();
                    for (Object obj : (Iterable<?>) inv.getArgument(0)) {
                        UUID id = (UUID) obj;
                        User u  = new User();
                        u.setUserId(id);
                        result.add(u);
                    }
                    return result;
                });

        UUID id = UUID.randomUUID();
        MataKuliahDto dto = new MataKuliahDto(
                "MK002", "Basis Data", 4, "RDBMS",
                List.of(id)
        );

        MataKuliah entity = mapper.toEntity(dto);

        assertThat(entity.getKode()).isEqualTo("MK002");
        assertThat(entity.getDosenPengampu())
                .extracting(User::getUserId)
                .containsExactly(id);
    }

    /* ---------- PATCH ---------- */
    @Test
    void patch_shouldUpdateNonNullFieldsOnly() {
        MataKuliah entity = new MataKuliah(
                "MK003", "Jaringan", "Old", 2);

        MataKuliahPatch patch = new MataKuliahPatch(5, null, null);
        mapper.patch(patch, entity);

        assertThat(entity.getSks()).isEqualTo(5);
        assertThat(entity.getDeskripsi()).isEqualTo("Old");
    }
}
