package com.bmc.extensions.loggingjson.runtime.infrastructure.serializers;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.bmc.extensions.loggingjson.runtime.infrastructure.utils.DateTimeUtils;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import lombok.Setter;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
@Setter
public class InstantSerializer extends JsonSerializer<Instant> {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private ZoneId            zoneId            = ZoneId.systemDefault();

    public static InstantSerializer getSerializer(final String pattern, final JsonConfig jsonConfig) {
        final DateTimeFormatter formatter             = DateTimeUtils.getDateTimeFormatterWithZone(pattern, jsonConfig);
        final InstantSerializer initializedSerializer = new InstantSerializer();

        jsonConfig.logZoneId.ifPresent(zoneId -> initializedSerializer.setZoneId(ZoneId.of(zoneId)));
        initializedSerializer.setDateTimeFormatter(formatter);

        return initializedSerializer;
    }

    @Override
    public void serialize(final Instant value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        final String formatted = ZonedDateTime.ofInstant(value, zoneId).format(dateTimeFormatter.withZone(zoneId));

        gen.writeString(formatted);
    }

}
