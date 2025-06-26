package com.bmc.extensions.loggingjson.runtime.models;

/**
 * Enum representing the position of a log entry within a structured log.
 * <p>
 * The available positions provide control over where log data is placed in the log structure.
 */
public enum LogEntryPosition {
    /**
     * Log entry will appear at the top level within the structured log.
     * <p>
     * i.e., at the same level as the rest of the fields.
     */
    TOP,
    /**
     * Log entry will appear within the "message" field of the structured log.
     */
    MSG_FIELD
}
