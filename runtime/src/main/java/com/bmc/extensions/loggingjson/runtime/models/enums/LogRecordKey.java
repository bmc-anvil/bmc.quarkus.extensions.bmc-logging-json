package com.bmc.extensions.loggingjson.runtime.models.enums;

import lombok.Getter;

/**
 * Enum representing the keys used in structured log records.
 *
 * @author BareMetalCode
 */
@Getter
public enum LogRecordKey {

    DETAILS("details"),
    EXCEPTION("exception"),
    EXCEPTION_CAUSED_BY("causedBy"),
    EXCEPTION_CIRCULAR_REFERENCE("circularReference"),
    EXCEPTION_FRAME("frame"),
    EXCEPTION_FRAMES("frames"),
    EXCEPTION_FRAME_CLASS("class"),
    EXCEPTION_FRAME_LINE("line"),
    EXCEPTION_FRAME_METHOD("method"),
    EXCEPTION_MESSAGE("message"),
    EXCEPTION_REFERENCE_ID("refId"),
    EXCEPTION_SUPPRESSED("suppressed"),
    EXCEPTION_TYPE("exceptionType"),
    HOSTNAME("hostname"),
    LEVEL("level"),
    LOGGER_CLASS_NAME("loggerClassName"),
    LOGGER_NAME("loggerName"),
    MDC("mdc"),
    MESSAGE("message"),
    NDC("ndc"),
    PARAMETERS("parameters"),
    PROCESS_ID("processId"),
    PROCESS_NAME("processName"),
    SEQUENCE("sequence"),
    SOURCE_CLASS_NAME("sourceClassName"),
    SOURCE_FILE_NAME("sourceFileName"),
    SOURCE_LINE_NUMBER("sourceLineNumber"),
    SOURCE_METHOD_NAME("sourceMethodName"),
    SOURCE_MODULE_NAME("sourceModuleName"),
    STACK_TRACE("stackTrace"),
    THREAD_ID("threadId"),
    THREAD_NAME("threadName"),
    THROWN("thrown"),
    TIMESTAMP("timestamp");

    private final String value;

    LogRecordKey(final String value) {
        this.value = value;
    }

}
