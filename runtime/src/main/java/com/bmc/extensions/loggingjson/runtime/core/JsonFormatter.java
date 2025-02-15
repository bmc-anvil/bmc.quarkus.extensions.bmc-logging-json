package com.bmc.extensions.loggingjson.runtime.core;

import java.util.HashMap;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.fasterxml.jackson.core.JsonFactory;

import org.jboss.logmanager.ExtFormatter;
import org.jboss.logmanager.ExtLogRecord;

import static com.bmc.extensions.loggingjson.runtime.core.StructuredLogWriter.formatRecord;
import static com.bmc.extensions.loggingjson.runtime.utils.StructuredLogDataUtils.*;

/**
 * Responsible for formatting log records into a JSON representation.
 * <p>
 * This class will get every component it requires already preconfigured in previous steps.
 * <p>
 * This is the class that does the actual JSON serializing of structured objects on client applications.
 *
 * @author BareMetalCode
 */
public class JsonFormatter extends ExtFormatter {

    private final JsonConfig    jsonConfig;
    private final JsonFactory   jsonFactory;
    private final StructuredLog structuredLog;

    public JsonFormatter(final JsonConfig jsonConfig, final StructuredLog structuredLog, final JsonFactory jsonFactory) {
        this.jsonFactory   = jsonFactory;
        this.jsonConfig    = jsonConfig;
        this.structuredLog = structuredLog;
    }

    /**
     * Formats the provided log record into a JSON representation by populating various fields
     * and finally printing the {@link StructuredLog} template.
     * <p>
     * Populating the different fields is conditional to their existence or particular conditions.<br>
     * I.e., Exceptions are only populated if they exist.
     *
     * @param record the {@link ExtLogRecord} to format.
     *
     * @return a JSON string representation {@link ExtLogRecord}.
     */
    @Override
    public String format(final ExtLogRecord record) {
        final Map<String, Object> fieldsToPrint = new HashMap<>();

        populateCoreFields(record, structuredLog, fieldsToPrint);
        populateAdditionalFieldsIfAny(structuredLog, fieldsToPrint);
        populateDetailsIfConfigured(record, structuredLog, fieldsToPrint, jsonConfig.printDetails());
        populateExceptionIfPresent(record, structuredLog, fieldsToPrint);

        return formatRecord(fieldsToPrint, jsonFactory, jsonConfig);
    }

}
