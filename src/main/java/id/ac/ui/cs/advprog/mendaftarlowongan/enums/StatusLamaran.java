package id.ac.ui.cs.advprog.mendaftarlowongan.enums;
import lombok.Getter;

@Getter
public enum StatusLamaran {
    MENUNGGU(0),
    DITERIMA(1),
    DITOLAK(2);

    private final int value;

    private StatusLamaran(int value) {
        this.value = value;
    }
}