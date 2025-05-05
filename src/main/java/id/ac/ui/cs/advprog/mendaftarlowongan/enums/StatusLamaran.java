package id.ac.ui.cs.advprog.mendaftarlowongan.enums;
import lombok.Getter;

@Getter
public enum StatusLamaran {
    MENUNGGU("MENUNGGU"),
    DITERIMA("DITERIMA"),
    DITOLAK("DITOLAK");

    private final String value;

    private StatusLamaran(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (StatusLamaran orderStatus : StatusLamaran.values()) {
            if (orderStatus.name().equals(param)) {
                return true;
            }
        }
        return false;
    }
}