package com.bmc.extensions.loggingjson.runtime.models;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.utils.StructuredExceptionUtils;

import org.jboss.logmanager.ExtLogRecord;

import lombok.Getter;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey.*;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static java.util.Optional.ofNullable;

/**
 * LogFunctionsMappings is responsible for providing mappings of log record keys
 * to functions that extract specific data from an instance of {@link ExtLogRecord}.
 * <p>
 * The mappings are categorized into three primary groups:
 * <p>
 * 1. {@code basicRecordMapping} Contains mappings for commonly used log record properties
 * such as the hostname, log level, logger information, message, thread details, and more.
 * <p>
 * 2. {@code detailsMapping} Contains mappings for additional log record details, including
 * source class name, source file name, line number, method name, and module name.
 * <p>
 * 3. {@code exceptionMapping} Contains mappings specifically for extracting exception-related
 * details from a log record.
 * <p>
 * It also includes helper functions to process and structure log messages effectively:
 * <p>
 * - {@code canBuildStructuredMessage} Determines if the log message can be represented as a structured message
 * based on its parameters.<br>
 * - {@code getStructuredMessage} Processes the message parameters to return a structured or formatted message.
 * <p>
 * This class facilitates efficient data extraction and preparation for JSON logging or other structured
 * log formats by centralizing and organizing the mappings and functions required for log record processing.
 *
 * @author BareMetalCode
 */
@Getter
public class LogFunctionsMappings {

    private static final BiFunction<ExtLogRecord, StructuredLog, String> GET_EX_CAUSED_BY        =
            (record, _) -> ofNullable(record.getThrown().getCause()).map(Throwable::getMessage).orElse(null);
    private static final BiFunction<ExtLogRecord, StructuredLog, Object> GET_EX_FIELD            = StructuredExceptionUtils::printStructuredException;
    private static final BiFunction<ExtLogRecord, StructuredLog, String> GET_EX_MESSAGE          = (record, _) -> record.getThrown().getMessage();
    private static final BiFunction<ExtLogRecord, StructuredLog, Object> GET_EX_STACKTRACE_INNER = StructuredExceptionUtils::printInnerStackTrace;
    private static final BiFunction<ExtLogRecord, StructuredLog, String> GET_EX_STACKTRACE_TOP   = StructuredExceptionUtils::printClassicStackTrace;
    private static final BiFunction<ExtLogRecord, StructuredLog, String> GET_EX_TYPE             =
            (record, _) -> record.getThrown().getClass().getName();
    //
    private static final Function<ExtLogRecord, String>                  GET_HOSTNAME            = ExtLogRecord::getHostName;
    private static final Function<ExtLogRecord, String>                  GET_LEVEL               = record -> record.getLevel().getName();
    private static final Function<ExtLogRecord, String>                  GET_LOGGER_CLASS_NAME   = ExtLogRecord::getLoggerClassName;
    private static final Function<ExtLogRecord, String>                  GET_LOGGER_NAME         = ExtLogRecord::getLoggerName;
    private static final Function<ExtLogRecord, Map<String, String>>     GET_MDC                 = ExtLogRecord::getMdcCopy;
    private static final Function<ExtLogRecord, Object>                  GET_MESSAGE             = LogFunctionsMappings::getStructuredMessage;
    private static final Function<ExtLogRecord, String>                  GET_NDC                 = ExtLogRecord::getNdc;
    private static final Function<ExtLogRecord, Long>                    GET_PROCESS_ID          = ExtLogRecord::getProcessId;
    private static final Function<ExtLogRecord, String>                  GET_PROCESS_NAME        = ExtLogRecord::getProcessName;
    private static final Function<ExtLogRecord, Long>                    GET_SEQUENCE            = ExtLogRecord::getSequenceNumber;
    private static final Function<ExtLogRecord, String>                  GET_SOURCE_CLASS_NAME   = ExtLogRecord::getSourceClassName;
    private static final Function<ExtLogRecord, String>                  GET_SOURCE_FILE_NAME    = ExtLogRecord::getSourceFileName;
    private static final Function<ExtLogRecord, Integer>                 GET_SOURCE_LINE_NUMBER  = ExtLogRecord::getSourceLineNumber;
    private static final Function<ExtLogRecord, String>                  GET_SOURCE_METHOD_NAME  = ExtLogRecord::getSourceMethodName;
    private static final Function<ExtLogRecord, String>                  GET_SOURCE_MODULE_NAME  = ExtLogRecord::getSourceModuleName;
    private static final Function<ExtLogRecord, Long>                    GET_THREAD_ID           = ExtLogRecord::getLongThreadID;
    private static final Function<ExtLogRecord, String>                  GET_THREAD_NAME         = ExtLogRecord::getThreadName;
    private static final Function<ExtLogRecord, Instant>                 GET_TIMESTAMP           = ExtLogRecord::getInstant;

    /**
     * The order of all these maps' entries will be respected in the final rendering of the log.
     */
    private final Map<String, Function<ExtLogRecord, ?>> basicRecordMapping = new LinkedHashMap<>(ofEntries(
            entry(HOSTNAME.getValue(), GET_HOSTNAME),
            entry(LEVEL.getValue(), GET_LEVEL),
            entry(LOGGER_CLASS_NAME.getValue(), GET_LOGGER_CLASS_NAME),
            entry(LOGGER_NAME.getValue(), GET_LOGGER_NAME),
            entry(MESSAGE.getValue(), GET_MESSAGE),
            entry(MDC.getValue(), GET_MDC),
            entry(NDC.getValue(), GET_NDC),
            entry(PROCESS_ID.getValue(), GET_PROCESS_ID),
            entry(PROCESS_NAME.getValue(), GET_PROCESS_NAME),
            entry(SEQUENCE.getValue(), GET_SEQUENCE),
            entry(THREAD_ID.getValue(), GET_THREAD_ID),
            entry(THREAD_NAME.getValue(), GET_THREAD_NAME),
            entry(TIMESTAMP.getValue(), GET_TIMESTAMP)));

    private final Map<String, Function<ExtLogRecord, ?>> detailsMapping = new LinkedHashMap<>(ofEntries(
            entry(SOURCE_MODULE_NAME.getValue(), GET_SOURCE_MODULE_NAME),
            entry(SOURCE_CLASS_NAME.getValue(), GET_SOURCE_CLASS_NAME),
            entry(SOURCE_FILE_NAME.getValue(), GET_SOURCE_FILE_NAME),
            entry(SOURCE_METHOD_NAME.getValue(), GET_SOURCE_METHOD_NAME),
            entry(SOURCE_LINE_NUMBER.getValue(), GET_SOURCE_LINE_NUMBER)));

    private final Map<String, BiFunction<ExtLogRecord, StructuredLog, ?>> exceptionInnerMapping = new LinkedHashMap<>(ofEntries(
            entry(EXCEPTION_MESSAGE.getValue(), GET_EX_MESSAGE),
            entry(EXCEPTION_TYPE.getValue(), GET_EX_TYPE),
            entry(EXCEPTION_CAUSED_BY.getValue(), GET_EX_CAUSED_BY),
            entry(EXCEPTION_STACK_TRACE.getValue(), GET_EX_STACKTRACE_INNER)));

    private final Map<String, BiFunction<ExtLogRecord, StructuredLog, ?>> exceptionMapping = new LinkedHashMap<>(ofEntries(
            entry(EXCEPTION.getValue(), GET_EX_FIELD)));

    private final Map<String, BiFunction<ExtLogRecord, StructuredLog, ?>> topStackTraceMapping = new LinkedHashMap<>(ofEntries(
            entry(EXCEPTION_STACK_TRACE.getValue(), GET_EX_STACKTRACE_TOP)));

    /**
     * Evaluates the parameters from the {@link ExtLogRecord} to determine if a "Structured Message" can be built from them.
     * <p>
     * These rules can change, and until the library is tested, this should be considered HIGHLY unstable.
     * <p>
     * The key rule is to determine if we have a single parameter and if it is a {@link StructuredLogArgument}.
     * In the constructed JSON, the message part, if not empty, will appear as an inner field within the message field itself and
     * the {@link KeyValue} entry/es will be printed in the message field as individual sub entries.
     * <p>
     * example:<br>
     * <pre>
     * {@code
     * logger.infof("maybe a title for the message", logEntry(of("buyer", buyerUser),of("seller", sellerUser)));
     * }
     * will render something like:<br>
     * <pre>
     * {@code
     * "message": {
     *      "_msgTag": "maybe a title for the message",
     *      "buyer": { "name": "john" },
     *      "seller": { "name": "tom" }
     *      }
     * }
     * </pre>
     * and<br>
     * <pre>
     * {@code
     * logger.infof("", logEntry(of("buyer", buyerUser),of("seller", sellerUser)));
     * }
     * </pre>
     * will render something like:<br>
     * <pre>
     * {@code
     * "message": {
     *      "buyer": { "name": "john" },
     *      "seller": { "name": "tom" }
     *      }
     * }
     * </pre>
     *
     * @param messageParameters {@link ExtLogRecord} parameters field.
     *
     * @return true if the parameter complies with the rules for the message to be considered a "Structured object"
     */
    private static boolean canBuildStructuredJSONMessage(final Object[] messageParameters) {

        return messageParameters != null && messageParameters.length == 1 && messageParameters[0] instanceof StructuredLogArgument;
    }

    private static Object getStructuredMessage(final ExtLogRecord extLogRecord) {

        final Object[] messageParameters = extLogRecord.getParameters();
        final Object   structuredMessage;

        if (canBuildStructuredJSONMessage(messageParameters)) {
            try {
                final Map<String, Object> map = ((StructuredLogArgument) messageParameters[0]).getContentToRender();
                if (extLogRecord.getMessage() != null && !extLogRecord.getMessage().isEmpty()) {
                    map.put("_msgTag", extLogRecord.getMessage());
                }
                structuredMessage = map;
            } catch (ClassCastException e) {
                // FIXME: could give some sort of option here instead of just exploding.
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
