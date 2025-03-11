package com.bmc.extensions.loggingjson.runtime.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;

import org.jboss.logmanager.ExtLogRecord;

import static com.bmc.extensions.loggingjson.runtime.utils.StructuredExceptionUtils.printClassicStackTrace;
import static com.bmc.extensions.loggingjson.runtime.utils.StructuredExceptionUtils.printStructuredException;
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

    private static Map<String, Object> extractDataFromRecord(final ExtLogRecord record, final Map<String, Function<ExtLogRecord, ?>> template) {

        final Map<String, Object> map = new HashMap<>();
        template.forEach((key, dataExtractingFunction) -> map.put(key, dataExtractingFunction.apply(record)));
        return map;
    }

    public static void populateAdditionalFieldsIfPresent(final StructuredLog structuredLog, final Map<String, Object> fieldsToRender) {

        ofNullable(structuredLog.getAdditionalFieldsTop()).ifPresent(fieldsToRender::putAll);
        ofNullable(structuredLog.getAdditionalFieldsWrapped()).ifPresent(fields -> fieldsToRender.put("additionalFields", fields));
    }

    public static void populateCoreFields(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender) {

        fieldsToRender.putAll(extractDataFromRecord(record, structuredLog.getCoreRecordMapping()));
    }

    public static void populateDetailsIfEnabled(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender, final Boolean printDetails) {

        if (printDetails) {
            fieldsToRender.put("details", extractDataFromRecord(record, structuredLog.getDetailsMapping()));
        }
    }

    public static void populateExceptionIfPresent(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender, final JsonConfig jsonConfig) {

        if (record.getThrown() == null) {
            return;
        }

        printStructuredException(record.getThrown(), structuredLog.getRecordKeys(), fieldsToRender, jsonConfig);

        if (jsonConfig.printClassicStackTrace()) {
            printClassicStackTrace(record.getThrown(), fieldsToRender, jsonConfig);
        }
    }

}
