package id.ac.ui.cs.advprog.course.repository;

import id.ac.ui.cs.advprog.course.model.MataKuliah;

import java.util.List;
import java.util.Optional;

//  ide saya untuk menggunakan DB in memory menggunakan MAP sehingga saat nanti saat benar benar menggunakan DB tinggal diubah
// Untuk sementara agar tidak mempersulit diri masih menggunakan ini
public interface MataKuliahRepository {

    /**
     * Menyimpan mata kuliah baru ke 'database' (in-memory atau lainnya).
     * @param mk objek mata kuliah yang akan disimpan
     * @throws RuntimeException jika kode mata kuliah sudah ada
     */
    void save(MataKuliah mk);

    /**
     * Mendapatkan semua mata kuliah yang tersimpan.
     * @return list mata kuliah
     */
    List<MataKuliah> findAll();

    /**
     * Mencari mata kuliah berdasarkan kode unik.
     * @param kode kode mata kuliah
     * @return Optional mata kuliah (empty jika tidak ditemukan)
     */
    Optional<MataKuliah> findByKode(String kode);

    /**
     * Memperbarui data mata kuliah yang sudah ada.
     * @param mk objek mata kuliah dengan data baru
     * @throws RuntimeException jika data belum ada di penyimpanan
     */
    void update(MataKuliah mk);

    /**
     * Menghapus data mata kuliah berdasarkan kode.
     * @param kode kode mata kuliah
     */
    void deleteByKode(String kode);
}
