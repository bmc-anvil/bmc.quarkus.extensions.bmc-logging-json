package com.bmc.extensions.loggingjson.runtime.models.factory;

import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.bmc.extensions.loggingjson.runtime.infrastructure.utils.DateTimeUtils.configureClientDateTime;
import static com.bmc.extensions.loggingjson.runtime.infrastructure.utils.SerializerUtils.addCustomSerializersIfAny;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * Factory for creating instances of Jackson's {@link JsonFactory}.
 * <p>
 * The class is designed to configure an {@link ObjectMapper} with the following settings:<br>
 * - Disables writing dates as timestamps.<br>
 * - Includes ONLY non-null fields in the JSON output.<br>
 * - Registers a custom module to handle Java Time types on the client side.<br>
 * (The Log's own time format is not controlled here)
 * <p>
 * Additionally, custom serializers can be dynamically added to the {@link ObjectMapper} if they are specified
 * within the provided {@link JsonConfig}.
 *
 * @author BareMetalCode
 */
public class JacksonMapperFactory {

    private JacksonMapperFactory() {}

    /**
     * Creates and configures a Jackson {@link JsonFactory} instance based on the provided {@link JsonConfig}.
     * <p>
     * The configuration includes disabling the use of timestamps for dates, setting non-null serialization inclusion,
     * registering custom modules for Java Time types, and adding any custom serializers defined in the configuration.
     *
     * @param jsonConfig the configuration for structuring JSON output, including options for custom serializers,
     *                   date-time formats, etc.
     *
     * @return a configured {@link JsonFactory} ready to produce JSON output with the specified settings.
     */
    public static JsonFactory getJacksonJSONFactory(final JsonConfig jsonConfig) {

        final ObjectMapper mapper = new ObjectMapper();

        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(NON_NULL);
        mapper.registerModule(configureClientDateTime(jsonConfig));

        addCustomSerializersIfAny(jsonConfig, mapper);

        return mapper.getFactory();
    }

}
