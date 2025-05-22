package id.ac.ui.cs.advprog.course.model;

import id.ac.ui.cs.advprog.authjwt.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "mata_kuliah")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "kode")
public class MataKuliah {

    /* ---------- Kolom dasar ---------- */

    @Id
    @Size(max = 10)
    @Column(length = 10, nullable = false, unique = true)
    private String kode;

    @Column(nullable = false)
    private String nama;

    @Lob
    private String deskripsi;

    @Min(1)
    @Column(nullable = false)
    private int sks;

    /* ---------- Relasi dosen ---------- */

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mata_kuliah_lecturer",
            joinColumns = @JoinColumn(name = "matkul_kode", referencedColumnName = "kode"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId")
    )
    private Set<User> dosenPengampu = new HashSet<>();

    /* ---------- Konstruktor ringkas ---------- */
    public MataKuliah(String kode, String nama, String deskripsi, int sks) {
        this.kode       = kode;
        this.nama       = nama;
        this.deskripsi  = deskripsi;
        this.sks        = sks;
        this.dosenPengampu = new HashSet<>();
    }

    /* ---------- Helper ---------- */
    public void addDosenPengampu(User dosen) {
        if (this.dosenPengampu == null) {
            this.dosenPengampu = new HashSet<>();
        }
        this.dosenPengampu.add(dosen);
    }

    public void removeDosenPengampu(UUID userId) {
        if (this.dosenPengampu != null) {
            this.dosenPengampu.removeIf(d -> d.getUserId().equals(userId));
        }
    }
}