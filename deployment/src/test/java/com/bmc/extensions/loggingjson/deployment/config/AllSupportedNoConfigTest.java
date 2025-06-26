package com.bmc.extensions.loggingjson.deployment.config;

import java.util.Arrays;

import com.bmc.extensions.loggingjson.runtime.config.ClientSerializerConfig;
import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.core.JsonFormatter;
import com.bmc.extensions.loggingjson.testutils.TestUtils;

import io.quarkus.bootstrap.logging.InitialConfigurator;
import io.quarkus.bootstrap.logging.QuarkusDelayedHandler;
import io.quarkus.test.QuarkusUnitTest;

import org.jboss.logmanager.handlers.ConsoleHandler;
import org.jboss.logmanager.handlers.FileHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogFormat.DEFAULT;
import static com.bmc.extensions.loggingjson.runtime.models.enums.StackTraceDetail.ONE_LINER;
import static com.bmc.extensions.loggingjson.testutils.TestUtils.extractJsonConfig;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing that the bare defaults hold true when no application-launcher.properties is loaded
 * defaults should be.
 *
 * @author BareMetalCode
 */
public class AllSupportedNoConfigTest {

    @RegisterExtension
    static final QuarkusUnitTest QUARKUS_UNIT_TEST = new QuarkusUnitTest()
            .withApplicationRoot(javaArchive -> javaArchive.addClass(TestUtils.class));

    @Test
    public void fullDefaultConfigurationTest() {

        final QuarkusDelayedHandler delayedHandler = InitialConfigurator.DELAYED_HANDLER;

        final int[] handlerCounter = Arrays.stream(delayedHandler.getHandlers()).reduce(new int[]{0, 0}, (accumulator, handler) -> {
            if (handler instanceof ConsoleHandler) {
                accumulator[0]++;
                final JsonFormatter formatter = (JsonFormatter) handler.getFormatter();
                assertDefaultJsonConfig(extractJsonConfig(formatter));
            }
            if (handler instanceof FileHandler) {
                accumulator[1]++;
            }
            return accumulator;
        }, (a, b) -> new int[]{a[0] + b[0], a[1] + b[1]});

        final int countConsoleHandlers = handlerCounter[0];
        final int countFileHandlers    = handlerCounter[1];

        assertEquals(1, countConsoleHandlers);
        assertEquals(0, countFileHandlers);

    }

    private void assertDefaultJsonConfig(final JsonConfig jsonConfig) {

        final ClientSerializerConfig clientSerializer = jsonConfig.clientSerializers();

        assertTrue(jsonConfig.enable());
        assertTrue(jsonConfig.additionalFieldsTop().isEmpty());
        assertTrue(jsonConfig.additionalFieldsWrapped().isEmpty());
        assertTrue(jsonConfig.excludedKeys().isEmpty());
        assertTrue(jsonConfig.keyOverrides().isEmpty());
        assertTrue(jsonConfig.logDateTimeFormat().isEmpty());
        assertTrue(jsonConfig.logZoneId().isEmpty());
        assertTrue(jsonConfig.recordDelimiter().isEmpty());
        assertTrue(clientSerializer.customSerializers().isEmpty());
        assertTrue(clientSerializer.instantFormat().isEmpty());
        assertTrue(clientSerializer.localDateFormat().isEmpty());
        assertTrue(clientSerializer.localTimeFormat().isEmpty());
        assertTrue(clientSerializer.zonedDateTimeFormat().isEmpty());

        assertEquals(DEFAULT, jsonConfig.logFormat());
        assertEquals(ONE_LINER, jsonConfig.exceptions().exceptionDetail());

        assertFalse(jsonConfig.prettyPrint());
        assertFalse(jsonConfig.printDetails());
    }

}
