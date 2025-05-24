package id.ac.ui.cs.advprog.log.model;

import id.ac.ui.cs.advprog.log.enums.KategoriLog;
import id.ac.ui.cs.advprog.log.enums.StatusLog;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "log")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String judul;

    @Column(nullable = false)
    private String keterangan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KategoriLog kategori;

    @Column(name = "tanggal_log", nullable = false)
    private LocalDate tanggalLog;

    @Column(name = "waktu_mulai", nullable = false)
    private LocalTime waktuMulai;

    @Column(name = "waktu_selesai", nullable = false)
    private LocalTime waktuSelesai;

    @Column(name = "pesan_untuk_dosen")
    private String pesanUntukDosen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusLog status = StatusLog.MENUNGGU;

    @Column(name = "id_lowongan", nullable = false)
    private UUID idLowongan;

    @Column(name = "id_mahasiswa", nullable = false)
    private UUID idMahasiswa;

    @Column(name = "id_dosen", nullable = false)
    private UUID idDosen;

    public Log() {
    }

    Log(LogBuilder builder) {
        if (builder.getId() != null) {
            this.id = builder.getId();
        }

        this.judul = builder.getJudul();
        this.keterangan = builder.getKeterangan();
        this.kategori = builder.getKategori();
        this.tanggalLog = builder.getTanggalLog();
        this.waktuMulai = builder.getWaktuMulai();
        this.waktuSelesai = builder.getWaktuSelesai();
        this.pesanUntukDosen = builder.getPesanUntukDosen();
        this.status = builder.getStatus();
        this.idLowongan = builder.getIdLowongan();
        this.idMahasiswa = builder.getIdMahasiswa();
        this.idDosen = builder.getIdDosen();
    }
}