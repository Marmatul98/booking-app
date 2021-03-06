package mmatula.bookingapp.enums;

public enum ERole {
    USER("USER"),
    ADMIN("ADMIN");

    private String value;

    ERole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
