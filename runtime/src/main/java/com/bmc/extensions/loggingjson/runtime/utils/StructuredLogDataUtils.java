package com.bmc.extensions.loggingjson.runtime.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;

import org.jboss.logmanager.ExtLogRecord;

import static java.util.Optional.ofNullable;

/**
 * Utility class for handling and populating structured log data with key-value mappings.<br>
 * Provides methods for processing log records, extracting data, and populating log details in a map.
 * <p>
 * Every method does as its name suggests, no documentation on each one is necessary.
 *
 * @author BareMetalCode
 */
public class StructuredLogDataUtils {

    private StructuredLogDataUtils() {

    }

    /**
     * Populates additional fields into the fields to render if any are present in the given structured log.
     * <p>
     * Fields can be rendered at top-level or wrapped together into a single field called "additionalFields" depending on configuration options.
     *
     * @param structuredLog  the structured log containing additional fields to be populated. May contain top-level or wrapped additional fields
     * @param fieldsToRender the map where the additional fields from the structured log will be added
     */
    public static void populateAdditionalFieldsIfPresent(final StructuredLog structuredLog, final Map<String, Object> fieldsToRender) {

        ofNullable(structuredLog.getAdditionalFieldsTop()).ifPresent(fieldsToRender::putAll);
        ofNullable(structuredLog.getAdditionalFieldsWrapped()).ifPresent(fields -> fieldsToRender.put("additionalFields", fields));
    }

    /**
     * Populates core fields into the provided map of fields to render.
     *
     * @param record         the log record from which core fields will be extracted
     * @param structuredLog  the structured log configuration containing the core record mapping
     * @param fieldsToRender the map where the core fields extracted from the log record will be added
     */
    public static void populateCoreFields(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender) {

        fieldsToRender.putAll(extractDataFromRecord(record, structuredLog.getCoreRecordMapping()));
    }

    /**
     * Populates detailed fields into the specified map of fields to render if the structured log configuration
     * is enabled to print details.
     * <p>
     * If enabled, it extracts additional details from the given log record using the details mapping configuration
     * provided by the structured log and adds them to the map of fields to render wrapped under the key "details".
     *
     * @param record         the log record from which detailed fields are extracted
     * @param structuredLog  the structured log configuration containing the details mapping and settings
     * @param fieldsToRender the map where the extracted detailed fields will be added if the "printDetails"
     *                       configuration is enabled
     */
    public static void populateDetailsIfEnabled(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender) {

        if (structuredLog.getJsonConfig().printDetails()) {
            fieldsToRender.put("details", extractDataFromRecord(record, structuredLog.getDetailsMapping()));
        }
    }

    /**
     * Populates exception-related fields if an exception is present in the given {@link ExtLogRecord}.
     * <p>
     * If the structured log is configured to print a classic stack trace, the method also adds corresponding
     * fields for the stack trace.
     *
     * @param record         the log record containing the exception to be processed
     * @param structuredLog  the structured log containing mappings and configurations for exception handling
     * @param fieldsToRender the map where extracted exception-related fields will be added
     */
    public static void populateExceptionIfPresent(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender) {

        if (record.getThrown() == null) {
            return;
        }

        fieldsToRender.putAll(extractExceptionFromRecord(record, structuredLog.getExceptionMapping(), structuredLog));

        if (structuredLog.getJsonConfig().printClassicStackTrace()) {
            fieldsToRender.putAll(
                    extractExceptionFromRecord(record, structuredLog.getExceptionStackTraceTopMapping(), structuredLog));
        }
    }

    private static Map<String, Object> extractDataFromRecord(final ExtLogRecord record, final Map<String, Function<ExtLogRecord, ?>> template) {

        final Map<String, Object> map = new LinkedHashMap<>(template.size());
        template.forEach((key, dataExtractingFunction) -> map.put(key, dataExtractingFunction.apply(record)));
        return map;
    }

    private static Map<String, Object> extractExceptionFromRecord(final ExtLogRecord record,
            final Map<String, BiFunction<ExtLogRecord, StructuredLog, ?>> template, final StructuredLog structuredLog) {

        final Map<String, Object> map = new LinkedHashMap<>(template.size());
        template.forEach((key, dataExtractingFunction) -> map.put(key, dataExtractingFunction.apply(record, structuredLog)));
        return map;
    }

}
