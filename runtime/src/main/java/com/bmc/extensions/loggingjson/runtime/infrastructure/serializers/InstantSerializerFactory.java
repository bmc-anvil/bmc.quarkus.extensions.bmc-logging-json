package com.bmc.extensions.loggingjson.runtime.infrastructure.serializers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;

import static com.bmc.extensions.loggingjson.runtime.infrastructure.utils.DateTimeUtils.getDateTimeFormatterWithZone;

/**
 * Factory to create an {@link InstantSerializer} from a custom pattern and ZoneId.
 *
 * @author BareMetalCode
 */
public class InstantSerializerFactory {

    private InstantSerializerFactory() {}

    /**
     * Creates and configures an {@code InstantSerializer} using the provided date-time pattern and {@link JsonConfig}.
     *
     * @param pattern    the date-time pattern to format {@link Instant} values. If null or invalid, a default format will be used.
     * @param jsonConfig the {@link JsonConfig} object containing configuration options for date-time formatting and zone ID.
     *                   It can influence the zone ID used in the serializer and the formatting behavior.
     *
     * @return a configured instance of {@link InstantSerializer}.
     */
    public static InstantSerializer getInstantSerializer(final String pattern, final JsonConfig jsonConfig) {

        final DateTimeFormatter formatter             = getDateTimeFormatterWithZone(pattern, jsonConfig);
        final InstantSerializer initializedSerializer = new InstantSerializer();

        jsonConfig.logZoneId().ifPresent(zoneId -> initializedSerializer.setZoneId(ZoneId.of(zoneId)));
        initializedSerializer.setDateTimeFormatter(formatter);

        return initializedSerializer;
    }

}
