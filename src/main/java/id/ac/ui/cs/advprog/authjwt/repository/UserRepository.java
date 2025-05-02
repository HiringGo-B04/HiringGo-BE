package id.ac.ui.cs.advprog.authjwt.repository;

import org.springframework.stereotype.Repository;
import id.ac.ui.cs.advprog.authjwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String email);
    boolean existsByUsername(String email);
    boolean existsByNim(String nim);
    boolean existsByNip(String nip);
    void deleteByUsername(String email);
}