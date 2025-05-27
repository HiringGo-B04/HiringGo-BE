package id.ac.ui.cs.advprog.account.service;

import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;
import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncAccountHelper {

    private final UserRepository userRepository;
    private final MataKuliahRepository mataKuliahRepository;
    private final LowonganRepository lowonganRepository;

    public AsyncAccountHelper(UserRepository userRepository, MataKuliahRepository mataKuliahRepository,
                              LowonganRepository lowonganRepository) {
        this.userRepository = userRepository;
        this.mataKuliahRepository = mataKuliahRepository;
        this.lowonganRepository = lowonganRepository;
    }

    @Async("taskExecutor")
    public CompletableFuture<List<User>> getUsersByRoleAsync(String role) {
        return CompletableFuture.completedFuture(userRepository.findAllByRole(role));
    }

    @Async("taskExecutor")
    public CompletableFuture<Integer> getNumberOfCoursesAsync() {
        return CompletableFuture.completedFuture(mataKuliahRepository.findAll().size());
    }

    @Async("taskExecutor")
    public CompletableFuture<Integer> getNumberOfVacanciesAsync() {
        return CompletableFuture.completedFuture(lowonganRepository.findAll().size());
    }
}
