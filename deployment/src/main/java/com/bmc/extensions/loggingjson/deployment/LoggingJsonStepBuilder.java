package com.bmc.extensions.loggingjson.deployment;

import com.bmc.extensions.loggingjson.runtime.LoggingJsonRecorder;

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
    LogConsoleFormatBuildItem setUpConsoleFormatter(final LoggingJsonRecorder recorder) {

        return new LogConsoleFormatBuildItem(recorder.getJsonFormatterForLogOutputType(CONSOLE));
    }

    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    LogFileFormatBuildItem setUpFileFormatter(final LoggingJsonRecorder recorder) {

        return new LogFileFormatBuildItem(recorder.getJsonFormatterForLogOutputType(FILE));
    }

}
