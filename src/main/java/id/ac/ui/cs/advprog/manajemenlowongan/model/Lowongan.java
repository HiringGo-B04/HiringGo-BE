package id.ac.ui.cs.advprog.manajemenlowongan.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "lowongan")
public class Lowongan {
    @Id
    private UUID id;

    private String matkul;
    private int tahun;
    private String term;

    @Column(name = "total_asdos_needed", nullable = false)
    private int totalAsdosNeeded;

    @Column(name = "total_asdos_registered", nullable = false)
    private int totalAsdosRegistered;

    @Column(name = "total_asdos_accepted", nullable = false)
    private int totalAsdosAccepted;

    private Lowongan(Builder builder) {
        this.id = UUID.randomUUID();
        this.matkul = builder.matkul;
        this.tahun = builder.year;
        this.term = builder.term;
        this.totalAsdosNeeded = builder.totalAsdosNeeded;
        this.totalAsdosRegistered = builder.totalAsdosRegistered;
        this.totalAsdosAccepted = builder.totalAsdosAccepted;
    }

    public Lowongan() {

    }

    public static class Builder {
        private String matkul;
        private int tahun;
        private String term;
        private int totalAsdosNeeded;
        private int totalAsdosRegistered = 0;
        private int totalAsdosAccepted = 0;

        public Builder matkul(String matkul) {
            this.matkul = matkul;
            return this;
        }

        public Builder tahun(int tahun) {
            this.tahun = tahun;
            return this;
        }

        public Builder term(String term) {
            this.term = term;
            return this;
        }

        public Builder totalAsdosNeeded(int totalAsdosNeeded) {
            this.totalAsdosNeeded = totalAsdosNeeded;
            return this;
        }

        public Builder totalAsdosRegistered(int totalAsdosRegistered) {
            this.totalAsdosRegistered = totalAsdosRegistered;
            return this;
        }

        public Builder totalAsdosAccepted(int totalAsdosAccepted) {
            this.totalAsdosAccepted = totalAsdosAccepted;
            return this;
        }

        public Lowongan build() {
            return new Lowongan(this);
        }
    }
}


