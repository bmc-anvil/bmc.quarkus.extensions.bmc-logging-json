package com.bmc.extensions.loggingjson.runtime.models.enums;

import lombok.Getter;

/**
 * Enum representing fields used for structured logging in the ECS (Elastic Common Schema) log format.
 * <p>
 * Each enum constant maps a "standard" log field to its corresponding field as defined in the ECS specification.
 * <p>
 * The enum constants provide both:<br>
 * - A {@code standardValue}: representing the generic or default representation of a log field.<br>
 * - An {@code ecsValue}: representing the corresponding ECS-compliant field name.
 *
 * @author BareMetalCode
 */
@Getter
public enum LogRecordKeyECS {

    EXCEPTION_MESSAGE("message", "error.message"),
    EXCEPTION_REFERENCE_ID("refId", "event.id"),
    EXCEPTION_TYPE("exceptionType", "error.type"),
    HOSTNAME("hostname", "host.name"),
    LEVEL("level", "log.level"),
    LOGGER_NAME("loggerName", "log.logger"),
    PROCESS_ID("processId", "process.pid"),
    PROCESS_NAME("processName", "process.name"),
    SEQUENCE("sequence", "event.sequence"),
    STACK_TRACE("stackTrace", "error.stack_trace"),
    THREAD_ID("threadId", "process.thread.id"),
    THREAD_NAME("threadName", "process.thread.name"),
    TIMESTAMP("timestamp", "@timestamp");

    private final String ecsValue;
    private final String standardValue;

    LogRecordKeyECS(final String standardValue, final String ecsValue) {

        this.standardValue = standardValue;
        this.ecsValue      = ecsValue;
    }
}
