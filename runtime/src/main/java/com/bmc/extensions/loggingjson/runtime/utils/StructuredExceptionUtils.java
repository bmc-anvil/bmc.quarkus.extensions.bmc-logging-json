package com.bmc.extensions.loggingjson.runtime.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.bmc.extensions.loggingjson.runtime.models.enums.StackTraceDetail;

import org.jboss.logmanager.ExtLogRecord;

import lombok.Getter;

import static org.jboss.logmanager.formatters.StackTraceFormatter.renderStackTrace;

/**
 * Utility class for formatting structured exception data.
 */
public class StructuredExceptionUtils {

    private StructuredExceptionUtils() {

    }

    /**
     * Generates a classic java stack trace from the provided log record and structured log configuration.
     *
     * @param record        the {@link ExtLogRecord} containing the exception details to be rendered
     * @param structuredLog the {@link StructuredLog} containing configuration for rendering the stack trace
     *
     * @return a string representation of the classic stack trace
     */
    public static String printClassicStackTrace(final ExtLogRecord record, final StructuredLog structuredLog) {

        final StringBuilder writer = new StringBuilder();
        writer.append(record.getThrown().getMessage());
        renderStackTrace(writer, record.getThrown(), structuredLog.getJsonConfig().exceptions().exceptionSTSuppressedDepth());
        return writer.toString();
    }

    /**
     * Processes and prints the stack trace information from the given log record based on the stack trace detail configuration
     * provided in the structured log.
     * <p>
     *
     * @param record        the {@link ExtLogRecord} containing the exception details, including the stack trace
     * @param structuredLog the {@link StructuredLog} containing the JSON configuration that specifies how the stack trace should be rendered
     *
     * @return an object representing the formatted stack trace, or {@code null} if the stack trace output is turned off
     *
     * @see StackTraceDetail
     */
    public static Object printInnerStackTrace(final ExtLogRecord record, final StructuredLog structuredLog) {

        final Throwable throwable = record.getThrown();

        return switch (structuredLog.getJsonConfig().exceptions().exceptionDetail()) {
            case ONE_LINER -> addOneLineStackTrace(throwable.getStackTrace());
            case CLASS_METHOD_LINE -> addCMLStackTrace(throwable.getStackTrace());
            case CLASSIC -> addClassicStackTrace(throwable, structuredLog.getJsonConfig());
            case FULL -> addFullStackTrace(throwable.getStackTrace());
            case OFF -> null;
        };
    }

    /**
     * Processes the inner exception details from the provided log record and structured log configuration
     * into a structured map containing key-value pairs based on the exception inner mapping.
     *
     * @param record        the {@code ExtLogRecord} containing the exception details
     * @param structuredLog the {@code StructuredLog} containing the configuration mapping for extracting exception details
     *
     * @return a map where the keys are the configured mappings and the values are the extracted data based on the log record
     */
    public static Map<String, Object> printStructuredException(final ExtLogRecord record, final StructuredLog structuredLog) {

        final Map<String, Object> map = new LinkedHashMap<>();
        structuredLog.getExceptionInnerMapping()
                     .forEach((key, dataExtractingFunction)
                                      -> map.put(key, dataExtractingFunction.apply(record, structuredLog)));
        return map;
    }

    private static Object addCMLStackTrace(final StackTraceElement[] stackTrace) {

        return Arrays.stream(stackTrace)
                     .map(stackTraceElement -> {
                         final CML cml = new CML();
                         cml.className = stackTraceElement.getClassName();
                         cml.line      = stackTraceElement.getLineNumber();
                         cml.method    = stackTraceElement.getMethodName();
                         return cml;
                     })
                     .toArray(CML[]::new);
    }

    private static String addClassicStackTrace(final Throwable throwable, final JsonConfig jsonConfig) {

        final StringBuilder writer = new StringBuilder();
        renderStackTrace(writer, throwable, jsonConfig.exceptions().exceptionSTSuppressedDepth());
        return writer.toString();
    }

    private static Object addFullStackTrace(final StackTraceElement[] stackTrace) {

        return stackTrace;
    }

    private static Object addOneLineStackTrace(final StackTraceElement[] stackTrace) {

        return Arrays.stream(stackTrace)
                     .map(StackTraceElement::toString)
                     .toList();
    }

    /**
     * CML stands for ClassMethodLine.
     * <p>
     * It encapsulates metadata about a specific code location, such as the class name, line number, and method name.
     */
    @Getter
    public static class CML {

        private String className;
        private int    line;
        private String method;

    }

}
