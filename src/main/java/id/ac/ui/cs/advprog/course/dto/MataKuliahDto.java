package id.ac.ui.cs.advprog.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

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
        @NotBlank String   kode,
        @NotBlank String   nama,
        @Min(0)   int      sks,
        String             deskripsi,
        @NotNull List<String> dosenPengampu
) {}
