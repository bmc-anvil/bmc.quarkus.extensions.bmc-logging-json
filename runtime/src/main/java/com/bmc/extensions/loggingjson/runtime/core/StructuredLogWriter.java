package com.bmc.extensions.loggingjson.runtime.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
public class StructuredLogWriter {

    private static final String NEW_LINE = System.lineSeparator();

    private static JsonGenerator getJsonGenerator(final ByteArrayOutputStream writer, final JsonFactory jsonFactory, final JsonConfig jsonConfig)
            throws IOException {
        final JsonGenerator generator = jsonFactory.createGenerator(writer);
        return jsonConfig.prettyPrint ? generator.useDefaultPrettyPrinter() : generator;
    }

    public static String renderStructuredLog(final Map<String, Object> fieldsToRender, final JsonFactory jsonFactory, final JsonConfig jsonConfig) {
        final ByteArrayOutputStream writer = new ByteArrayOutputStream();
        try (final JsonGenerator generator = getJsonGenerator(writer, jsonFactory, jsonConfig)) {
            generator.writeObject(fieldsToRender);

//
//            if (jsonConfig.recordDelimiter.isPresent() && !thisIsTheLastRecord(fieldsToRender)) {
//                generator.writeRaw(jsonConfig.recordDelimiter.get());
//            }

            generator.writeRaw(NEW_LINE);
            generator.flush();

            return writer.toString();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    // HHHHHHorrible implementation to check if we are at the last message
    // I mean... just... no... like...why I am doing this...
    private static boolean thisIsTheLastRecord(final Map<String, Object> fieldsToRender) {
        final Object messageField = fieldsToRender.get("message");
        return messageField instanceof String && ((String) messageField).contains("stopped in");
    }

}
