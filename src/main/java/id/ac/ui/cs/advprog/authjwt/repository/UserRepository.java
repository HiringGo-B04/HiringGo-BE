package id.ac.ui.cs.advprog.authjwt.repository;

import org.springframework.stereotype.Repository;
import id.ac.ui.cs.advprog.authjwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String email);
    User findByUserId(UUID userId);
    boolean existsByUsername(String email);
    boolean existsByNim(String nim);
    boolean existsByNip(String nip);
    void deleteByUsername(String email);
    List<User> findAllByRole(String role);
}