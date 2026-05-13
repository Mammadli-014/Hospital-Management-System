package model;

import enums.AppointmentType;

public enum Gender {
    MALE("M"), FEMALE("F");
    private String message;

    Gender(String message) {
        this.message = message;
    }

    public static Gender valueof(String gender) {
        return switch (gender) {
            case "M" -> MALE;
            case "F" -> FEMALE;
            default -> null;
        };
    }
    public String getMessage() {
        return message;
    }

    public static Gender fromMessage(String message) {
        for (Gender g : values()) {
            if (g.message.equalsIgnoreCase(message)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Unknown Gender Type: " + message);
    }
}
