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

    public static AppointmentType fromMessage(String message) {
        for (AppointmentType at : values()) {
            if (at.message.equalsIgnoreCase(message)) {
                return at;
            }
        }
        throw new IllegalArgumentException("Unknown Appointment type: " + message);
    }
}