public enum Environment {
    DEV,
    QA,
    STAGING;

    public static Environment from(String value) {
        if (value == null || value.isBlank()) {
            return DEV;
        }

        return Environment.valueOf(value.trim().toUpperCase());
    }
}