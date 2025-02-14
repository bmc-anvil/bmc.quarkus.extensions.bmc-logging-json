package com.bmc.extensions.loggingjson.runtime.models;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jboss.logmanager.ExtLogRecord;

import lombok.Getter;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey.*;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 * <p>
 * it is not a static final class, so it is instantiated then collected away instead of living in memo with no purpose
 * FIXME: check if the above statement holds true
 *
 * @author BareMetalCode
 */
@Getter
public class LogFunctionsMappings {

    private static final Function<ExtLogRecord, String>              GET_HOSTNAME           = ExtLogRecord::getHostName;
    private static final Function<ExtLogRecord, String>              GET_LEVEL              = extLogRecord -> extLogRecord.getLevel().getName();
    private static final Function<ExtLogRecord, String>              GET_LOGGER_CLASS_NAME  = ExtLogRecord::getLoggerClassName;
    private static final Function<ExtLogRecord, String>              GET_LOGGER_NAME        = ExtLogRecord::getLoggerName;
    private static final Function<ExtLogRecord, Map<String, String>> GET_MDC                = ExtLogRecord::getMdcCopy;
    private static final Function<ExtLogRecord, Object>              GET_MESSAGE            = LogFunctionsMappings::getStructuredMessage;
    private static final Function<ExtLogRecord, String>              GET_NDC                = ExtLogRecord::getNdc;
    private static final Function<ExtLogRecord, Long>                GET_PROCESS_ID         = ExtLogRecord::getProcessId;
    private static final Function<ExtLogRecord, String>              GET_PROCESS_NAME       = ExtLogRecord::getProcessName;
    private static final Function<ExtLogRecord, Long>                GET_SEQUENCE           = ExtLogRecord::getSequenceNumber;
    private static final Function<ExtLogRecord, String>              GET_SOURCE_CLASS_NAME  = ExtLogRecord::getSourceClassName;
    private static final Function<ExtLogRecord, String>              GET_SOURCE_FILE_NAME   = ExtLogRecord::getSourceFileName;
    private static final Function<ExtLogRecord, Integer>             GET_SOURCE_LINE_NUMBER = ExtLogRecord::getSourceLineNumber;
    private static final Function<ExtLogRecord, String>              GET_SOURCE_METHOD_NAME = ExtLogRecord::getSourceMethodName;
    private static final Function<ExtLogRecord, String>              GET_SOURCE_MODULE_NAME = ExtLogRecord::getSourceModuleName;
    private static final Function<ExtLogRecord, Long>                GET_THREAD_ID          = ExtLogRecord::getLongThreadID;
    private static final Function<ExtLogRecord, String>              GET_THREAD_NAME        = ExtLogRecord::getThreadName;
    private static final Function<ExtLogRecord, Throwable>           GET_THROWN             = ExtLogRecord::getThrown;
    private static final Function<ExtLogRecord, Instant>             GET_TIMESTAMP          = ExtLogRecord::getInstant;

    private final Map<String, Function<ExtLogRecord, ?>> basicRecordMapping = new HashMap<>(ofEntries(
            entry(HOSTNAME.getValue(), GET_HOSTNAME),
            entry(LEVEL.getValue(), GET_LEVEL),
            entry(LOGGER_CLASS_NAME.getValue(), GET_LOGGER_CLASS_NAME),
            entry(LOGGER_NAME.getValue(), GET_LOGGER_NAME),
            entry(MESSAGE.getValue(), GET_MESSAGE),
            entry(NDC.getValue(), GET_NDC),
            entry(PROCESS_ID.getValue(), GET_PROCESS_ID),
            entry(PROCESS_NAME.getValue(), GET_PROCESS_NAME),
            entry(SEQUENCE.getValue(), GET_SEQUENCE),
            entry(THREAD_ID.getValue(), GET_THREAD_ID),
            entry(THREAD_NAME.getValue(), GET_THREAD_NAME),
            entry(MDC.getValue(), GET_MDC),
            entry(TIMESTAMP.getValue(), GET_TIMESTAMP)));

    private final Map<String, Function<ExtLogRecord, ?>> detailsMapping = new HashMap<>(ofEntries(
            entry(SOURCE_CLASS_NAME.getValue(), GET_SOURCE_CLASS_NAME),
            entry(SOURCE_FILE_NAME.getValue(), GET_SOURCE_FILE_NAME),
            entry(SOURCE_LINE_NUMBER.getValue(), GET_SOURCE_LINE_NUMBER),
            entry(SOURCE_METHOD_NAME.getValue(), GET_SOURCE_METHOD_NAME),
            entry(SOURCE_MODULE_NAME.getValue(), GET_SOURCE_MODULE_NAME)));

    private final Map<String, Function<ExtLogRecord, ?>> exceptionMapping = new HashMap<>(ofEntries(
            entry(THROWN.getValue(), GET_THROWN)));

    private static boolean canBuildStructuredMessage(final Object[] messageParameters) {
        return messageParameters != null && messageParameters.length == 1 && messageParameters[0] instanceof Map<?, ?>;
    }

    private static Object getStructuredMessage(final ExtLogRecord extLogRecord) {
        final Object[] messageParameters = extLogRecord.getParameters();
        final Object   structuredMessage;

        if (canBuildStructuredMessage(messageParameters)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) messageParameters[0];
                structuredMessage = map;
            } catch (ClassCastException e) {
                // could give some sort of option here instead of just exploding.
                // leaving this during the lib stabilization phase.
                // structuredMessage = extLogRecord.getMessage().formatted(messageParameters);
                throw new IllegalArgumentException("Invalid map structure being injected to JSON Logger", e);
            }
        } else {
            structuredMessage = extLogRecord.getMessage().formatted(messageParameters);
        }
        return structuredMessage;
    }

}
