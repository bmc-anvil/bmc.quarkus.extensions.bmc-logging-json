package com.bmc.extensions.loggingjson.deployment;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonOutputConfig;
import com.bmc.extensions.loggingjson.runtime.core.LoggingJsonRecorder;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogConsoleFormatBuildItem;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogOutput.CONSOLE;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
public class LoggingJsonProcessor {

    private static final String FEATURE = "bmc-logging-json";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    LogConsoleFormatBuildItem setUpConsoleFormatter(final LoggingJsonRecorder recorder, final JsonOutputConfig config) {
        return new LogConsoleFormatBuildItem(recorder.getJsonFormatterForLogOutputType(config, CONSOLE));
    }

}
