package com.bmc.extensions.loggingjson.deployment.config;

import java.util.Arrays;

import com.bmc.extensions.loggingjson.testutils.TestUtils;

import io.quarkus.bootstrap.logging.InitialConfigurator;
import io.quarkus.bootstrap.logging.QuarkusDelayedHandler;
import io.quarkus.test.QuarkusUnitTest;

import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.ConsoleHandler;
import org.jboss.logmanager.handlers.FileHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Testing that with disabled JSON formatting we get the default {@link PatternFormatter} for all supported loggers.
 *
 * @author BareMetalCode
 */
public class AllSupportedLoggingJsonDisabledConfigTest {

    @RegisterExtension
    static final QuarkusUnitTest QUARKUS_UNIT_TEST = new QuarkusUnitTest()
            .withApplicationRoot(javaArchive -> javaArchive.addClass(TestUtils.class))
            .withConfigurationResource("application-all-supported-loggers-logging-json-disabled.properties");

    @Test
    public void loggingJsonDisabledTest() {

        final QuarkusDelayedHandler delayedHandler = InitialConfigurator.DELAYED_HANDLER;

        final int[] handlerCounter = Arrays.stream(delayedHandler.getHandlers()).reduce(new int[]{0, 0}, (accumulator, handler) -> {
            if (handler instanceof ConsoleHandler) {
                accumulator[0]++;
                assertInstanceOf(PatternFormatter.class, handler.getFormatter());
            }
            if (handler instanceof FileHandler) {
                accumulator[1]++;
                assertInstanceOf(PatternFormatter.class, handler.getFormatter());

            }
            return accumulator;
        }, (a, b) -> new int[]{a[0] + b[0], a[1] + b[1]});

        final int countConsoleHandlers = handlerCounter[0];
        final int countFileHandlers    = handlerCounter[1];

        assertEquals(1, countConsoleHandlers);
        assertEquals(1, countFileHandlers);

    }

}
