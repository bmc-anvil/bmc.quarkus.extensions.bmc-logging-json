package com.bmc.extensions.loggingjson.runtime;

import java.util.Optional;
import java.util.logging.Formatter;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonLogConfig;
import com.bmc.extensions.loggingjson.runtime.core.JsonFormatter;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogOutput;
import com.bmc.extensions.loggingjson.runtime.models.factory.JacksonMapperFactory;
import com.bmc.extensions.loggingjson.runtime.models.factory.StructuredLogFactory;
import com.fasterxml.jackson.core.JsonFactory;

import io.quarkus.runtime.RuntimeValue;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogOutput.CONSOLE;
import static com.bmc.extensions.loggingjson.runtime.models.enums.LogOutput.FILE;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LoggingJsonRecorderTest {

    private final LoggingJsonRecorder loggingJsonRecorder = new LoggingJsonRecorder();

    @Test
    void testConsoleJsonDisabled() {
        final JsonConfig    jsonConfig    = mockJsonConfig(false);
        final JsonLogConfig jsonLogConfig = mock(JsonLogConfig.class);
        when(jsonLogConfig.consoleJson()).thenReturn(jsonConfig);

        final RuntimeValue<Optional<Formatter>> result = loggingJsonRecorder.getJsonFormatterForLogOutputType(jsonLogConfig, CONSOLE);

        assertTrue(result.getValue()
                         .isEmpty());
    }

    @Test
    void testConsoleJsonEnabled() {
        final JsonConfig    jsonConfig    = mockJsonConfig(true);
        final JsonLogConfig jsonLogConfig = mock(JsonLogConfig.class);
        when(jsonLogConfig.consoleJson()).thenReturn(jsonConfig);

        assertFormatterPresence(jsonConfig, jsonLogConfig, CONSOLE);
    }

    @Test
    void testFileJsonDisabled() {
        final JsonConfig    jsonConfig    = mockJsonConfig(false);
        final JsonLogConfig jsonLogConfig = mock(JsonLogConfig.class);
        when(jsonLogConfig.fileJson()).thenReturn(jsonConfig);

        final RuntimeValue<Optional<Formatter>> result = loggingJsonRecorder.getJsonFormatterForLogOutputType(jsonLogConfig, FILE);

        assertTrue(result.getValue()
                         .isEmpty());
    }

    @Test
    void testFileJsonEnabled() {
        final JsonConfig    jsonConfig    = mockJsonConfig(true);
        final JsonLogConfig jsonLogConfig = mock(JsonLogConfig.class);
        when(jsonLogConfig.fileJson()).thenReturn(jsonConfig);

        assertFormatterPresence(jsonConfig, jsonLogConfig, FILE);
    }

    private void assertFormatterPresence(JsonConfig jsonConfig, JsonLogConfig jsonLogConfig, LogOutput logOutput) {
        final StructuredLog structuredLog = mock(StructuredLog.class);
        final JsonFactory   jsonFactory   = mock(JsonFactory.class);

        try (final MockedStatic<StructuredLogFactory> structuredLogMockedStatic = mockStatic(StructuredLogFactory.class);
             final MockedStatic<JacksonMapperFactory> jacksonMapperMockedStatic = mockStatic(JacksonMapperFactory.class)) {

            structuredLogMockedStatic.when(() -> StructuredLogFactory.getPrecomputedStructuredLog(jsonConfig))
                                     .thenReturn(structuredLog);
            jacksonMapperMockedStatic.when(() -> JacksonMapperFactory.getJacksonJSONFactory(jsonConfig))
                                     .thenReturn(jsonFactory);

            final RuntimeValue<Optional<Formatter>> result = loggingJsonRecorder.getJsonFormatterForLogOutputType(jsonLogConfig, logOutput);

            assertTrue(result.getValue()
                             .isPresent());
            assertInstanceOf(JsonFormatter.class, result.getValue()
                                                        .get());
        }
    }

    private JsonConfig mockJsonConfig(boolean isEnabled) {
        final JsonConfig jsonConfig = mock(JsonConfig.class);
        when(jsonConfig.enable()).thenReturn(isEnabled);
        return jsonConfig;
    }

}
