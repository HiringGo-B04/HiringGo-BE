package id.ac.ui.cs.advprog.course.model;

import id.ac.ui.cs.advprog.authjwt.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/** CATATAN
 * Entity Mata Kuliah – disimpan pada tabel "mata_kuliah".
 * •  kode  : primary‑key (varchar 10, unik)
 * •  nama  : nama mata kuliah
 * •  deskripsi : teks panjang (optional)
 * •  sks   : jumlah SKS (>= 1)
 * •  dosenPengampu : many‑to‑many ke User (role LECTURER)
 */
@Entity
@Table(name = "mata_kuliah")
@Getter
@Setter
@NoArgsConstructor                        // wajib untuk JPA
@AllArgsConstructor
@EqualsAndHashCode(of = "kode")
public class MataKuliah {

    /* ---------- Kolom dasar ---------- */

    @Id
    @Column(length = 10, nullable = false, unique = true)
    private String kode;

    @Column(nullable = false, length = 255)
    private String nama;

    @Lob
    private String deskripsi;

    @Column(nullable = false)
    private int sks;

    /* ---------- Relasi dosen ---------- */

    @ManyToMany
    @JoinTable(
            name = "mata_kuliah_lecturer",
            joinColumns = @JoinColumn(name = "matkul_kode", referencedColumnName = "kode"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId")
    )
    private Set<User> dosenPengampu = new HashSet<>();

    /* ---------- Konstruktor ringkas ---------- */
    public MataKuliah(String kode, String nama, String deskripsi, int sks) {
        this.kode = kode;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.sks = sks;
    }

    /* ---------- Helper ---------- */
    public void addDosenPengampu(User dosen) {
        this.dosenPengampu.add(dosen);
    }
}
