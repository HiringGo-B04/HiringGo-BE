package id.ac.ui.cs.advprog.account.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.course.model.MataKuliah;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AsyncAccountHelperTest {

    private UserRepository userRepository;
    private MataKuliahRepository mataKuliahRepository;
    private LowonganRepository lowonganRepository;
    private AsyncAccountHelper asyncHelper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        mataKuliahRepository = mock(MataKuliahRepository.class);
        lowonganRepository = mock(LowonganRepository.class);
        asyncHelper = new AsyncAccountHelper(userRepository, mataKuliahRepository, lowonganRepository);
    }

    @Test
    void testGetUsersByRoleAsync_ReturnsCorrectUsers() throws Exception {
        // Given
        List<User> mockUsers = List.of(new User(), new User());
        when(userRepository.findAllByRole("STUDENT")).thenReturn(mockUsers);

        // When
        CompletableFuture<List<User>> future = asyncHelper.getUsersByRoleAsync("STUDENT");
        List<User> result = future.get(); // Block for test

        // Then
        assertEquals(2, result.size());
        verify(userRepository).findAllByRole("STUDENT");
    }

    @Test
    void testGetUsersByRoleAsync_EmptyList() throws Exception {
        when(userRepository.findAllByRole("ADMIN")).thenReturn(List.of());

        CompletableFuture<List<User>> future = asyncHelper.getUsersByRoleAsync("ADMIN");
        List<User> result = future.get();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNumberOfCoursesAsync_ReturnsCorrectSize() throws Exception {
        MataKuliah mk1 = mock(MataKuliah.class);
        MataKuliah mk2 = mock(MataKuliah.class);
        MataKuliah mk3 = mock(MataKuliah.class);

        when(mataKuliahRepository.findAll()).thenReturn(List.of(mk1, mk2, mk3));

        CompletableFuture<Integer> future = asyncHelper.getNumberOfCoursesAsync();
        int result = future.get();

        assertEquals(3, result);
        verify(mataKuliahRepository).findAll();
    }


    @Test
    void testGetNumberOfVacanciesAsync_ReturnsCorrectSize() throws Exception {
        Lowongan low1 = mock(Lowongan.class);
        Lowongan low2 = mock(Lowongan.class);

        when(lowonganRepository.findAll()).thenReturn(List.of(low1, low2));

        CompletableFuture<Integer> future = asyncHelper.getNumberOfVacanciesAsync();
        int result = future.get();

        assertEquals(2, result);
        verify(lowonganRepository).findAll();
    }

}
