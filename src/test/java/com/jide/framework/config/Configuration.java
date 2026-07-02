/**
 * Represents the resolved runtime configuration for the test framework.
 *
 * This object is fully immutable and is constructed once by ConfigManager.
 *
 * Configuration sources (in order of precedence):
 *  1. JVM system properties (-D overrides)
 *  2. Environment variables (Docker / CI runtime)
 *  3. Environment-specific properties file (config-dev.properties, etc.)
 *  4. Default properties file (config.properties)
 *
 * Once created, this object cannot be modified.
 * This ensures:
 *  - Thread safety for parallel test execution
 *  - Predictable CI behaviour
 *  - No accidental mutation during test runs
 */
public final class Configuration {

    private final Environment environment;

    private final String jsonBaseUrl;
    private final String xmlBaseUrl;

    private final boolean logRequests;
    private final boolean logResponses;

    private final String suite;

    public Configuration(
            Environment environment,
            String jsonBaseUrl,
            String xmlBaseUrl,
            boolean logRequests,
            boolean logResponses,
            String suite
    ) {
        this.environment = environment;
        this.jsonBaseUrl = jsonBaseUrl;
        this.xmlBaseUrl = xmlBaseUrl;
        this.logRequests = logRequests;
        this.logResponses = logResponses;
        this.suite = suite;
    }

    // ─────────────────────────────────────────────
    // Getters only (NO setters → immutable)
    // ─────────────────────────────────────────────

    public Environment getEnvironment() {
        return environment;
    }

    public String getJsonBaseUrl() {
        return jsonBaseUrl;
    }

    public String getXmlBaseUrl() {
        return xmlBaseUrl;
    }

    public boolean isLogRequests() {
        return logRequests;
    }

    public boolean isLogResponses() {
        return logResponses;
    }

    public String getSuite() {
        return suite;
    }
}