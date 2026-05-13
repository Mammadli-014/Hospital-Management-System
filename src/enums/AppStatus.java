package enums;

public enum AppStatus {
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    SCHEDULED("Scheduled"),
    NO_SHOW("No-Show");
    private String message;

    AppStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static AppStatus fromMessage(String message) {
        for (AppStatus as : values()) {
            if (as.message.equalsIgnoreCase(message)) {
                return as;
            }
        }
        throw new IllegalArgumentException("Unknown Appointment Status: " + message);
    }
}