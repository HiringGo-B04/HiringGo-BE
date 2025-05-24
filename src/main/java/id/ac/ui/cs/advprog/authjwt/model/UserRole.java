package id.ac.ui.cs.advprog.authjwt.model;

public enum UserRole {
    ADMIN("ADMIN"),
    STUDENT("STUDENT"),
    LECTURER("LECTURER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
