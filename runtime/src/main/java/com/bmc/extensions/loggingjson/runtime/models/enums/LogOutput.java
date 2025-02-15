package com.bmc.extensions.loggingjson.runtime.models.enums;

/**
 * Enum representing the output targets for log messages.
 * <p>
 * Currently supported targets for log output:<br>
 * - {@code CONSOLE}: Log messages are directed to standard console output.<br>
 * - {@code FILE}: Log messages are written to a file.
 *
 * @author BareMetalCode
 */
public enum LogOutput {
    CONSOLE,
    FILE
}
