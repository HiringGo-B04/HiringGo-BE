package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;


@Repository
@Profile({"!test","jpa-test"})
public interface JpaMataKuliahRepository
        extends JpaRepository<MataKuliah, String>,
        MataKuliahRepository {

    @Override
    default void deleteByKode(@NonNull String kode) {
        deleteById(kode);
    }
}

