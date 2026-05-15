package enums;

public enum AppointmentType{
    CALL("Call"),IN_PERSON("In Person"),ONLINE("Online");

    private String message;

    AppointmentType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static AppointmentType fromMessage(String input) {
        if (input == null || input.isBlank()) return null;

        for (AppointmentType at : values()) {
            if (at.message.equalsIgnoreCase(input)) {
                return at;
            }
            if (at.name().equalsIgnoreCase(input.replace(" ", "_"))) {
                return at;
            }
        }

        System.err.println("Warning: Unknown Appointment type found in DB: " + input);
        return IN_PERSON;
    }
}