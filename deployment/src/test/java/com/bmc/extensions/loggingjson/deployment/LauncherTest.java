package com.bmc.extensions.loggingjson.deployment;

import java.io.InvalidClassException;
import java.math.BigInteger;
import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.models.StructuredLogArgument;

import io.quarkus.test.QuarkusUnitTest;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.bmc.extensions.loggingjson.runtime.models.KeyValue.of;
import static com.bmc.extensions.loggingjson.runtime.models.StructuredLogArgument.logEntry;

/**
 * Just a launcher.
 *
 * @author BareMetalCode
 */
public class LauncherTest {

    @RegisterExtension
    static final QuarkusUnitTest QUARKUS_UNIT_TEST = new QuarkusUnitTest()
            .withEmptyApplication()
            .withConfigurationResource("application-launcher.properties");

    private static final Logger      logger      = Logger.getLogger("JBOSS");
    private static final TestingJson testingJson = new TestingJson();

    static {
        testingJson.name     = "test";
        testingJson.lastName = "Last test";

        final TestingJsonInner inner = new TestingJsonInner();
        inner.innerName     = "inner test";
        inner.innerLastName = Arrays.asList("testing", "the list", "1", "two", "more");

        testingJson.inner = inner;
    }

    @Test
    @Disabled
    public void exceptionFormattingTest() {

        try {
            final BigInteger one   = new BigInteger("1");
            final BigInteger two   = new BigInteger("0");
            final BigInteger three = one.divide(two);
        } catch (final Exception e) {
            logger.error("boom exception thrown", new InvalidClassException("and it was", e));
        }

    }

    @Test
    @Disabled
    public void testStructureNew() {

        final StructuredLogArgument structuredLogArgument =
                logEntry(of("TestStructure", testingJson),
                         of("secondEntry", "second"),
                         of("third Entry time", Instant.now()),
                         of("Forth Entry Map in map", Map.of("key", "value", "key2", "value2", "key3", "value3")));

        final StructuredLogArgument structuredLogArgumentSmall = logEntry(of("TestStructure", testingJson));

        final long startTime = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            logger.infof("", structuredLogArgumentSmall);
        }

        final long endTime  = System.nanoTime();
        final long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds

        logger.warn("Test 'testStructureNew' execution time: " + duration + " ms");
    }

    /**
     * Testing inner class.
     */
    public static class TestingJson {

        public TestingJsonInner         inner;
        public String                   lastName;
        public String                   name;
        public TestingJsonInnerTemporal temporal = new TestingJsonInnerTemporal();

        @Override
        public String toString() {

            return "testingJson{"
                   + ", inner=" + inner
                   + ", lastName='" + lastName + '\''
                   + ", name='" + name + '\''
                   + ", temporal=" + temporal
                   + '}';
        }

    }

    /**
     * Testing inner class.
     */
    public static class TestingJsonInnerTemporal {

        public Instant       instant       = Instant.now();
        public LocalDate     localDate     = LocalDate.now();
        public LocalDateTime localDateTime = LocalDateTime.now();
        public LocalTime     localTime     = LocalTime.now();
        public ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());

        @Override
        public String toString() {

            return "testingJsonInnerTemporal{"
                   + "instant=" + instant
                   + ", localDate=" + localDate
                   + ", localDateTime=" + localDateTime
                   + ", localTime=" + localTime
                   + ", zonedDateTime=" + zonedDateTime
                   + '}';
        }

    }

    /**
     * Testing inner class.
     */
    public static class TestingJsonInner {

        public List<String> innerLastName;
        public String       innerName;

        @Override
        public String toString() {

            return "testingJsonInner{"
                   + "innerLastName=" + innerLastName
                   + ", innerName='" + innerName + '\''
                   + '}';
        }

    }

}
