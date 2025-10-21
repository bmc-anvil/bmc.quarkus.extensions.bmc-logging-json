package com.bmc.extensions.loggingjson.runtime.config;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigGroup;

/**
 * Interface In charge of client application serialization configuration.
 * <p>
 * This differs with the Logger configurators as these client serializers affect the output of objects in the client application itself.
 * <p>
 * This approach allows for maximum flexibility when using the JSON logger.<br>
 * You can have the logs to produce a given timestamp output suited to your monitoring stack and a different timestamp format of your own application
 * objects suited for your own readability or parser needs.
 * <p>
 * You can add custom serializers to any of your own objects or anything your application will output and have it render your own way on the final
 * log.
 * <p>
 * Because date/time is a common overriding concern, I am adding here convenience serializers so you don't have to create and inject your own.<br>
 * I am not adding any default other than what Jackson provides, and you can override any of the ones below just with a {@link DateTimeFormatter}
 * string pattern
 *
 * @author BareMetalCode
 */
@ConfigGroup
public interface ClientSerializerConfig {

    /**
     * Custom JSON serializers for your own app data types.
     */
    @ConfigDocMapKey("serializer-name")
    Map<String, String> customSerializers();

    /**
     * Convenience formatter for client app {@link Instant} instances using {@link DateTimeFormatter} string patterns.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/time/format/DateTimeFormatter.html">DateTimeFormatter Patterns</a>
     */
    Optional<String> instantFormat();

    /**
     * Convenience formatter for client app {@link LocalDate} instances using {@link DateTimeFormatter} string patterns.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/time/format/DateTimeFormatter.html">DateTimeFormatter Patterns</a>
     */
    Optional<String> localDateFormat();

    /**
     * Convenience formatter for client app {@link LocalDateTime} instances using {@link DateTimeFormatter} string patterns.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/time/format/DateTimeFormatter.html">DateTimeFormatter Patterns</a>
     */
    Optional<String> localDateTimeFormat();

    /**
     * Convenience formatter for client app {@link LocalTime} instances using {@link DateTimeFormatter} string patterns.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/time/format/DateTimeFormatter.html">DateTimeFormatter Patterns</a>
     */
    Optional<String> localTimeFormat();

    /**
     * Convenience formatter for client app {@link ZonedDateTime} instances using {@link DateTimeFormatter} string patterns.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/time/format/DateTimeFormatter.html">DateTimeFormatter Patterns</a>
     */
    Optional<String> zonedDateTimeFormat();

}
