package id.ac.ui.cs.advprog.account.service;

import id.ac.ui.cs.advprog.account.dto.delete.DeleteRequestDTO;
import id.ac.ui.cs.advprog.account.dto.delete.DeleteResponseDTO;
import id.ac.ui.cs.advprog.account.dto.get.GetAllUserDTO;
import id.ac.ui.cs.advprog.account.dto.update.ResponseUpdateDTO;
import id.ac.ui.cs.advprog.account.dto.update.UserUpdateDTO;
import id.ac.ui.cs.advprog.account.service.strategy.AdminRoleUpdateStrategy;
import id.ac.ui.cs.advprog.account.service.strategy.LecturerRoleUpdateStrategy;
import id.ac.ui.cs.advprog.account.service.strategy.RoleUpdateStrategy;
import id.ac.ui.cs.advprog.account.service.strategy.StudentRoleUpdateStrategy;
import id.ac.ui.cs.advprog.authjwt.model.User;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import id.ac.ui.cs.advprog.course.repository.MataKuliahRepository;
import id.ac.ui.cs.advprog.manajemenlowongan.repository.LowonganRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService{
    private final UserRepository userRepository;
    private final LowonganRepository lowonganRepository;
    private final MataKuliahRepository mataKuliahRepository;
    private final AsyncAccountHelper asyncHelper;


    public AccountService(UserRepository userRepository, LowonganRepository lowonganRepository, MataKuliahRepository mataKuliahRepository, AsyncAccountHelper asyncHelper) {
        this.userRepository = userRepository;
        this.lowonganRepository = lowonganRepository;
        this.mataKuliahRepository = mataKuliahRepository;
        this.asyncHelper = asyncHelper;
    }

    @Transactional
    public ResponseEntity<DeleteResponseDTO> delete(DeleteRequestDTO email){
        try {
            User user = userRepository.findByUsername(email.email());
            if(user == null) {
                throw new IllegalArgumentException("User not found");
            }

            userRepository.deleteByUsername(user.getUsername());

            return new ResponseEntity<>(
                    new DeleteResponseDTO("accept", "Succes delete user"),
                    HttpStatus.valueOf(200));
        }

        catch (Exception e) {
            return new ResponseEntity<>(
                    new DeleteResponseDTO("error", e.getMessage()),
                    HttpStatus.valueOf(400));
        }
    }

    public ResponseEntity<ResponseUpdateDTO> update(UserUpdateDTO userUpdateDTO){
        try{
            User user = userRepository.findByUsername(userUpdateDTO.username);
            if(user == null) {
                throw new IllegalArgumentException("User not found");
            }

            RoleUpdateStrategy strategy = getRoleUpdateStrategy(userUpdateDTO);

            return strategy.updateRole(userUpdateDTO, user);
        }
        catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseUpdateDTO("error", e.getMessage()),

                    HttpStatus.valueOf(400));
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<ResponseEntity<GetAllUserDTO>> getAllUser() {

        CompletableFuture<List<User>> studentFuture = asyncHelper.getUsersByRoleAsync("STUDENT");
        CompletableFuture<List<User>> lecturerFuture = asyncHelper.getUsersByRoleAsync("LECTURER");
        CompletableFuture<List<User>> adminFuture = asyncHelper.getUsersByRoleAsync("ADMIN");
        CompletableFuture<Integer> courseCountFuture = asyncHelper.getNumberOfCoursesAsync();
        CompletableFuture<Integer> vacancyCountFuture = asyncHelper.getNumberOfVacanciesAsync();

        return CompletableFuture.allOf(studentFuture, lecturerFuture, adminFuture, courseCountFuture, vacancyCountFuture)
                .handle((ignored, throwable) -> {
                    if (throwable != null) {
                        return new ResponseEntity<>(
                                new GetAllUserDTO("error", throwable.getCause().getMessage(), 0, 0, 0, 0, null),
                                HttpStatus.BAD_REQUEST);
                    }

                    try {
                        List<User> students = studentFuture.get();
                        List<User> lecturers = lecturerFuture.get();
                        List<User> admins = adminFuture.get();
                        int numberOfCourses = courseCountFuture.get();
                        int numberOfVacancies = vacancyCountFuture.get();

                        List<User> users = new ArrayList<>();
                        users.addAll(students);
                        users.addAll(lecturers);
                        users.addAll(admins);

                        for (User user : users) {
                            user.setPassword(null);
                        }

                        return new ResponseEntity<>(
                                new GetAllUserDTO("accept", "test", lecturers.size(), students.size(), numberOfVacancies, numberOfCourses, users),
                                HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>(
                                new GetAllUserDTO("error", e.getMessage(), 0, 0, 0, 0, null),
                                HttpStatus.BAD_REQUEST);
                    }
                });
    }

    private RoleUpdateStrategy getRoleUpdateStrategy(UserUpdateDTO userUpdateDTO) {
        RoleUpdateStrategy strategy;
        if(userUpdateDTO.role.equals("ADMIN")){
            strategy = new AdminRoleUpdateStrategy(userRepository);
        }
        else if(userUpdateDTO.role.equals("LECTURER")){
            strategy = new LecturerRoleUpdateStrategy(userRepository);
        }
        else if(userUpdateDTO.role.equals("STUDENT")){
            strategy = new StudentRoleUpdateStrategy(userRepository);
        }
        else{
            throw new IllegalArgumentException("Role not found");
        }
        return strategy;
    }
}