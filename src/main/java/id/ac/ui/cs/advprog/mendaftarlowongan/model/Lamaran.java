

package id.ac.ui.cs.advprog.mendaftarlowongan.model;

import id.ac.ui.cs.advprog.mendaftarlowongan.enums.StatusLamaran;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class Lamaran {
    private UUID id;
    private int sks;
    private float ipk;
    private StatusLamaran status = StatusLamaran.MENUNGGU;
    private UUID idMahasiswa;
    private UUID idLowongan;

    private Lamaran(Builder builder) {
        this.id = UUID.randomUUID();
        this.sks = builder.sks;
        this.ipk = builder.ipk;
        this.status = builder.status;
        this.idMahasiswa = builder.idMahasiswa;
        this.idLowongan = builder.idLowongan;
    }

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
}
