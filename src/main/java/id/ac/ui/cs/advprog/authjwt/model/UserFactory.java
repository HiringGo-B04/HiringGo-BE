package id.ac.ui.cs.advprog.authjwt.model;

import java.util.UUID;

public class UserFactory {

    public static User createAdmin(UUID id, String email, String password) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(email);
        user.setPassword(password);
        user.setRole("ADMIN");
        return user;
    }

    public static User createStudent(UUID id, String email, String password, String fullName, String nim) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(email);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setRole("STUDENT");
        user.setNim(nim);
        return user;
    }

    public static User createLecturer(UUID id, String email, String password, String fullName, String nip) {
        User user = new User();
        user.setUserId(id);
        user.setUsername(email);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setRole("LECTURER");
        user.setNip(nip);
        return user;
    }
}
