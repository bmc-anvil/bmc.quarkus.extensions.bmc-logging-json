package com.bmc.extensions.loggingjson.runtime;

import java.util.Optional;
import java.util.logging.Formatter;

import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.config.JsonLogConfig;
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
 *
 * @author BareMetalCode
 */
@Recorder
public class LoggingJsonRecorder {

    private final RuntimeValue<JsonLogConfig> runtimeJsonConfig;

    /**
     * Constructs a new instance of {@code LoggingJsonRecorder} with the specified runtime JSON configuration.
     * <p>
     * This is required by Quarkus deployment guidelines that state that RuntimeValues should be initialized in the Recorder's constructor.
     *
     * @param runtimeJsonConfig the {@link JsonLogConfig} wrapped in a {@link RuntimeValue}.
     *                          This parameter is the JSON log configuration, containing specific settings for the different log outputs.
     */
    public LoggingJsonRecorder(final RuntimeValue<JsonLogConfig> runtimeJsonConfig) {

        this.runtimeJsonConfig = runtimeJsonConfig;
    }

    /**
     * Creates a JSON formatter for the specified log output type based on the given JSON log configuration to inject into the Quarkus Extension.
     * <p>
     * This is the entry point for creating and configuring the JSON Logger Extension.
     *
     * @param logOutput the output type for which the JSON formatter is requested (e.g., console, file, etc.)
     *
     * @return a {@link RuntimeValue} containing an {@link Optional} JSON {@link Formatter}.
     * The optional will contain a formatter if JSON logging is enabled for the specified output; otherwise, it will be empty.
     */
    public RuntimeValue<Optional<Formatter>> getJsonFormatterForLogOutputType(final LogOutput logOutput) {

        return switch (logOutput) {
            case FILE -> createJsonFormater(runtimeJsonConfig.getValue().fileJson());
            case CONSOLE -> createJsonFormater(runtimeJsonConfig.getValue().consoleJson());
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

        if (!jsonConfig.enable()) {
            return new RuntimeValue<>(empty());
        }

        final StructuredLog structuredLog = getPrecomputedStructuredLog(jsonConfig);
        final JsonFactory   jsonFactory   = getJacksonJSONFactory(jsonConfig);
        final JsonFormatter jsonFormatter = new JsonFormatter(structuredLog, jsonFactory);

        return new RuntimeValue<>(of(jsonFormatter));
    }

}
