package id.ac.ui.cs.advprog.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/** CATATAN !!!!
 * Data‑transfer object untuk operasi REST Mata Kuliah.
 *
 * • kode  : kode unik, maksimal 10 karakter (divalidasi @NotBlank)
 * • nama  : nama mata kuliah (tidak boleh kosong)
 * • sks   : jumlah SKS (>= 0)
 * • deskripsi : teks bebas, optional
 * • dosenPengampu : daftar nama / UUID dosen (optional saat create)
 */
public record MataKuliahDto(
        @NotBlank
        @Size(max = 10)
        String kode,

        @NotBlank
        String nama,

        @Min(1)
        int sks,

        String deskripsi,

        @NotNull
        List<UUID> dosenPengampu
) {}
