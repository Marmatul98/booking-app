package mmatula.bookingapp.enums;

public enum ERole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String value;

    ERole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
