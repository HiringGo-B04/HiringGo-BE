package id.ac.ui.cs.advprog.manajemenlowongan.repository;

import id.ac.ui.cs.advprog.manajemenlowongan.model.Lowongan;
import id.ac.ui.cs.advprog.mendaftarlowongan.model.Lamaran;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.UUID;

@Repository
public interface LowonganRepository extends JpaRepository<Lowongan, UUID> {
}