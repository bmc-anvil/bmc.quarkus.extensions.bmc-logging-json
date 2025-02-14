package com.bmc.extensions.loggingjson.deployment;

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.quarkus.test.QuarkusUnitTest;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
public class LoggingJsonStepBuilderTest {

    @RegisterExtension
    static final QuarkusUnitTest QUARKUS_UNIT_TEST = new QuarkusUnitTest()
            .withEmptyApplication()
            .withConfigurationResource("application.properties");

    private static final Logger      logger      = Logger.getLogger("JBOSS");
    private static final testingJson testingJson = new testingJson();
    static {
        testingJson.name     = "test";
        testingJson.lastName = "Last test";

        testingJsonInner inner = new testingJsonInner();
        inner.innerName     = "inner test";
        inner.innerLastName = Arrays.asList("testing", "the list", "1", "two", "more");

        testingJson.inner = inner;
    }

    @RepeatedTest(5000)
    public void test() {
        logger.infof("%s ", Map.of("TEST:::TEST", testingJson));
    }

    public static class testingJson {

        public testingJsonInner         inner;
        public String                   lastName;
        public String                   name;
        public testingJsonInnerTemporal temporal = new testingJsonInnerTemporal();

        @Override
        public String toString() {
            return "testingJson{" +
                   ", inner=" + inner +
                   ", lastName='" + lastName + '\'' +
                   ", name='" + name + '\'' +
                   ", temporal=" + temporal +
                   '}';
        }

    }

    public static class testingJsonInnerTemporal {

        public Instant       instant       = Instant.now();
        public LocalDate     localDate     = LocalDate.now();
        public LocalDateTime localDateTime = LocalDateTime.now();
        public LocalTime     localTime     = LocalTime.now();
        public ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());

        @Override
        public String toString() {
            return "testingJsonInnerTemporal{" +
                   "instant=" + instant +
                   ", localDate=" + localDate +
                   ", localDateTime=" + localDateTime +
                   ", localTime=" + localTime +
                   ", zonedDateTime=" + zonedDateTime +
                   '}';
        }

    }

    public static class testingJsonInner {

        public List<String> innerLastName;
        public String       innerName;

        @Override
        public String toString() {
            return "testingJsonInner{" +
                   "innerLastName=" + innerLastName +
                   ", innerName='" + innerName + '\'' +
                   '}';
        }

    }

}
