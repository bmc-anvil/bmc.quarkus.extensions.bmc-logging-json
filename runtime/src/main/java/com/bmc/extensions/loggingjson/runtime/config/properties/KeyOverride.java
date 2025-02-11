package com.bmc.extensions.loggingjson.runtime.config.properties;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
@ConfigGroup
public class KeyOverride {

    /**
     * Overriding value for the given key.
     */
    @ConfigItem
    public String override;

}
