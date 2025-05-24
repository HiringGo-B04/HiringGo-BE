package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaMataKuliahRepository
        extends JpaRepository<MataKuliah, String>,
        MataKuliahRepository {

    @Override
    default MataKuliah addMataKuliah(MataKuliah mk) {
        return save(mk);
    }
    @Override
    default Optional<MataKuliah> findByKode(String kode) {
        return findById(kode);
    }
    @Override
    default void deleteByKode(@NonNull String kode) {
        deleteById(kode);
    }
}