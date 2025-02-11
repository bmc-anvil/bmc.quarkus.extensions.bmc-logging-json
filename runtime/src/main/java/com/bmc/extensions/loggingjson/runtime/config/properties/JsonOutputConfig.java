package com.bmc.extensions.loggingjson.runtime.config.properties;

import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Configuration for JSON log formatting.
 */
@ConfigRoot(name = "log",
            phase = ConfigPhase.RUN_TIME)
public class JsonOutputConfig {

    /**
     * Console logging.
     */
    @ConfigDocSection
    @ConfigItem(name = "console.json")
    public JsonConfig consoleJson;

    /**
     * File logging.
     */
    @ConfigDocSection
    @ConfigItem(name = "file.json")
    public JsonConfig fileJson;

}
