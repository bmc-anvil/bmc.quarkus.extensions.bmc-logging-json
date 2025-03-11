package com.bmc.extensions.loggingjson.runtime.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey.*;
import static org.jboss.logmanager.formatters.StackTraceFormatter.renderStackTrace;

public class StructuredExceptionUtils {

    private StructuredExceptionUtils() {

    }

    public static void printClassicStackTrace(final Throwable throwable, final Map<String, Object> fieldsToRender, final JsonConfig jsonConfig) {

        final StringBuilder writer = new StringBuilder();
        renderStackTrace(writer, throwable, jsonConfig.stackTraceSuppressedDepth());
        fieldsToRender.put("stackTrace", writer.toString());
    }

    public static void printStructuredException(final Throwable throwable, final Map<LogRecordKey, String> recordKeys,
            final Map<String, Object> fieldsToRender, final JsonConfig jsonConfig) {

        final Map<String, Object> exceptions = new HashMap<>();

        Map<Integer, String>      stackMap   = new HashMap<>();
        final StackTraceElement[] stackTrace = throwable.getStackTrace();
        String[] stackList = Arrays.stream(throwable.getStackTrace())
                                   .toList().stream()
                                   .map(StackTraceElement::toString)
                                   .toList().toArray(new String[0]);

        for (int i = 0; i < stackTrace.length; i++) {
            stackMap.put(i, stackTrace[i].toString());
        }

        exceptions.put(recordKeys.get(EXCEPTION_TYPE), throwable.getClass().getName());
        exceptions.put(recordKeys.get(EXCEPTION_MESSAGE), throwable.getMessage());
        // FIXME: add a formatted stacktrace
        //        exceptions.put(EXCEPTION_FRAMES, thrown.getStackTrace());
        exceptions.put(recordKeys.get(EXCEPTION_CAUSED_BY), throwable.getCause());
        //        exceptions.put("StackTrace", Arrays.toString(thrownException.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",", "\n"));
        //        exceptions.put("StackTrace", Arrays.asList(thrownException.getStackTrace()));
        exceptions.put("stackTraceMAP", stackMap);
        exceptions.put("stackTraceList", stackList);

        fieldsToRender.put("exceptions", exceptions);
    }

}
