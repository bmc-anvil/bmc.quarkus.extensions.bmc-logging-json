package com.bmc.extensions.loggingjson.runtime.infrastructure.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.infrastructure.serializers.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
public class DateTimeUtils {

    private DateTimeUtils() {}

    public static JavaTimeModule configureClientDateTime(final JsonConfig jsonConfig) {
        final JavaTimeModule javaTimeModule = new JavaTimeModule();

        jsonConfig.clientSerializers.localDateTimeFormat
                .ifPresent(pattern -> javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(getDateTimeFormatter(pattern))));

        jsonConfig.clientSerializers.localDateFormat
                .ifPresent(pattern -> javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(getDateTimeFormatter(pattern))));

        jsonConfig.clientSerializers.localTimeFormat
                .ifPresent(pattern -> javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(getDateTimeFormatter(pattern))));

        jsonConfig.clientSerializers.localDateTimeFormat
                .ifPresent(pattern -> javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(getDateTimeFormatter(pattern))));

        jsonConfig.clientSerializers.zonedDateTimeFormat
                .ifPresent(pattern -> javaTimeModule
                        .addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(getDateTimeFormatterWithZone(pattern, jsonConfig))));

        jsonConfig.clientSerializers.instantFormat.ifPresent(
                pattern -> javaTimeModule.addSerializer(Instant.class, InstantSerializer.getSerializer(pattern, jsonConfig)));

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
        return jsonConfig.logZoneId
                .map(ZoneId::of)
                .orElseGet(ZoneId::systemDefault);
    }

}
