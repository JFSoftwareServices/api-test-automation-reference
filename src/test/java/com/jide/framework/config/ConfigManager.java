package com.jide.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigManager is the central configuration resolver for the test framework.
 *
 * It supports layered configuration loading:
 *
 *  1. Base configuration (config.properties)
 *  2. Environment-specific configuration (config-{env}.properties)
 *  3. Environment variables (Docker / CI runtime)
 *  4. JVM system properties (-D overrides)
 *
 * Resolution priority (highest → lowest):
 *  JVM system properties
 *  Environment variables
 *  Environment-specific properties file
 *  Base properties file
 *
 * This ensures:
 *  - deterministic behaviour across CI and local runs
 *  - no hardcoded environment logic in tests
 *  - full override flexibility for CI/CD pipelines
 */
public final class ConfigManager {

    private static final Properties PROPERTIES = new Properties();

    private static final String BASE_FILE = "config.properties";
    private static final String ENV_FILE_PREFIX = "config-";
    private static final String ENV_FILE_SUFFIX = ".properties";

    static {
        loadBaseConfig();
        loadEnvironmentConfig();
        applyRuntimeOverrides();
    }

    private ConfigManager() {}

    private static void loadBaseConfig() {
        try (InputStream is = getResource(BASE_FILE)) {
            if (is == null) {
                throw new IllegalStateException("Missing " + BASE_FILE);
            }
            PROPERTIES.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + BASE_FILE, e);
        }
    }

    private static void loadEnvironmentConfig() {
        String env = getEnvName();

        String fileName = ENV_FILE_PREFIX + env + ENV_FILE_SUFFIX;

        try (InputStream is = getResource(fileName)) {
            if (is == null) {
                // optional environment file (not required)
                return;
            }
            PROPERTIES.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + fileName, e);
        }
    }

    private static void applyRuntimeOverrides() {
        // JVM overrides
        for (String key : PROPERTIES.stringPropertyNames()) {
            String sys = System.getProperty(key);
            if (sys != null) {
                PROPERTIES.setProperty(key, sys);
            }
        }

        // Environment variable overrides
        for (String key : PROPERTIES.stringPropertyNames()) {
            String envKey = key.toUpperCase().replace(".", "_");
            String envVal = System.getenv(envKey);

            if (envVal != null) {
                PROPERTIES.setProperty(key, envVal);
            }
        }
    }

    public static String get(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }

        return value;
    }

    public static String get(String key, String defaultValue) {
        String value = PROPERTIES.getProperty(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key, String.valueOf(defaultValue));
        return value.equalsIgnoreCase("true") || value.equals("1");
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getJsonBaseUrl() {
        return get("base.url.json");
    }

    public static String getXmlBaseUrl() {
        return get("base.url.xml");
    }

    public static boolean isLogRequestsEnabled() {
        return getBoolean("log.requests", false);
    }

    public static boolean isLogResponsesEnabled() {
        return getBoolean("log.responses", false);
    }

    public static String getSuite() {
        return get("suite", "testng.xml");
    }

    private static String getEnvName() {
        String env = System.getProperty("env");

        if (env != null && !env.isBlank()) {
            return env.trim().toLowerCase();
        }

        env = System.getenv("ENV");

        if (env != null && !env.isBlank()) {
            return env.trim().toLowerCase();
        }

        return "dev";
    }

    private static InputStream getResource(String file) {
        return ConfigManager.class
                .getClassLoader()
                .getResourceAsStream(file);
    }
}