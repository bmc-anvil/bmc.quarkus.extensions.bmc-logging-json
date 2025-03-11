package com.bmc.extensions.loggingjson.deployment.output;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import com.bmc.extensions.loggingjson.deployment.serializers.DummyTestSerializer;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.core.JsonFormatter;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLogArgument;
import com.bmc.extensions.loggingjson.testutils.DummyAddressPOJO;
import com.bmc.extensions.loggingjson.testutils.DummyPOJO;
import com.bmc.extensions.loggingjson.testutils.TestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.test.QuarkusUnitTest;

import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.formatters.PatternFormatter;
import org.jboss.logmanager.handlers.ConsoleHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.bmc.extensions.loggingjson.runtime.models.KV.of;
import static com.bmc.extensions.loggingjson.runtime.models.StructuredLogArgument.logEntry;
import static com.bmc.extensions.loggingjson.testutils.TestUtils.extractJsonConfig;
import static io.quarkus.bootstrap.logging.InitialConfigurator.DELAYED_HANDLER;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static org.jboss.logmanager.Level.INFO;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing the format itself produces the expected output
 *
 * @author BareMetalCode
 */
public class FormatterOutputTest {

    @RegisterExtension
    static final QuarkusUnitTest     QUARKUS_UNIT_TEST       = new QuarkusUnitTest()
            .withConfigurationResource("application-console-logger-full.properties")
            .withApplicationRoot(javaArchive -> javaArchive.addClass(TestUtils.class))
            .withAdditionalDependency(javaArchive -> javaArchive.addClass(DummyPOJO.class))
            .withAdditionalDependency(javaArchive -> javaArchive.addClass(DummyAddressPOJO.class))
            .withAdditionalDependency(javaArchive -> javaArchive.addClass(DummyTestSerializer.class))
            .withAdditionalDependency(javaArchive -> javaArchive.addClass(PatternFormatter.class));
    static final DummyAddressPOJO    dummyAddressPOJO        = new DummyAddressPOJO();
    static final DummyPOJO           dummyPOJO               = new DummyPOJO();
    static       ConsoleHandler      consoleHandler;
    static       JsonConfig          jsonConfig;
    final        Map<String, String> additionalFieldsWrapped = Map.of("baz", "qux", "qux", "quux");
    final        List<String>        detailsFields           =
            List.of("sourceFileName", "sourceMethodName", "sourceLineNumber", "sourceClassName", "sourceSimpleClassName");
    final        Level               level                   = INFO;
    final        String              loggerClassName         = this.getClass().getName();
    final        String              loggerName              = "loggerName";
    final        ObjectMapper        mapper                  = new ObjectMapper();
    final        Map<String, String> mdc                     = Map.of("mdcKey_01", "mdcValue_01", "mdcKey_02", "mdcValue_02");
    final        String              ndc                     = "Dummy NDC String";
    final        Map<String, Object> expectedKeys            = new HashMap<>(ofEntries(entry("level", level),
                                                                                       entry("additionalFields", additionalFieldsWrapped),
                                                                                       entry("loggerClassName", loggerClassName),
                                                                                       entry("foo", "bar"),
                                                                                       entry("bar", "baz"),
                                                                                       entry("ndc", ndc),
                                                                                       entry("mdc", mdc),
                                                                                       entry("threadName", ""),
                                                                                       entry("threadId", "Dummy Serializer Long Value:"),
                                                                                       entry("sequence", "Dummy Serializer Long Value:"),
                                                                                       entry("hostname", getHostName()),
                                                                                       entry("processId", "Dummy Serializer Long Value:"),
                                                                                       entry("processName", ""),
                                                                                       entry("message", ""),
                                                                                       entry("details", ""),
                                                                                       entry("loggerName", loggerName),
                                                                                       entry("timestamp", "")));

    @BeforeAll
    static void setup() {

        dummyAddressPOJO.setDummyCity("dummy city");
        dummyAddressPOJO.setDummyStreet("dummy street");

        dummyPOJO.setDummyAddress(dummyAddressPOJO);
        dummyPOJO.setDummyAge(12);
        dummyPOJO.setDummyLongId(123L);
        dummyPOJO.setDummyFistName("John");
        dummyPOJO.setDummyLastName("Doe");

        consoleHandler = Arrays.stream(DELAYED_HANDLER.getHandlers()).filter(handler -> handler instanceof ConsoleHandler)
                               .map(handler -> (ConsoleHandler) handler)
                               .findFirst()
                               .orElseThrow();

        final JsonFormatter formatter = (JsonFormatter) consoleHandler.getFormatter();
        jsonConfig = extractJsonConfig(formatter);

    }

    /**
     * FIxME: add message tag testing
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "test message to appear in message tag"})
    public void formatterOutputTest(final String message) throws IOException {

        final ExtLogRecord          extLogRecord      = new ExtLogRecord(level, message, loggerClassName);
        final StructuredLogArgument structureToOutput = logEntry(of("dummyPojo", dummyPOJO), of("dummyAddressPojo", dummyAddressPOJO));
        final Object[]              parameters        = new Object[]{structureToOutput};

        extLogRecord.setParameters(parameters);
        extLogRecord.setLoggerName(loggerName);
        extLogRecord.setMdc(mdc);
        extLogRecord.setNdc(ndc);

        expectedKeys.put("message", dummyPOJO);

        final String   structuredOutput = consoleHandler.getFormatter().format(extLogRecord);
        final JsonNode outputAsJsonNode = mapper.readValue(structuredOutput, JsonNode.class);

        expectedKeys.keySet().forEach(key -> assertTrue(outputAsJsonNode.has(key), "Missing key: " + key));

        for (Iterator<String> it = outputAsJsonNode.fieldNames(); it.hasNext(); ) {
            assertOutput(it.next(), outputAsJsonNode);
        }

        if (message != null && !message.isEmpty()) {
            assertEquals(message, outputAsJsonNode.get("message").get("_msgTag").asText());
        } else {
            assertNull(outputAsJsonNode.get("message").get("_msgTag"));
        }
    }

    private void assertDetails(final JsonNode outputAsJsonNode) {

        outputAsJsonNode.get("details").fields().forEachRemaining(entry -> {
            assertTrue(detailsFields.contains(entry.getKey()));
            assertNotNull(entry.getValue().asText());
        });

    }

    private void assertMessage(final JsonNode outputAsJsonNode) {

        final JsonNode dummyPOJOFromLog        = outputAsJsonNode.get("message").get("dummyPojo");
        final JsonNode dummyAddressPOJOFromLog = outputAsJsonNode.get("message").get("dummyAddressPojo");

        assertNotNull(dummyPOJOFromLog);
        assertNotNull(dummyAddressPOJOFromLog);

        assertEquals(dummyPOJO.getDummyAge(), dummyPOJOFromLog.get("dummyAge").asInt());
        assertEquals(dummyPOJO.getDummyFistName(), dummyPOJOFromLog.get("dummyFistName").asText());
        assertEquals(dummyPOJO.getDummyLastName(), dummyPOJOFromLog.get("dummyLastName").asText());
        assertEquals(dummyPOJO.getDummyLongId(), dummyPOJOFromLog.get("dummyLongId").asLong());
        assertEquals(dummyPOJO.getDummyAddress().getDummyCity(), dummyPOJOFromLog.get("dummyAddress").get("dummyCity").asText());
        assertEquals(dummyPOJO.getDummyAddress().getDummyStreet(), dummyPOJOFromLog.get("dummyAddress").get("dummyStreet").asText());

        assertEquals(dummyAddressPOJO.getDummyCity(), dummyAddressPOJOFromLog.get("dummyCity").asText());
        assertEquals(dummyAddressPOJO.getDummyStreet(), dummyAddressPOJOFromLog.get("dummyStreet").asText());

    }

    private void assertNotNullField(final String fieldName, final JsonNode outputAsJsonNode, final String assertMsg) {

        assertNotNull(outputAsJsonNode.get(fieldName), assertMsg);
    }

    private void assertOutput(final String fieldName, final JsonNode outputAsJsonNode) {

        assertTrue(expectedKeys.containsKey(fieldName), "evaluating: " + fieldName);
        final String assertMsg = "Mismatch in field:" + fieldName;

        switch (fieldName) {
            case "level" -> assertEquals(level.getName(), outputAsJsonNode.get(fieldName).asText(), assertMsg);
            case "additionalFields", "mdc" -> assertSimpleMapFields(fieldName, outputAsJsonNode, assertMsg);
            case "details" -> assertDetails(outputAsJsonNode);
            case "message" -> assertMessage(outputAsJsonNode);
            case "timestamp" -> assertTimeStamp(outputAsJsonNode);
            case "loggerName", "hostname", "loggerClassName", "ndc", "bar", "foo" -> assertTextField(fieldName, outputAsJsonNode, assertMsg);
            case "threadName", "processName", "processId", "sequence", "threadId" -> assertNotNullField(fieldName, outputAsJsonNode, assertMsg);
            default -> throw new IllegalStateException("Unexpected field: " + fieldName);
        }
    }

    private void assertSimpleMapFields(final String fieldName, final JsonNode outputAsJsonNode, final String assertMsg) {

        outputAsJsonNode.get(fieldName).fields().forEachRemaining(entry -> {
            final String key   = entry.getKey();
            final String value = entry.getValue().asText();

            final String string = ((Map<?, ?>) expectedKeys.get(fieldName)).get(key).toString();
            assertEquals(string, value, assertMsg);
        });

    }

    private void assertTextField(final String fieldName, final JsonNode outputAsJsonNode, final String assertMsg) {

        assertEquals(expectedKeys.get(fieldName), outputAsJsonNode.get(fieldName).asText(), assertMsg);
    }

    private void assertTimeStamp(final JsonNode outputAsJsonNode) {
        // executing all these steps guarantees that the formatter and the timestamps are correct and able to parse timestamps
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(jsonConfig.logDateTimeFormat().orElseThrow());
        final ZoneId            zoneId    = ZoneId.of(jsonConfig.logZoneId().orElseThrow());
        final TemporalAccessor  timestamp = formatter.withZone(zoneId).parse(outputAsJsonNode.get("timestamp").asText());
        final Instant           instant   = Instant.from(timestamp);

        assertNotNull(instant);
    }

    private String getHostName() {

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
