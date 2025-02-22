package com.bmc.extensions.loggingjson.deployment.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Dummy serializer to test client custom serializer loading via properties.
 *
 * @author BareMetalCode
 */
public class DummyTestSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(final Long value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {

        gen.writeString("Dummy Serializer Long value: " + value.toString());
    }

}
