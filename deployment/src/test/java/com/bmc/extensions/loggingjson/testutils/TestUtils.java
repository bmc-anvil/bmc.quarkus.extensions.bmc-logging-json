package com.bmc.extensions.loggingjson.testutils;

import java.lang.reflect.Field;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.core.JsonFormatter;

/**
 * Testing utilities.
 *
 * @author BareMetalCode
 */
public class TestUtils {

    private TestUtils() {}

    public static JsonConfig extractJsonConfig(JsonFormatter formatter) {

        try {
            Field configField = JsonFormatter.class.getDeclaredField("jsonConfig");
            configField.setAccessible(true);
            return (JsonConfig) configField.get(formatter);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not extract JsonConfig from JsonFormatter", e);
        }
    }

}
