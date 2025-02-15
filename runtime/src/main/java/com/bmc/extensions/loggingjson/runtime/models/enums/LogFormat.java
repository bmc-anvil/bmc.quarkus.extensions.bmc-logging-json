package com.bmc.extensions.loggingjson.runtime.models.enums;

/**
 * Enum representing the supported log format types.
 * <p>
 * The following formats are currently supported:<br>
 * - {@code DEFAULT}: This format represents a standard log output format which may rely on a generic or application-specific structure.<br>
 * - {@code ECS}: This format adheres to the Elastic Common Schema (ECS), which is designed to standardize log data formatting and improve integration
 * with tools like the Elasticsearch ecosystem.
 *
 * @author BareMetalCode
 */
public enum LogFormat {
    DEFAULT,
    ECS
}
