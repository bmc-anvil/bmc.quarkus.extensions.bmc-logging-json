package com.bmc.extensions.loggingjson.runtime.models;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey;

import org.jboss.logmanager.ExtLogRecord;

import lombok.Getter;
import lombok.Setter;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
@Getter
@Setter
public class StructuredLog {

    private Map<String, Object>                    additionalFieldsTop;
    private Map<String, Object>                    additionalFieldsWrapped;
    private Map<String, Function<ExtLogRecord, ?>> basicRecordMapping;
    private Map<String, Function<ExtLogRecord, ?>> detailsMapping;
    private Map<String, Function<ExtLogRecord, ?>> exceptionMapping;
    private EnumMap<LogRecordKey, String>          recordKeys;

}
