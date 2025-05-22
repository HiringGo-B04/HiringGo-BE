package id.ac.ui.cs.advprog.mendaftarlowongan.repository;

import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LamaranRepository extends JpaRepository<Lamaran, UUID> {
    List<Lamaran> findByIdLowongan(UUID idLowongan);
}
