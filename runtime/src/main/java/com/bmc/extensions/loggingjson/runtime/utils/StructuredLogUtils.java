package com.bmc.extensions.loggingjson.runtime.utils;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogFormat;
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

    /**
     * Adds additional fields from the configuration to the specified structured log if they are defined.
     *
     * @param structuredLog the structured log object to which additional fields will be added;
     *                      it must not be null. Its configuration is checked for any defined
     *                      additional fields at the top or wrapped level, and these are applied accordingly.
     */
    public static void addAdditionalFieldsIfAny(final StructuredLog structuredLog) {

        final JsonConfig jsonConfig = structuredLog.getJsonConfig();

        if (!jsonConfig.additionalFieldsTop().isEmpty()) {
            structuredLog.setAdditionalFieldsTop(new LinkedHashMap<>());
            structuredLog.getAdditionalFieldsTop().putAll(jsonConfig.additionalFieldsTop());
        }

        if (!jsonConfig.additionalFieldsWrapped().isEmpty()) {
            structuredLog.setAdditionalFieldsWrapped(new LinkedHashMap<>());
            structuredLog.getAdditionalFieldsWrapped().putAll(jsonConfig.additionalFieldsWrapped());
        }
    }

    /**
     * Applies exclusions from the structured log configuration if any are defined.
     * <p>
     * This method checks the JSON configuration of the provided structured log for a set of excluded keys.
     * If excluded keys are present, they are removed from the core record mapping of the structured log.
     *
     * @param structuredLog the structured log instance that contains the core record mapping and JSON configuration;
     *                      cannot be null. If the JSON configuration specifies excluded keys, these keys will be removed
     *                      from the core record mapping.
     */
    public static void applyExclusionsIfAny(final StructuredLog structuredLog) {

        structuredLog.getJsonConfig()
                     .excludedKeys()
                     .ifPresent(excludedKeys -> excludedKeys.forEach(structuredLog.getCoreRecordMapping()::remove));
    }

    /**
     * Applies key overrides from the structured log's JSON configuration to the core record mapping if any are defined.
     * <p>
     * If key overrides are present, it replaces the original keys in the core record mapping with the new keys while maintaining
     * their associated value mappings.
     * <p>
     * This allows avoiding checking if a key has a different configured name every time a structured log is rendered.
     *
     * @param structuredLog the structured log instance containing the core record mapping and JSON configuration;
     *                      must not be null. The keyOverrides map in the JSON configuration is used to determine
     *                      which keys should be overridden in the core record mapping.
     */
    public static void applyOverridesIfAny(final StructuredLog structuredLog) {

        final Map<String, Function<ExtLogRecord, ?>> basicRecordMapping = structuredLog.getCoreRecordMapping();

        structuredLog.getJsonConfig()
                     .keyOverrides()
                     .forEach((oldKeyName, newKeyName) -> {
                         if (basicRecordMapping.containsKey(oldKeyName)) {
                             basicRecordMapping.put(newKeyName, basicRecordMapping.remove(oldKeyName));
                         }
                     });
    }

    /**
     * Configures the structured log instance timestamps formatting.
     * <p>
     * This method retrieves a timestamp formatting pattern from the structured log's configuration. If a
     * specific pattern is provided, it is used to create a {@link DateTimeFormatter}. Otherwise, a default
     * formatting pattern is applied.
     *
     * @param structuredLog the structured log instance to configure; must not be null. This instance is
     *                      updated with a timestamp formatting function in its core record mapping based
     *                      on the log date-time formatting configuration.
     */
    public static void setStructuredLogInstantFormatting(final StructuredLog structuredLog) {

        final String                         logInstantPattern       = structuredLog.getJsonConfig().logDateTimeFormat().orElse(null);
        final DateTimeFormatter              logFormatter            = getDateTimeFormatterWithZone(logInstantPattern, structuredLog.getJsonConfig());
        final Function<ExtLogRecord, String> formatedInstantFunction = extLogRecord -> logFormatter.format(extLogRecord.getInstant());

        structuredLog.getCoreRecordMapping().replace(TIMESTAMP.getValue(), formatedInstantFunction);
    }

    /**
     * Updates the provided JSON configuration when the log format is set to ECS (Elastic Common Schema).
     *
     * @param jsonConfig the JSON configuration to be updated.
     *                   This must not be null, and its log format must be set to ECS to apply the updates.
     */
    public static void updateConfigIfLogFormatIsECS(final JsonConfig jsonConfig) {

        if (!jsonConfig.logFormat().equals(LogFormat.ECS)) {
            return;
        }

        final Map<String, String> keyOverrides      = jsonConfig.keyOverrides();
        final Map<String, String> excExtraFieldsTop = jsonConfig.additionalFieldsTop();
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

        final Set<String> excludedKeys = jsonConfig.excludedKeys().orElseGet(HashSet::new);
        excludedKeys.add(LOGGER_CLASS_NAME.getValue());
    }

}
