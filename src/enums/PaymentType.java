package enums;

public enum PaymentType {
    CASH("Cash"),
    CARD("Card"),
    INSURANCE("Insurance"),
    DIGITAL_WALLET("Digital Wallet"); // Java ismi: DIGITAL_WALLET, DB ismi: Digital Wallet

    private final String dbName;

    PaymentType(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public static PaymentType fromMessage(String text) {
        for (PaymentType b : PaymentType.values()) {
            if (b.dbName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return CASH;
    }
}