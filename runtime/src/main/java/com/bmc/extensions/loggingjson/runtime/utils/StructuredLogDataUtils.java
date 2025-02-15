package com.bmc.extensions.loggingjson.runtime.utils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey;

import org.jboss.logmanager.ExtLogRecord;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey.*;
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

    private static Map<String, Object> buildExceptionField(final Map<String, Object> exceptionMap, final EnumMap<LogRecordKey, String> recordKeys) {
        final Throwable thrownException = (Throwable) exceptionMap.get(recordKeys.get(THROWN));

        final Map<String, Object> exceptions = new HashMap<>();
        exceptions.put(recordKeys.get(EXCEPTION_TYPE), thrownException.getClass().getName());
        exceptions.put(recordKeys.get(EXCEPTION_MESSAGE), thrownException.getMessage());
        // FIXME: add a formatted stacktrace
        //        exceptions.put(EXCEPTION_FRAMES, thrown.getStackTrace());
        exceptions.put(recordKeys.get(EXCEPTION_CAUSED_BY), thrownException.getCause());

        return exceptions;
    }

    private static Map<String, Object> extractDataFromRecord(final ExtLogRecord record, final Map<String, Function<ExtLogRecord, ?>> template) {
        final Map<String, Object> map = new HashMap<>();
        template.forEach((key, dataExtractingFunction) -> map.put(key, dataExtractingFunction.apply(record)));
        return map;
    }

    public static void populateAdditionalFieldsIfAny(final StructuredLog structuredLog, final Map<String, Object> fieldsToRender) {
        ofNullable(structuredLog.getAdditionalFieldsTop()).ifPresent(fieldsToRender::putAll);
        ofNullable(structuredLog.getAdditionalFieldsWrapped())
                .ifPresent(wrappedAdditionalFields -> fieldsToRender.put("additionalFields", wrappedAdditionalFields));
    }

    public static void populateCoreFields(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender) {
        fieldsToRender.putAll(extractDataFromRecord(record, structuredLog.getCoreRecordMapping()));
    }

    public static void populateDetailsIfConfigured(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender,
            final Boolean printDetails) {
        if (printDetails) {
            fieldsToRender.put("details", extractDataFromRecord(record, structuredLog.getDetailsMapping()));
        }
    }

    public static void populateExceptionIfPresent(final ExtLogRecord record, final StructuredLog structuredLog,
            final Map<String, Object> fieldsToRender) {
        if (record.getThrown() != null) {
            final String exceptionKey = structuredLog.getRecordKeys().get(EXCEPTION);
            fieldsToRender.put(exceptionKey, buildExceptionField(extractDataFromRecord(record, structuredLog.getExceptionMapping()),
                                                                 structuredLog.getRecordKeys()));
        }
    }

}
