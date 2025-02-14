package com.bmc.extensions.loggingjson.runtime.core;

import java.util.HashMap;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.fasterxml.jackson.core.JsonFactory;

import org.jboss.logmanager.ExtFormatter;
import org.jboss.logmanager.ExtLogRecord;

import static com.bmc.extensions.loggingjson.runtime.core.StructuredLogWriter.renderStructuredLog;
import static com.bmc.extensions.loggingjson.runtime.utils.StructuredLogDataUtils.*;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
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

    @Override
    public String format(final ExtLogRecord record) {
        final Map<String, Object> fieldsToRender = new HashMap<>();

        populateBasicRecordFields(record, structuredLog, fieldsToRender);
        populateAdditionalFields(structuredLog, fieldsToRender);
        populateRecordDetails(record, structuredLog, fieldsToRender, jsonConfig.printDetails);
        populateRecordException(record, structuredLog, fieldsToRender);

        return renderStructuredLog(fieldsToRender, jsonFactory, jsonConfig);
    }

}
