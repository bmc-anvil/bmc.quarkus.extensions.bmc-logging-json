package com.bmc.extensions.loggingjson.runtime.config;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.bmc.extensions.loggingjson.runtime.models.enums.LogFormat;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

/**
 * Configuration interface for structuring JSON output.
 *
 * @author BareMetalCode
 */
@ConfigGroup
public interface JsonConfig {

    /**
     * Additional fields to be appended in the JSON logs and will appear at the top level of the logs.
     */
    @ConfigDocMapKey("top-field-name")
    Map<String, String> additionalFieldsTop();

    /**
     * Additional fields to be appended in the JSON logs and will appear wrapped under the "additionalFields" key.
     */
    @ConfigDocMapKey("wrapped-field-name")
    Map<String, String> additionalFieldsWrapped();

    /**
     * Custom Serializers that will apply to the client application and not to the log itself.
     * These serializers will target the rendering inside the message field
     */
    ClientSerializerConfig clientSerializers();

    /**
     * Determine whether to enable the JSON console formatting extension, which disables "normal" console formatting.
     */
    @WithDefault("true")
    boolean enable();

    /**
     * Configuration for handling Exceptions output.
     */
    ExceptionConfig exceptions();

    /**
     * Keys to be excluded from the JSON output.
     */
    Optional<Set<String>> excludedKeys();

    /**
     * Override keys with custom values. Omitting this value indicates that no key overrides will be applied.
     */
    Map<String, String> keyOverrides();

    /**
     * The date format to use on the log record output.
     * <p>
     * Defaults to {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME} format: {@code YYYY-MM-dd'T'HH:mm:ss}<br>
     * example: '2011-12-03T10:15:30+01:00'.
     * <p>
     * You can customize the date-time format of your own objects by adding a serializer for a {@link Temporal} type data
     * by adding them in the customSerializers configuration
     *
     * @see
     * <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/time/format/DateTimeFormatter.html">DateTimeFormatter Patterns</a>
     */
    Optional<String> logDateTimeFormat();

    /**
     * Specify the format of the produced JSON.
     */
    @WithDefault("DEFAULT")
    LogFormat logFormat();

    /**
     * The zone ID to use in an accepted ZoneId format.
     * <p>
     * Default to the system's zone id.<br>
     * Mind that the system is where the JVM is running by calling ZoneId::systemDefault
     */

    Optional<String> logZoneId();

    /**
     * Enable "pretty printing" of the JSON record. Note that some JSON parsers will fail to read the pretty printed output.
     */
    @WithDefault("false")
    boolean prettyPrint();

    /**
     * Prints the classic Java-Style {@link StackTraceElement} array.
     */
    @WithDefault("false")
    boolean printClassicStackTrace();

    /**
     * Enable printing of more details in the log.
     * <p>
     * Printing the details can be expensive as the values are retrieved from the caller. The details include the
     * source class name, source file name, source method name, and source line number.
     */
    @WithDefault("false")
    boolean printDetails();

    /**
     * The special end-of-record delimiter to be used.
     * <p>
     * By default, a system-dependent newline is used and appended after each record in addition to any custom delimiter
     *
     * @see System#lineSeparator()
     */
    Optional<String> recordDelimiter();

}
