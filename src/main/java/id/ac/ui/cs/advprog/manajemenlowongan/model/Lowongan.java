package id.ac.ui.cs.advprog.manajemenlowongan.model;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Lowongan {
    private UUID id;
    private String matkul;
    private int year;
    private String term;
    private int totalAsdosNeeded;
    private int totalAsdosRegistered;
    private int totalAsdosAccepted;

    private Lowongan() {
        // private constructor to force usage of builder
    }

    public static class Builder {
        private UUID id = UUID.randomUUID(); // default to new ID
        private String matkul;
        private int year;
        private String term;
        private int totalAsdosNeeded;
        private int totalAsdosRegistered = 0;
        private int totalAsdosAccepted = 0;

        public Builder matkul(String matkul) {
            this.matkul = matkul;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
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
            Lowongan lowongan = new Lowongan();
            lowongan.id = this.id;
            lowongan.matkul = this.matkul;
            lowongan.year = this.year;
            lowongan.term = this.term;
            lowongan.totalAsdosNeeded = this.totalAsdosNeeded;
            lowongan.totalAsdosRegistered = this.totalAsdosRegistered;
            lowongan.totalAsdosAccepted = this.totalAsdosAccepted;
            return lowongan;
        }
    }
}
