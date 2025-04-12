package com.bmc.extensions.loggingjson.runtime.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * StructuredLogWriter is responsible for writing the prepopulated fields to a Writer / OutputStream.
 * <p>
 * This is the class that will actually generate a full log record output.
 *
 * @author BareMetalCode
 */
public class StructuredLogWriter {

    private static final String NEW_LINE = System.lineSeparator();

    public static String formatRecord(final Map<String, Object> fieldsToRender, final JsonFactory jsonFactory, final JsonConfig jsonConfig) {

        final ByteArrayOutputStream writer = new ByteArrayOutputStream();
        try (final JsonGenerator generator = getJsonGenerator(writer, jsonFactory, jsonConfig)) {
            generator.writeObject(fieldsToRender);

            if (jsonConfig.recordDelimiter().isPresent() && !thisIsTheLastRecord(fieldsToRender)) {
                generator.writeRaw(jsonConfig.recordDelimiter().get());
            }

            generator.writeRaw(NEW_LINE);
            generator.flush();

            return writer.toString();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static JsonGenerator getJsonGenerator(final ByteArrayOutputStream writer, final JsonFactory jsonFactory, final JsonConfig jsonConfig)
            throws IOException {

        final JsonGenerator generator = jsonFactory.createGenerator(writer);
        return jsonConfig.prettyPrint() ? generator.useDefaultPrettyPrinter() : generator;
    }

    // FIXME: this seems a very innocent implementation.
    private static boolean thisIsTheLastRecord(final Map<String, Object> fieldsToRender) {

        final Object messageField = fieldsToRender.get("message");
        return messageField instanceof String && ((String) messageField).contains("stopped in");
    }

}
