package com.bmc.extensions.loggingjson.runtime.models.enums;

import org.jboss.logmanager.formatters.StackTraceFormatter;

/**
 * Enum representing different levels of detail for printing {@link StackTraceElement}.
 * <p>
 * It is used to configure the verbosity of the exception details included in the log message exception field.
 */
public enum StackTraceDetail {

    /**
     * Each element is printed in a single line.
     */
    ONE_LINER,
    /**
     * Each element is printed as an entry showing Class-Method-Line info.
     */
    CML,
    /**
     * Classic formatting of stackTrace as per {@link StackTraceFormatter}
     */
    CLASSIC,
    /**
     * Each element is printed as an entry with all available info.
     */
    FULL,
    /**
     * no stack trace information is printed at all.
     */
    OFF
}
