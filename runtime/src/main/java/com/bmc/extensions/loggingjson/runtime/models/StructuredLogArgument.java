package com.bmc.extensions.loggingjson.runtime.models;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;

import static com.bmc.extensions.loggingjson.runtime.models.LogEntryPosition.MSG_FIELD;

/**
 * Represents an argument for structured logging that encapsulates log entry content and its placement regarding log fields.
 * <p>
 * --<br>
 * performance considerations: {@link LinkedHashMap} vs {@link LinkedHashMap}.<br>
 * --<br>
 * The difference is very small in terms of performance, but there is one. A {@link LinkedHashMap} respects ordering but takes a little longer to be
 * instantiated. This will go completely unnoticed for most usages, and after doing some experiments I'll just use {@link LinkedHashMap}.
 */
@Getter
public class StructuredLogArgument {

    private static final LogEntryPosition    DEFAULT_POSITION = MSG_FIELD;
    private final        Map<String, Object> contentToRender;
    private final        LogEntryPosition    logEntryPosition;

    private StructuredLogArgument(final LogEntryPosition logEntryPosition, final Map<String, Object> contentToRender) {

        this.contentToRender  = contentToRender;
        this.logEntryPosition = logEntryPosition;
    }

    /**
     * Creates and returns a new instance of {@link StructuredLogArgument} with the provided log entry position, and structured key-value entries.
     *
     * @param entryPosition     the position of the log entry in the structured log (e.g., TOP or MSG_FIELD)
     * @param structuredEntries an array of key-value pairs to be included in the structured log entry
     *
     * @return a new instance of {@link StructuredLogArgument} containing the given key-value entries and position
     */
    public static StructuredLogArgument logEntry(final LogEntryPosition entryPosition, final KeyValue... structuredEntries) {
        // The fixed capacity can help in footprint
        final int                 mapCapacity     = structuredEntries.length + 1;
        final Map<String, Object> contentToRender = new LinkedHashMap<>(mapCapacity);

        for (final KeyValue structuredEntry : structuredEntries) {
            contentToRender.put(structuredEntry.getKey(), structuredEntry.getValue());
        }

        return new StructuredLogArgument(entryPosition, contentToRender);
    }

    /**
     * Creates and returns a new instance of {@link StructuredLogArgument} with structured key-value entries.
     * <p>
     * LogPosition of the key values defaults to {@link LogEntryPosition#MSG_FIELD}.
     *
     * @param structuredEntries an array of key-value pairs to be included in the structured log entry
     *
     * @return a new instance of {@link StructuredLogArgument} containing the given key-value entries and position
     */
    public static StructuredLogArgument logEntry(final KeyValue... structuredEntries) {

        return logEntry(DEFAULT_POSITION, structuredEntries);
    }

}
