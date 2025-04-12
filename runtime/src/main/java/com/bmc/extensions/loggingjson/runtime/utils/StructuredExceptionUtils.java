package com.bmc.extensions.loggingjson.runtime.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;

import org.jboss.logmanager.ExtLogRecord;

import lombok.Getter;

import static org.jboss.logmanager.formatters.StackTraceFormatter.renderStackTrace;

public class StructuredExceptionUtils {

    private StructuredExceptionUtils() {

    }

    public static String printClassicStackTrace(final ExtLogRecord record, final StructuredLog structuredLog) {

        final StringBuilder writer = new StringBuilder();
        writer.append(record.getThrown().getMessage());
        renderStackTrace(writer, record.getThrown(), structuredLog.getJsonConfig().exceptions().exceptionSTSuppressedDepth());
        return writer.toString();
    }

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

    @Getter
    public static class CML {

        String className;
        int    line;
        String method;

    }

}
