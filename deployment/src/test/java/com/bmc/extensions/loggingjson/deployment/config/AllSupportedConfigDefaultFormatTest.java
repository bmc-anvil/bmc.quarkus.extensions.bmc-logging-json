package com.bmc.extensions.loggingjson.deployment.config;

import java.util.Arrays;
import java.util.List;

import com.bmc.extensions.loggingjson.deployment.serializers.DummyTestSerializer;
import com.bmc.extensions.loggingjson.runtime.config.properties.ClientSerializerConfig;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.core.JsonFormatter;
import com.bmc.extensions.loggingjson.testutils.TestUtils;

import io.quarkus.bootstrap.logging.QuarkusDelayedHandler;
import io.quarkus.test.QuarkusUnitTest;

import org.jboss.logmanager.handlers.ConsoleHandler;
import org.jboss.logmanager.handlers.FileHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogFormat.DEFAULT;
import static com.bmc.extensions.loggingjson.testutils.TestUtils.extractJsonConfig;
import static io.quarkus.bootstrap.logging.InitialConfigurator.DELAYED_HANDLER;
import static org.jboss.logmanager.formatters.StructuredFormatter.ExceptionOutputType.DETAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testing that the bare defaults hold true when no application-launcher.properties is loaded
 * defaults should be.
 *
 * @author BareMetalCode
 */
public class AllSupportedConfigDefaultFormatTest {

    @RegisterExtension
    static final QuarkusUnitTest QUARKUS_UNIT_TEST = new QuarkusUnitTest()
            .withConfigurationResource("application-all-supported-loggers-enabled-full.properties")
            .withApplicationRoot(javaArchive -> javaArchive.addClass(TestUtils.class))
            .withAdditionalDependency(javaArchive -> javaArchive.addClass(DummyTestSerializer.class));

    private final List<String> excludedKeys =
            Arrays.asList("ndc", "sequence", "hostname", "processName", "processId", "loggerClassName", "threadId", "mdc");

    @Test
    public void fullConfigurationTest() {
        final QuarkusDelayedHandler delayedHandler = DELAYED_HANDLER;

        final ConsoleHandler consoleHandler = Arrays.stream(delayedHandler.getHandlers()).filter(handler -> handler instanceof ConsoleHandler)
                                                    .map(handler -> (ConsoleHandler) handler)
                                                    .findFirst()
                                                    .orElseThrow();

        final FileHandler fileHandler = Arrays.stream(delayedHandler.getHandlers()).filter(handler -> handler instanceof FileHandler)
                                              .map(handler -> (FileHandler) handler)
                                              .findFirst()
                                              .orElseThrow();

        final JsonFormatter consoleHandlerFormatter = (JsonFormatter) consoleHandler.getFormatter();
        final JsonFormatter fileHandlerFormatter    = (JsonFormatter) fileHandler.getFormatter();
        final JsonConfig    consoleJsonConfig       = extractJsonConfig(consoleHandlerFormatter);
        final JsonConfig    fileJsonConfig          = extractJsonConfig(fileHandlerFormatter);

        assertFullJsonConfig(consoleJsonConfig);
        assertFullJsonConfig(fileJsonConfig);
    }

    private void assertFullJsonConfig(final JsonConfig jsonConfig) {
        final ClientSerializerConfig clientSerializer = jsonConfig.clientSerializers();

        // assert top level config
        assertTrue(jsonConfig.enable());
        assertTrue(jsonConfig.prettyPrint());
        assertTrue(jsonConfig.printDetails());
        assertEquals("+05:00", jsonConfig.logZoneId().orElseThrow());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSZ", jsonConfig.logDateTimeFormat().orElseThrow());
        assertEquals(DEFAULT, jsonConfig.logFormat());
        assertEquals(DETAILED, jsonConfig.exceptionOutputType());

        jsonConfig.excludedKeys().get().forEach(key -> assertTrue(excludedKeys.contains(key)));

        // assert additionalFields top
        assertEquals("bar", jsonConfig.additionalFieldsTop().get("foo"));
        assertEquals("baz", jsonConfig.additionalFieldsTop().get("bar"));

        // assert additionalFields wrapped
        assertEquals("qux", jsonConfig.additionalFieldsWrapped().get("baz"));
        assertEquals("quux", jsonConfig.additionalFieldsWrapped().get("qux"));

        // assert key overrides
        assertEquals("information", jsonConfig.keyOverrides().get("ndc"));
        assertEquals("severity", jsonConfig.keyOverrides().get("level"));

        // assert client config
        // assert client temporal config
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS", clientSerializer.localDateTimeFormat().orElseThrow());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSZ", clientSerializer.zonedDateTimeFormat().orElseThrow());
        assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSZ", clientSerializer.instantFormat().orElseThrow());
        assertEquals("yyyy-MM-dd EEEE", clientSerializer.localDateFormat().orElseThrow());
        assertEquals("HH:mm:ss.SSS B", clientSerializer.localTimeFormat().orElseThrow());

        // assert client custom serializers
        assertEquals("com.bmc.extensions.loggingjson.deployment.serializers.DummyTestSerializer",
                     clientSerializer.customSerializers().get("dummylong"));
    }

}
