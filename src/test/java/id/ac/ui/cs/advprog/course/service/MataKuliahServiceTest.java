package id.ac.ui.cs.advprog.course.service;

import id.ac.ui.cs.advprog.course.dto.MataKuliahDto;
import id.ac.ui.cs.advprog.course.dto.MataKuliahPatch;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test untuk memastikan MataKuliahService interface memiliki method signature yang benar
 */
@SpringBootTest
@ActiveProfiles("test")
class MataKuliahServiceTest {

    @Test
    void testServiceInterfaceExists() {
        // Pastikan interface MataKuliahService ada
        assertNotNull(MataKuliahService.class);
    }

    @Test
    void testFindAllMethodSignature() throws NoSuchMethodException {
        // Pastikan method findAll() ada dan return CompletableFuture<List<MataKuliahDto>>
        var method = MataKuliahService.class.getMethod("findAll");

        assertNotNull(method);
        assertEquals(CompletableFuture.class, method.getReturnType());
        assertEquals(0, method.getParameterCount()); // Tidak ada parameter
    }

    @Test
    void testFindByKodeMethodSignature() throws NoSuchMethodException {
        // Pastikan method findByKode(String) ada dan return MataKuliahDto
        var method = MataKuliahService.class.getMethod("findByKode", String.class);

        assertNotNull(method);
        assertEquals(MataKuliahDto.class, method.getReturnType());
        assertEquals(1, method.getParameterCount());
        assertEquals(String.class, method.getParameterTypes()[0]);
    }

    @Test
    void testCreateMethodSignature() throws NoSuchMethodException {
        // Pastikan method create(MataKuliahDto) ada dan return MataKuliahDto
        var method = MataKuliahService.class.getMethod("create", MataKuliahDto.class);

        assertNotNull(method);
        assertEquals(MataKuliahDto.class, method.getReturnType());
        assertEquals(1, method.getParameterCount());
        assertEquals(MataKuliahDto.class, method.getParameterTypes()[0]);
    }

    @Test
    void testUpdateMethodSignature() throws NoSuchMethodException {
        // Pastikan method update(String, MataKuliahDto) ada dan return MataKuliahDto
        var method = MataKuliahService.class.getMethod("update", String.class, MataKuliahDto.class);

        assertNotNull(method);
        assertEquals(MataKuliahDto.class, method.getReturnType());
        assertEquals(2, method.getParameterCount());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(MataKuliahDto.class, method.getParameterTypes()[1]);
    }

    @Test
    void testPartialUpdateMethodSignature() throws NoSuchMethodException {
        // Pastikan method partialUpdate(String, MataKuliahPatch) ada dan return MataKuliahDto
        var method = MataKuliahService.class.getMethod("partialUpdate", String.class, MataKuliahPatch.class);

        assertNotNull(method);
        assertEquals(MataKuliahDto.class, method.getReturnType());
        assertEquals(2, method.getParameterCount());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(MataKuliahPatch.class, method.getParameterTypes()[1]);
    }

    @Test
    void testDeleteMethodSignature() throws NoSuchMethodException {
        // Pastikan method delete(String) ada dan return void
        var method = MataKuliahService.class.getMethod("delete", String.class);

        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
        assertEquals(1, method.getParameterCount());
        assertEquals(String.class, method.getParameterTypes()[0]);
    }

    @Test
    void testAddLecturerMethodSignature() throws NoSuchMethodException {
        // Pastikan method addLecturer(String, UUID) ada dan return void
        var method = MataKuliahService.class.getMethod("addLecturer", String.class, UUID.class);

        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
        assertEquals(2, method.getParameterCount());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(UUID.class, method.getParameterTypes()[1]);
    }

    @Test
    void testRemoveLecturerMethodSignature() throws NoSuchMethodException {
        // Pastikan method removeLecturer(String, UUID) ada dan return void
        var method = MataKuliahService.class.getMethod("removeLecturer", String.class, UUID.class);

        assertNotNull(method);
        assertEquals(void.class, method.getReturnType());
        assertEquals(2, method.getParameterCount());
        assertEquals(String.class, method.getParameterTypes()[0]);
        assertEquals(UUID.class, method.getParameterTypes()[1]);
    }

    @Test
    void testAllMethodsExist() {
        // Pastikan interface memiliki semua method yang diharapkan (8 methods)
        var methods = MataKuliahService.class.getDeclaredMethods();
        assertEquals(8, methods.length);
    }
}