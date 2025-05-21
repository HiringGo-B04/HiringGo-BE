package id.ac.ui.cs.advprog.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LowonganRepository extends JpaRepository<Lowongan, UUID> {
}