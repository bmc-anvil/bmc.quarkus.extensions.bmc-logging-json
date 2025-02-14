package com.bmc.extensions.loggingjson.runtime;

import java.util.Optional;
import java.util.logging.Formatter;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonOutputConfig;
import com.bmc.extensions.loggingjson.runtime.core.JsonFormatter;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogOutput;
import com.fasterxml.jackson.core.JsonFactory;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

import static com.bmc.extensions.loggingjson.runtime.models.factory.JacksonMapperFactory.getJacksonJSONFactory;
import static com.bmc.extensions.loggingjson.runtime.models.factory.StructuredLogFactory.getPrecomputedStructuredLog;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Quarkus {@link Recorder} handling the configuration and initialization of JSON formatters.
 * <p>
 * This class is the entry point to generate everything required to create a structured JSON formatter for logging purposes.
 */
@Recorder
public class LoggingJsonRecorder {

    public RuntimeValue<Optional<Formatter>> getJsonFormatterForLogOutputType(final JsonOutputConfig jsonOutputConfig, final LogOutput logOutput) {
        return switch (logOutput) {
            case FILE -> createJsonFormater(jsonOutputConfig.fileJson);
            case CONSOLE -> createJsonFormater(jsonOutputConfig.consoleJson);
        };
    }

    /**
     * Creates a JSON formatter based on the provided JSON configuration.
     *
     * @param jsonConfig the {@link JsonConfig} for creating a {@link JsonFormatter}, specifying options additional fields,
     *                   pretty-printing, key overrides, serializers client configuration etc...
     *
     * @return a {@link RuntimeValue} containing an optional {@link Formatter}.<br>
     * If JSON formatting is disabled in the configuration, it returns an {@link Optional#empty()}, otherwise, it returns an optional containing a
     * {@link JsonFormatter} instance.
     */
    private RuntimeValue<Optional<Formatter>> createJsonFormater(final JsonConfig jsonConfig) {
        if (!jsonConfig.enable) {
            return new RuntimeValue<>(empty());
        }

        final StructuredLog structuredLog = getPrecomputedStructuredLog(jsonConfig);
        final JsonFactory   jsonFactory   = getJacksonJSONFactory(jsonConfig);
        final JsonFormatter jsonFormatter = new JsonFormatter(jsonConfig, structuredLog, jsonFactory);

        return new RuntimeValue<>(of(jsonFormatter));
    }

}
