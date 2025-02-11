package com.bmc.extensions.loggingjson.runtime.config.properties;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * Post additional fields. E.g. `fieldName1=value1,fieldName2=value2`.
 */
@ConfigGroup
public class AdditionalField {
    /**
     * Additional field value.
     */
    @ConfigItem
    public String value;

}
