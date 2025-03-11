package com.bmc.extensions.loggingjson.deployment;

import com.bmc.extensions.loggingjson.runtime.LoggingJsonRecorder;
import com.bmc.extensions.loggingjson.runtime.config.properties.JsonLogConfig;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogConsoleFormatBuildItem;
import io.quarkus.deployment.builditem.LogFileFormatBuildItem;

import static com.bmc.extensions.loggingjson.runtime.models.enums.LogOutput.CONSOLE;
import static com.bmc.extensions.loggingjson.runtime.models.enums.LogOutput.FILE;

/**
 * {@link  BuildStep} class responsible for configuring and enabling JSON log formatters
 * <p>
 * The configuration depends on the JSON-related settings provided through the application's configuration files.
 *
 * @author BareMetalCode
 */
public class LoggingJsonStepBuilder {

    private static final String FEATURE = "bmc-logging-json";

    @BuildStep
    FeatureBuildItem feature() {

        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    LogConsoleFormatBuildItem setUpConsoleFormatter(final LoggingJsonRecorder recorder, final JsonLogConfig config) {

        return new LogConsoleFormatBuildItem(recorder.getJsonFormatterForLogOutputType(config, CONSOLE));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    LogFileFormatBuildItem setUpFileFormatter(final LoggingJsonRecorder recorder, final JsonLogConfig config) {

        return new LogFileFormatBuildItem(recorder.getJsonFormatterForLogOutputType(config, FILE));
    }

}
