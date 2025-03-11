package com.bmc.extensions.loggingjson.runtime.models.factory;

import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonLogConfig;
import com.bmc.extensions.loggingjson.runtime.core.JsonFormatter;
import com.bmc.extensions.loggingjson.runtime.models.LogFunctionsMappings;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;

import static com.bmc.extensions.loggingjson.runtime.utils.StructuredLogUtils.*;

/**
 * Factory to create a {@link StructuredLog} that will act as a template for printing JSON Logs.
 * <p>
 * The goal is to return a minimum footprint {@link JsonFormatter} using Jackson as serializer.<br>
 * This is done by precomputing the fields to be printed according to {@link JsonLogConfig} into a map of {@link String}/{@link Function} to reduce
 * as much as possible testing the different decision paths for overrides, formatting, static keys, configs, etc.
 *
 * @author BareMetalCode
 */
public class StructuredLogFactory {

    private StructuredLogFactory() {}

    /**
     * Creates a precomputed {@link StructuredLog} instance that is optimized for speed / memory efficiency, to be used as a template for
     * rendering JSON logs.
     * <p>
     * This is the entry point for optimizations into building a structure that is as lightweight as possible and has everything needed to process a
     * Log from a given producer and generate a JSON parseable output.
     * <p>
     * This factory should produce a structure that will have the narrowest selection of fields to print, with precomputed name overrides, additional
     * static keys, etc.<br>
     * This approach should hopefully help to reduce output evaluation on the client app side to a maximum.
     *
     * @param jsonConfig the {@link JsonConfig} containing settings for building the structured log
     *
     * @return a {@link StructuredLog} instance that has been configured with precomputed mappings for record keys,
     * basic record fields, exception handling, optional detailed mappings, and additional fields, based on the provided configuration.
     */
    public static StructuredLog getPrecomputedStructuredLog(final JsonConfig jsonConfig) {

        final LogFunctionsMappings logFunctionsMappings = new LogFunctionsMappings();
        final StructuredLog        structuredLog        = new StructuredLog();

        structuredLog.setRecordKeys(buildDefaultKeys());
        structuredLog.setCoreRecordMapping(logFunctionsMappings.getBasicRecordMapping());
        structuredLog.setExceptionMapping(logFunctionsMappings.getExceptionMapping());
        structuredLog.setDetailsMapping(jsonConfig.printDetails() ? logFunctionsMappings.getDetailsMapping() : null);
        setStructuredLogInstantFormatting(structuredLog, jsonConfig);

        applyExclusionsIfAny(jsonConfig, structuredLog);
        applyOverridesIfAny(jsonConfig.keyOverrides(), structuredLog);
        updateConfigIfLogFormatIsECS(jsonConfig);
        addAdditionalFieldsIfAny(jsonConfig, structuredLog);

        return structuredLog;
    }

}
