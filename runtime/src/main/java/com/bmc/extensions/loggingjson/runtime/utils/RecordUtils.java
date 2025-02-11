package com.bmc.extensions.loggingjson.runtime.utils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import com.bmc.extensions.loggingjson.runtime.config.properties.AdditionalField;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.bmc.extensions.loggingjson.runtime.config.properties.KeyOverride;
import com.bmc.extensions.loggingjson.runtime.models.enums.EcsLogRecordKey;
import com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey;
import com.bmc.extensions.loggingjson.runtime.models.RecordTemplate;
import com.bmc.extensions.loggingjson.runtime.models.StructuredLog;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logmanager.ExtLogRecord;

import static com.bmc.extensions.loggingjson.runtime.utils.DateTimeUtils.getDateTimeFormatterWithZone;
import static com.bmc.extensions.loggingjson.runtime.models.enums.LogFormat.ECS;
import static com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey.LOGGER_CLASS_NAME;
import static com.bmc.extensions.loggingjson.runtime.models.enums.LogRecordKey.TIMESTAMP;
import static java.util.stream.Collectors.toMap;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
public class RecordUtils {

    private RecordUtils() {}

    private static void addAdditionalFields(final JsonConfig jsonConfig, final StructuredLog structuredLog) {

        // I do not want to instantiate a map I will not use in the final render that will live in memo for nothing
        if (!jsonConfig.additionalFieldsTop.isEmpty()) {
            structuredLog.setAdditionalFieldsTop(new HashMap<>());
            appendAdditionalFields(jsonConfig.additionalFieldsTop, structuredLog.getAdditionalFieldsTop());
        }

        if (!jsonConfig.additionalFieldsWrapped.isEmpty()) {
            structuredLog.setAdditionalFieldsWrapped(new HashMap<>());
            appendAdditionalFields(jsonConfig.additionalFieldsWrapped, structuredLog.getAdditionalFieldsWrapped());
        }
    }

    private static void appendAdditionalFields(final Map<String, AdditionalField> additionalFields, final Map<String, Object> targetMap) {
        targetMap.putAll(additionalFields.entrySet()
                                         .stream()
                                         .collect(toMap(Entry::getKey, entry -> entry.getValue().value)));
    }

    private static void applyEcsOverridesAndAdditionalFields(final JsonConfig jsonConfig) {
        for (final EcsLogRecordKey ecsLogRecordKey : EcsLogRecordKey.values()) {
            final KeyOverride keyOverride = new KeyOverride();
            keyOverride.override = ecsLogRecordKey.getEcsValue();
            jsonConfig.keyOverrides.putIfAbsent(ecsLogRecordKey.getStandardValue(), keyOverride);
        }

        jsonConfig.additionalFieldsTop.putIfAbsent("ecs.version", buildAdditionalField("1.12.2"));
        jsonConfig.additionalFieldsTop.putIfAbsent("data_stream.type", buildAdditionalField("logs"));

        final Config quarkusConfig = ConfigProvider.getConfig();
        quarkusConfig.getOptionalValue("quarkus.application.name", String.class)
                     .map(RecordUtils::buildAdditionalField)
                     .ifPresent(additionalField -> jsonConfig.additionalFieldsTop.putIfAbsent("service.name", additionalField));

        quarkusConfig.getOptionalValue("quarkus.application.version", String.class)
                     .map(RecordUtils::buildAdditionalField)
                     .ifPresent(additionalField -> jsonConfig.additionalFieldsTop.putIfAbsent("service.version", additionalField));

        quarkusConfig.getOptionalValue("quarkus.profile", String.class)
                     .map(RecordUtils::buildAdditionalField)
                     .ifPresent(additionalField -> jsonConfig.additionalFieldsTop.putIfAbsent("service.environment", additionalField));

        final Set<String> excludedKeys = jsonConfig.excludedKeys.orElseGet(HashSet::new);
        excludedKeys.add(LOGGER_CLASS_NAME.getValue());

    }

    private static void applyOverrides(final Map<String, KeyOverride> overrideMap, final StructuredLog structuredLog) {
        final Map<String, Function<ExtLogRecord, ?>> basicRecordMapping = structuredLog.getBasicRecordMapping();

        overrideMap.forEach((oldKeyName, newKeyName) -> {
            if (basicRecordMapping.containsKey(oldKeyName)) {
                basicRecordMapping.put(newKeyName.override, basicRecordMapping.remove(oldKeyName));
            }
        });
    }

    private static AdditionalField buildAdditionalField(final String additionalFieldValue) {
        final AdditionalField additionalField = new AdditionalField();
        additionalField.value = additionalFieldValue;
        return additionalField;
    }

    private static EnumMap<LogRecordKey, String> buildDefaultKeys() {
        final EnumMap<LogRecordKey, String> map = new EnumMap<>(LogRecordKey.class);

        for (final LogRecordKey logRecordKey : LogRecordKey.values()) {
            map.put(logRecordKey, logRecordKey.getValue());
        }

        return map;
    }

    private static void excludeFromDefaultLogRecord(final Set<String> excludedKeys, final StructuredLog structuredLog) {
        excludedKeys.forEach(structuredLog.getBasicRecordMapping()::remove);
    }

    // this should create an efficient default for a map of keys to be rendered
    // build a small as possible keymap to live in memory
    public static StructuredLog getRenderTemplate(final JsonConfig jsonConfig) {
        final RecordTemplate recordTemplate = new RecordTemplate();
        final StructuredLog  structuredLog  = new StructuredLog();

        structuredLog.setRecordKeys(buildDefaultKeys());
        structuredLog.setBasicRecordMapping(recordTemplate.getBasicRecordMapping());
        structuredLog.setExceptionMapping(recordTemplate.getExceptionMapping());
        if (jsonConfig.printDetails) {
            structuredLog.setDetailsMapping(recordTemplate.getDetailsMapping());
        }

        setStructuredLogInstantFormatting(structuredLog, jsonConfig);

        if (jsonConfig.logFormat.equals(ECS)) {
            applyEcsOverridesAndAdditionalFields(jsonConfig);
        }

        jsonConfig.excludedKeys.ifPresent(excludedKeys -> excludeFromDefaultLogRecord(excludedKeys, structuredLog));
        applyOverrides(jsonConfig.keyOverrides, structuredLog);
        addAdditionalFields(jsonConfig, structuredLog);

        return structuredLog;
    }

    // this, combined with injecting serializers, gives full flexibility on how date-time related fields are formatted to output
    // we only provide simple config for the log themselves then user can override their own objects as pleased
    // we can add default datetime config fields to avoid the long classnames
    // a property can look like quarkus.log.console.json.customizers.localDateTime: "pattern"
    // all loaded at bootstrap
    private static void setStructuredLogInstantFormatting(final StructuredLog structuredLog, final JsonConfig jsonConfig) {
        final String                         logInstantFormatterPattern = jsonConfig.logDateTimeFormat.orElse(null);
        final DateTimeFormatter              logFormatter               = getDateTimeFormatterWithZone(logInstantFormatterPattern, jsonConfig);
        final Function<ExtLogRecord, String> formatedInstantFunction    = extLogRecord -> logFormatter.format(extLogRecord.getInstant());

        structuredLog.getBasicRecordMapping().replace(TIMESTAMP.getValue(), formatedInstantFunction);
    }

}
