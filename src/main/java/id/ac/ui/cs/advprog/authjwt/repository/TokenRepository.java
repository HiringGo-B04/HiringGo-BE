package id.ac.ui.cs.advprog.authjwt.repository;

import org.springframework.stereotype.Repository;
import id.ac.ui.cs.advprog.authjwt.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    Token findByToken(String token);
    void deleteByToken(String token);
    boolean existsByToken(String token);
}