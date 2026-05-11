package model;

public enum Gender {
    MALE("Male"), FEMALE("Female");
    private String message;

    Gender(String message) {
        this.message = message;
    }

    public static Gender valueof(String gender) {
        return switch (gender) {
            case "Male" -> MALE;
            case "Female" -> FEMALE;
            default -> null;
        };
    }

    public String getMessage() {
        return message;
    }
}
