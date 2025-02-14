package com.bmc.extensions.loggingjson.runtime.utils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogFormat;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKeyECS;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logmanager.ExtLogRecord;

import static com.bmc.extensions.loggingjson.runtime.infrastructure.utils.DateTimeUtils.getDateTimeFormatterWithZone;
import static com.bmc.extensions.loggingjson.runtime.models.enums.ExtraECSValues.*;
import static com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey.LOGGER_CLASS_NAME;
import static com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey.TIMESTAMP;

/**
 * Utility class to build the different parts of a {@link StructuredLog}.
 * <p>
 * The name of each method self describes what it does, and there is no special intention in the code to be explained.
 *
 * @author BareMetalCode
 */
public class StructuredLogUtils {

    private StructuredLogUtils() {}

    public static void addAdditionalFieldsIfAny(final JsonConfig jsonConfig, final StructuredLog structuredLog) {

        if (!jsonConfig.additionalFieldsTop.isEmpty()) {
            structuredLog.setAdditionalFieldsTop(new HashMap<>());
            structuredLog.getAdditionalFieldsTop().putAll(jsonConfig.additionalFieldsTop);
        }

        if (!jsonConfig.additionalFieldsWrapped.isEmpty()) {
            structuredLog.setAdditionalFieldsWrapped(new HashMap<>());
            structuredLog.getAdditionalFieldsWrapped().putAll(jsonConfig.additionalFieldsWrapped);
        }
    }

    public static void applyExclusionsIfAny(final JsonConfig jsonConfig, final StructuredLog structuredLog) {
        jsonConfig.excludedKeys.ifPresent(excludedKeys -> excludedKeys.forEach(structuredLog.getBasicRecordMapping()::remove));
    }

    public static void applyOverridesIfAny(final Map<String, String> overrideMap, final StructuredLog structuredLog) {
        final Map<String, Function<ExtLogRecord, ?>> basicRecordMapping = structuredLog.getBasicRecordMapping();

        overrideMap.forEach((oldKeyName, newKeyName) -> {
            if (basicRecordMapping.containsKey(oldKeyName)) {
                basicRecordMapping.put(newKeyName, basicRecordMapping.remove(oldKeyName));
            }
        });
    }

    public static EnumMap<LogRecordKey, String> buildDefaultKeys() {
        final EnumMap<LogRecordKey, String> map = new EnumMap<>(LogRecordKey.class);

        for (final LogRecordKey logRecordKey : LogRecordKey.values()) {
            map.put(logRecordKey, logRecordKey.getValue());
        }

        return map;
    }

    public static void setStructuredLogInstantFormatting(final StructuredLog structuredLog, final JsonConfig jsonConfig) {
        final String                         logInstantFormatterPattern = jsonConfig.logDateTimeFormat.orElse(null);
        final DateTimeFormatter              logFormatter               = getDateTimeFormatterWithZone(logInstantFormatterPattern, jsonConfig);
        final Function<ExtLogRecord, String> formatedInstantFunction    = extLogRecord -> logFormatter.format(extLogRecord.getInstant());

        structuredLog.getBasicRecordMapping().replace(TIMESTAMP.getValue(), formatedInstantFunction);
    }

    public static void updateConfigIfLogFormatIsECS(final JsonConfig jsonConfig) {
        if (!jsonConfig.logFormat.equals(LogFormat.ECS)) {
            return;
        }

        final Map<String, String> keyOverrides      = jsonConfig.keyOverrides;
        final Map<String, String> excExtraFieldsTop = jsonConfig.additionalFieldsTop;
        final Config              quarkusConfig     = ConfigProvider.getConfig();

        final String ecsServiceName        = keyOverrides.getOrDefault(SERVICE_NAME.getValue(), SERVICE_NAME.getValue());
        final String ecsServiceVersion     = keyOverrides.getOrDefault(SERVICE_VERSION.getValue(), SERVICE_VERSION.getValue());
        final String ecsServiceEnvironment = keyOverrides.getOrDefault(SERVICE_ENV.getValue(), SERVICE_ENV.getValue());

        excExtraFieldsTop.putIfAbsent(ECS_VERSION.getEcsExtraField(), ECS_VERSION.getValue());
        excExtraFieldsTop.putIfAbsent(DATA_STREAM_TYPE.getEcsExtraField(), DATA_STREAM_TYPE.getValue());

        for (final LogRecordKeyECS logRecordKeyECS : LogRecordKeyECS.values()) {
            keyOverrides.putIfAbsent(logRecordKeyECS.getStandardValue(), logRecordKeyECS.getEcsValue());
        }

        quarkusConfig.getOptionalValue(SERVICE_NAME.getEcsExtraField(), String.class)
                     .ifPresent(additionalEcsField -> excExtraFieldsTop.putIfAbsent(ecsServiceName, additionalEcsField));

        quarkusConfig.getOptionalValue(SERVICE_VERSION.getEcsExtraField(), String.class)
                     .ifPresent(additionalField -> excExtraFieldsTop.putIfAbsent(ecsServiceVersion, additionalField));

        quarkusConfig.getOptionalValue(SERVICE_ENV.getEcsExtraField(), String.class)
                     .ifPresent(additionalField -> excExtraFieldsTop.putIfAbsent(ecsServiceEnvironment, additionalField));

        final Set<String> excludedKeys = jsonConfig.excludedKeys.orElseGet(HashSet::new);
        excludedKeys.add(LOGGER_CLASS_NAME.getValue());

    }

}
