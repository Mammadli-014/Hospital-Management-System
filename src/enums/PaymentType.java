package enums;

public enum PaymentType {
    INSURANCE("Insurance"), CARD("Card"), CASH("Cash"), DIGITAL_WALLET("Digital Wallet");
    private String message;

    PaymentType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static PaymentType fromMessage(String message) {
        for (PaymentType pt : values()) {
            if (pt.message.equalsIgnoreCase(message)) {
                return pt;
            }
        }
        throw new IllegalArgumentException("Unknown Payment Type: " + message);
    }
}
