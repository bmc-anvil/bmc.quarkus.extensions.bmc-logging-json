package com.bmc.extensions.loggingjson.runtime.config;

import com.bmc.extensions.loggingjson.runtime.models.enums.StackTraceDetail;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

/**
 * Exception Configuration Section
 *
 * @author BareMetalCode
 */
@ConfigGroup
public interface ExceptionConfig {

    /**
     * The detail in which exception {@link StackTraceElement} will be printed inside the exception field.<br>
     * defaults to {@link StackTraceDetail#ONE_LINER}.
     * <p>
     * Options are:
     * <p>
     * {@link StackTraceDetail#ONE_LINER}: each element is printed in a single line.<br>
     * {@link StackTraceDetail#CLASS_METHOD_LINE}: each element is printed as an entry showing Class-Method-Line info.<br>
     * {@link StackTraceDetail#FULL}: each element is printed as an entry with all available info.<br>
     * {@link StackTraceDetail#OFF}: no stack trace information is printed at all.<br>
     */
    @WithDefault("ONE_LINER")
    StackTraceDetail exceptionDetail();

    /**
     * Depth of the Suppressed StackTrace to print if any is present.
     * <p>
     * Defaults to 0
     */
    @WithDefault("0")
    int exceptionSTSuppressedDepth();

    /**
     * Controls if the stacktrace is shown as a separate field<br>
     * defaults to {@code true}
     */
    @WithDefault("true")
    boolean exceptionStackTraceAsTopField();
}
