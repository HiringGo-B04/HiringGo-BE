

package id.ac.ui.cs.advprog.mendaftarlowongan.model;

import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@Entity
@Table(name = "lamaran")
public class Lamaran {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int sks;
    private float ipk;

    @Enumerated(EnumType.ORDINAL)
    private StatusLamaran status = StatusLamaran.MENUNGGU;

    @Column(name = "id_mahasiswa", nullable = false)
    private UUID idMahasiswa;

    @Column(name = "id_lowongan", nullable = false)
    private UUID idLowongan;

    private Lamaran(Builder builder) {
        this.id = UUID.randomUUID();
        this.sks = builder.sks;
        this.ipk = builder.ipk;
        this.status = builder.status;
        this.idMahasiswa = builder.idMahasiswa;
        this.idLowongan = builder.idLowongan;
    }

    public Lamaran() {}

    public static class Builder {
        private int sks;
        private float ipk;
        private StatusLamaran status = StatusLamaran.MENUNGGU;
        private UUID idMahasiswa;
        private UUID idLowongan;

        public Builder sks(int sks) {
            this.sks = sks;
            return this;
        }

        public Builder ipk(float ipk) {
            this.ipk = ipk;
            return this;
        }

        public Builder status(StatusLamaran status) {
            this.status = status;
            return this;
        }

        public Builder mahasiswa(UUID mahasiswa) {
            this.idMahasiswa = mahasiswa;
            return this;
        }

        public Builder lowongan(UUID lowongan) {
            this.idLowongan = lowongan;
            return this;
        }

        public Lamaran build() {
            return new Lamaran(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lamaran)) return false;
        Lamaran lamaran = (Lamaran) o;
        return id != null && id.equals(lamaran.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
