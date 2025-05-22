package id.ac.ui.cs.advprog.log.repository;

import id.ac.ui.cs.advprog.log.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {

    List<Log> findByIdLowongan(UUID idLowongan);

    List<Log> findByIdMahasiswa(UUID idMahasiswa);

    List<Log> findByIdDosen(UUID idDosen);

    List<Log> findByIdMahasiswaAndIdLowongan(UUID idMahasiswa, UUID idLowongan);

    @Query("""
    SELECT l FROM Log l
    WHERE l.idMahasiswa = :idMahasiswa
      AND l.idLowongan = :idLowongan
      AND l.status = :status
      AND FUNCTION('YEAR', l.tanggalLog) = :tahun
      AND FUNCTION('MONTH', l.tanggalLog) = :bulan
""")
    List<Log> findAcceptedLogsByMahasiswaAndLowonganAndMonth(
            @Param("idMahasiswa") UUID idMahasiswa,
            @Param("idLowongan") UUID idLowongan,
            @Param("status") id.ac.ui.cs.advprog.log.enums.StatusLog status,
            @Param("tahun") int tahun,
            @Param("bulan") int bulan
    );
    List<Log> findAcceptedLogsByMahasiswaAndLowonganAndMonth(
            @Param("idMahasiswa") UUID idMahasiswa,
            @Param("idLowongan") UUID idLowongan,
            @Param("tahun") int tahun,
            @Param("bulan") int bulan);
}