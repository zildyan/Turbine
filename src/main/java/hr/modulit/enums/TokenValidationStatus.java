package hr.modulit.enums;

public enum TokenValidationStatus {
    INVALID_TOKEN("invalidToken"),
    EXPIRED_TOKEN("expiredToken"),
    VALID_TOKEN("validToken");

    private String description;

    TokenValidationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
