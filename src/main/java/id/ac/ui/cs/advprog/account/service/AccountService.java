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
import id.ac.ui.cs.advprog.authjwt.model.UserRole;
import id.ac.ui.cs.advprog.authjwt.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService{
    private final UserRepository userRepository;
    private final AsyncAccountHelper asyncHelper;
    private final String defaultAcceptResponse = "accept";
    private final String defaultErrorResponse = "error";

    public AccountService(UserRepository userRepository, AsyncAccountHelper asyncHelper) {
        this.userRepository = userRepository;
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
                    new DeleteResponseDTO(defaultAcceptResponse, "Succes delete user"),
                    HttpStatus.valueOf(200));
        }

        catch (Exception e) {
            return new ResponseEntity<>(
                    new DeleteResponseDTO(defaultErrorResponse, e.getMessage()),
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
                    new ResponseUpdateDTO(defaultErrorResponse, e.getMessage()),

                    HttpStatus.valueOf(400));
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<ResponseEntity<GetAllUserDTO>> getAllUser() {

        CompletableFuture<List<User>> studentFuture = asyncHelper.getUsersByRoleAsync(UserRole.STUDENT.getValue());
        CompletableFuture<List<User>> lecturerFuture = asyncHelper.getUsersByRoleAsync(UserRole.LECTURER.getValue());
        CompletableFuture<List<User>> adminFuture = asyncHelper.getUsersByRoleAsync(UserRole.ADMIN.getValue());
        CompletableFuture<Integer> courseCountFuture = asyncHelper.getNumberOfCoursesAsync();
        CompletableFuture<Integer> vacancyCountFuture = asyncHelper.getNumberOfVacanciesAsync();

        return CompletableFuture.allOf(studentFuture, lecturerFuture, adminFuture, courseCountFuture, vacancyCountFuture)
                .handle((ignored, throwable) -> {

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
                                new GetAllUserDTO(defaultAcceptResponse, "test", lecturers.size(), students.size(), numberOfVacancies, numberOfCourses, users),
                                HttpStatus.OK);
                    } catch (Exception e) {
                        return new ResponseEntity<>(
                                new GetAllUserDTO(defaultErrorResponse, e.getMessage(), 0, 0, 0, 0, null),
                                HttpStatus.BAD_REQUEST);
                    }
                });
    }

    private RoleUpdateStrategy getRoleUpdateStrategy(UserUpdateDTO userUpdateDTO) {
        RoleUpdateStrategy strategy;
        if(userUpdateDTO.role.equals(UserRole.ADMIN.getValue())) {
            strategy = new AdminRoleUpdateStrategy(userRepository);
        }
        else if(userUpdateDTO.role.equals(UserRole.LECTURER.getValue())) {
            strategy = new LecturerRoleUpdateStrategy(userRepository);
        }
        else if(userUpdateDTO.role.equals(UserRole.STUDENT.getValue())) {
            strategy = new StudentRoleUpdateStrategy(userRepository);
        }
        else{
            throw new IllegalArgumentException("Role not found");
        }
        return strategy;
    }
}