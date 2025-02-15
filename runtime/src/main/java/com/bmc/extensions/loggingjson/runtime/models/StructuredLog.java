package com.bmc.extensions.loggingjson.runtime.models;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey;

import org.jboss.logmanager.ExtLogRecord;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a structured logging model that provides mappings and configurations for log records.
 * <p>
 * This Model is the template that will be used at runtime to extract only relevant information from a LogRecord
 * <p>
 * It plays a central role in optimizing the data extraction, field names, etc., as before being used, it's heavily modified to contain only the data
 * required by the end user via configuration.
 * <p>
 * This model reduces querying configuration for naming overrides, formats details etc. as everything will be precomputed and stored into this model.
 * <p>
 * Each one of the xxxMapping fields associates a top level log key with a function that will extract that corresponding data from a
 * {@link ExtLogRecord }
 * <p>
 * Fields:<br>
 * - {@code additionalFieldsTop}: Custom fields to be included at the top level of a structured log.<br>
 * - {@code additionalFieldsWrapped}: Custom fields structured in a wrapped format within the log.<br>
 * - {@code coreRecordMapping}: A mapping to extract the most basic log record data.<br>
 * - {@code detailsMapping}: A mapping to extract additional details from log record.<br>
 * - {@code exceptionMapping}: A mapping to extract exception-related details.<br>
 * - {@code recordKeys}: A mapping of log record keys associated with their string representations (possible overridden), using {@link LogRecordKey}.
 *
 * @author BareMetalCode
 */
@Getter
@Setter
public class StructuredLog {

    private Map<String, Object>                    additionalFieldsTop;
    private Map<String, Object>                    additionalFieldsWrapped;
    private Map<String, Function<ExtLogRecord, ?>> coreRecordMapping;
    private Map<String, Function<ExtLogRecord, ?>> detailsMapping;
    private Map<String, Function<ExtLogRecord, ?>> exceptionMapping;
    private EnumMap<LogRecordKey, String>          recordKeys;

}
