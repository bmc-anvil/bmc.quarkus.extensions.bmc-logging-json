package com.bmc.extensions.loggingjson.runtime.config.properties;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.bmc.extensions.loggingjson.runtime.models.enums.LogFormat;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

import org.jboss.logmanager.formatters.StructuredFormatter;

/**
 * Configuration class for structuring JSON output.
 *
 * @author BareMetalCode
 */
@ConfigGroup
public class JsonConfig {

    /**
     * Additional fields to be appended in the JSON logs and will appear at the top level of the logs.
     */
    @ConfigItem
    @ConfigDocMapKey("field-name")
    public Map<String, String> additionalFieldsTop;

    /**
     * Additional fields to be appended in the JSON logs and will appear wrapped under the "additionalFields" key.
     */
    @ConfigItem
    @ConfigDocMapKey("field-name")
    public Map<String, String> additionalFieldsWrapped;

    /**
     * Custom Serializers that will apply to the client application and not to the log itself
     * These serializers will target the rendering inside the message field
     */
    @ConfigItem
    public ClientSerializerConfig clientSerializers;

    /**
     * Determine whether to enable the JSON console formatting extension, which disables "normal" console formatting.
     */
    @ConfigItem(defaultValue = "true")
    public boolean enable;

    /**
     * The exception output type to specify.
     */
    @ConfigItem(defaultValue = "detailed")
    public StructuredFormatter.ExceptionOutputType exceptionOutputType;

    /**
     * Keys to be excluded from the JSON output.
     */
    @ConfigItem
    public Optional<Set<String>> excludedKeys;

    /**
     * Override keys with custom values. Omitting this value indicates that no key overrides will be applied.
     */
    @ConfigItem
    public Map<String, String> keyOverrides;

    /**
     * The date format to use on the log record output.
     * <p>
     * Defaults to {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME} format: YYYY-MM-dd'T'HH:mm:ss<br>
     * example: '2011-12-03T10:15:30+01:00'.
     * <p>
     * You can customize the date-time format of your own objects by adding a serializer for a {@link Temporal} type data
     * by adding them in the customSerializers configuration
     *
     * @see
     * <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/time/format/DateTimeFormatter.html">DateTimeFormatter Patterns</a>
     */
    @ConfigItem
    public Optional<String> logDateTimeFormat;

    /**
     * Specify the format of the produced JSON
     */
    @ConfigItem(defaultValue = "DEFAULT")
    public LogFormat logFormat;

    /**
     * The zone ID to use in an accepted ZoneId format.
     * <p>
     * Default to the system's zone id.<br>
     * Mind that the system is where the JVM is running by calling ZoneId::systemDefault
     */
    @ConfigItem
    public Optional<String> logZoneId;

    /**
     * Enable "pretty printing" of the JSON record. Note that some JSON parsers will fail to read the pretty printed output.
     */
    @ConfigItem(defaultValue = "false")
    public boolean prettyPrint;

    /**
     * Enable printing of more details in the log.
     * <p>
     * Printing the details can be expensive as the values are retrieved from the caller. The details include the
     * source class name, source file name, source method name, and source line number.
     */
    @ConfigItem(defaultValue = "false")
    public Boolean printDetails;

    /**
     * The special end-of-record delimiter to be used.
     * <p>
     * By default, a system-dependent newline is used and appended after each record in addition to any custom delimiter
     *
     * @see System#lineSeparator()
     */
    @ConfigItem
    public Optional<String> recordDelimiter;

}
