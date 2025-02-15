package com.bmc.extensions.loggingjson.runtime.infrastructure.serializers;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.infrastructure.utils.DateTimeUtils;

/**
 * Factory to create an {@link InstantSerializer} from a custom pattern and ZoneId.
 *
 * @author BareMetalCode
 */
public class InstantSerializerFactory {

    private InstantSerializerFactory() {}

    public static InstantSerializer getInstantSerializer(final String pattern, final JsonConfig jsonConfig) {
        final DateTimeFormatter formatter             = DateTimeUtils.getDateTimeFormatterWithZone(pattern, jsonConfig);
        final InstantSerializer initializedSerializer = new InstantSerializer();

        jsonConfig.logZoneId.ifPresent(zoneId -> initializedSerializer.setZoneId(ZoneId.of(zoneId)));
        initializedSerializer.setDateTimeFormatter(formatter);

        return initializedSerializer;
    }

}
