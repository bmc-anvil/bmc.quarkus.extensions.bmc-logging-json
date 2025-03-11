package com.bmc.extensions.loggingjson.runtime.infrastructure.serializers;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import lombok.Setter;

/**
 * Instant Serializer to inject to the mapper if so configured.
 *
 * @author BareMetalCode
 */
@Setter
public class InstantSerializer extends JsonSerializer<Instant> {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private ZoneId            zoneId            = ZoneId.systemDefault();

    @Override
    public void serialize(final Instant value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {

        final String formatted = ZonedDateTime.ofInstant(value, zoneId).format(dateTimeFormatter.withZone(zoneId));

        gen.writeString(formatted);
    }

}
