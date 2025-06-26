package com.bmc.extensions.loggingjson.runtime.infrastructure.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

import com.bmc.extensions.loggingjson.runtime.config.ClientSerializerConfig;
import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import static com.bmc.extensions.loggingjson.runtime.infrastructure.serializers.InstantSerializerFactory.getInstantSerializer;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.Optional.ofNullable;

/**
 * Utility class providing methods to configure and manage date-time serialization and formatting.
 * <p>
 * It leverages the Jackson library's {@link JavaTimeModule} for default serialization of Java date-time objects to JSON,
 * while allowing users to override the defaults and define their own patterns and configurations through the provided {@link JsonConfig} object.
 * <p>
 * Methods are straightforward, so there is no need for further documentation.<br>
 * There is no try/catch for possible incorrect formatting on purpose, so we fail fast if an incorrect serialization pattern is configured.
 * <p>
 * The {@link DateTimeUtils#configureClientDateTime(JsonConfig)} method serves as a convenience utility for configuring common date-time classes used
 * on applications adding the functionality here instead of making client apps implement a serializer themselves.<br>
 * This approach can be used for other commonly used classes based on feedback, making the extension more flexible and convenient over time.
 *
 * @author BareMetalCode
 */
public class DateTimeUtils {

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMAT = ISO_LOCAL_DATE_TIME;

    private DateTimeUtils() {}

    /**
     * Configures a {@link JavaTimeModule} instance with custom serializers for various date-time types
     * such as {@link LocalDateTime}, {@link LocalDate}, {@link LocalTime}, {@link ZonedDateTime}, and {@link Instant}.
     * The serializers are customized using the date-time formatting patterns provided within the given {@link JsonConfig}.
     *
     * @param jsonConfig the {@link JsonConfig} instance containing the client serializer configurations
     *                   such as date-time formatting patterns for different date or time types.
     *
     * @return a configured {@link JavaTimeModule} instance that includes custom serializers for the specified date-time types.
     */
    public static JavaTimeModule configureClientDateTime(final JsonConfig jsonConfig) {

        final JavaTimeModule         javaTimeModule    = new JavaTimeModule();
        final ClientSerializerConfig clientSerializers = jsonConfig.clientSerializers();

        clientSerializers.localDateTimeFormat()
                         .ifPresent(pattern -> javaTimeModule.addSerializer(LocalDateTime.class,
                                                                            new LocalDateTimeSerializer(getDateTimeFormatter(pattern))));

        clientSerializers.localDateFormat()
                         .ifPresent(pattern -> javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(getDateTimeFormatter(pattern))));

        clientSerializers.localTimeFormat()
                         .ifPresent(pattern -> javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(getDateTimeFormatter(pattern))));

        clientSerializers.localDateTimeFormat()
                         .ifPresent(pattern -> javaTimeModule.addSerializer(LocalDateTime.class,
                                                                            new LocalDateTimeSerializer(getDateTimeFormatter(pattern))));

        clientSerializers.zonedDateTimeFormat()
                         .ifPresent(pattern -> javaTimeModule
                                 .addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(getDateTimeFormatterWithZone(pattern, jsonConfig))));

        clientSerializers.instantFormat()
                         .ifPresent(pattern -> javaTimeModule.addSerializer(Instant.class, getInstantSerializer(pattern, jsonConfig)));

        return javaTimeModule;
    }

    /**
     * Creates a {@link DateTimeFormatter} based on the provided date-time formatting pattern.
     * <p>
     * If the pattern is null, it defaults to {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @param pattern the date-time formatting pattern to use for creating the {@link DateTimeFormatter}.
     *                If null, {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME} will be used.
     *
     * @return a {@link DateTimeFormatter} created using the specified pattern, or the default {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     */
    public static DateTimeFormatter getDateTimeFormatter(final String pattern) {

        return ofNullable(pattern)
                .map(java.time.format.DateTimeFormatter::ofPattern)
                .orElse(DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * Creates a {@link DateTimeFormatter} with a time zone applied from the specified {@link JsonConfig}.
     * <p>
     * If no pattern is provided (null), it defaults to {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}.
     * The resulting formatter includes a time zone derived using the {@link JsonConfig} instance via {@link #getZoneId(JsonConfig)}.
     *
     * @param pattern    the date-time formatting pattern to use for creating the {@link DateTimeFormatter}.
     *                   If null, the default {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME} will be used.
     * @param jsonConfig the {@link JsonConfig} instance that provides the time zone configuration.
     *                   The time zone is resolved using {@link #getZoneId(JsonConfig)}.
     *
     * @return a {@link DateTimeFormatter} configured with the specified pattern and time zone.
     */
    public static DateTimeFormatter getDateTimeFormatterWithZone(final String pattern, final JsonConfig jsonConfig) {

        return ofNullable(pattern)
                .map(java.time.format.DateTimeFormatter::ofPattern)
                .orElse(DEFAULT_DATE_TIME_FORMAT)
                .withZone(getZoneId(jsonConfig));
    }

    /**
     * Retrieves the {@link ZoneId} based on the provided {@link JsonConfig}.
     * <p>
     * If the {@code logZoneId} is defined in the configuration, it converts it to a valid {@link ZoneId}.<br>
     * If not, it defaults to the system's default {@link ZoneId}.
     *
     * @param jsonConfig the {@link JsonConfig} instance containing the configuration for the zone ID.
     *                   If the zone ID is not specified, {@link ZoneId#systemDefault()} is used as the fallback.
     *
     * @return the resolved {@link ZoneId}, either from the configuration or the system default.
     */
    public static ZoneId getZoneId(final JsonConfig jsonConfig) {

        return jsonConfig.logZoneId()
                         .map(ZoneId::of)
                         .orElseGet(ZoneId::systemDefault);
    }

}
