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
public class CustomSerializer {

    /**
     * Fully Qualified Class Name of the serializer to inject.
     * <p>
     * example: {@code com.test.my.package.MySerializer}
     */
    @ConfigItem
    public String className;

}
