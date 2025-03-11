package com.bmc.extensions.loggingjson.runtime.infrastructure.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.bmc.extensions.loggingjson.runtime.config.properties.ClientSerializerConfig;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import static com.bmc.extensions.loggingjson.runtime.infrastructure.serializers.InstantSerializerFactory.getInstantSerializer;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

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

    private DateTimeUtils() {}

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

    public static DateTimeFormatter getDateTimeFormatter(final String pattern) {

        return Optional.ofNullable(pattern)
                       .map(DateTimeFormatter::ofPattern)
                       .orElseGet(() -> ISO_LOCAL_DATE_TIME);
    }

    public static DateTimeFormatter getDateTimeFormatterWithZone(final String pattern, final JsonConfig jsonConfig) {

        return Optional.ofNullable(pattern)
                       .map(DateTimeFormatter::ofPattern)
                       .orElseGet(() -> ISO_OFFSET_DATE_TIME)
                       .withZone(getZoneId(jsonConfig));
    }

    public static ZoneId getZoneId(final JsonConfig jsonConfig) {

        return jsonConfig.logZoneId()
                         .map(ZoneId::of)
                         .orElseGet(ZoneId::systemDefault);
    }

}
