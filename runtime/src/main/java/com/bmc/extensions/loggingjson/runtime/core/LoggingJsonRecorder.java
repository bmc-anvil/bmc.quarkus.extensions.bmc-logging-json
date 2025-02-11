package com.bmc.extensions.loggingjson.runtime.core;

import java.util.Optional;
import java.util.logging.Formatter;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonOutputConfig;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogOutput;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

import static com.bmc.extensions.loggingjson.runtime.models.factory.JacksonMapperFactory.getJacksonMapper;
import static com.bmc.extensions.loggingjson.runtime.utils.RecordUtils.getRenderTemplate;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
@Recorder
public class LoggingJsonRecorder {

    public RuntimeValue<Optional<Formatter>> getJsonFormatterForLogOutputType(final JsonOutputConfig jsonOutputConfig, final LogOutput logOutput) {
        return switch (logOutput) {
            case FILE -> jsonFormater(jsonOutputConfig.fileJson);
            case CONSOLE -> jsonFormater(jsonOutputConfig.consoleJson);
        };
    }

    private RuntimeValue<Optional<Formatter>> jsonFormater(final JsonConfig jsonConfig) {
        if (!jsonConfig.enable) {
            return new RuntimeValue<>(Optional.empty());
        }

        final StructuredLog structuredLog = getRenderTemplate(jsonConfig);
        final ObjectMapper  jacksonMapper = getJacksonMapper(jsonConfig);
        final JsonFactory   jsonFactory   = jacksonMapper.getFactory();
        final JsonFormatter jsonFormatter = new JsonFormatter(jsonConfig, structuredLog, jsonFactory);

        return new RuntimeValue<>(Optional.of(jsonFormatter));
    }

}
