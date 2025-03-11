package com.bmc.extensions.loggingjson.runtime.models.enums;

import lombok.Getter;

/**
 * Enum representing additional fields and their corresponding values used in ECS (Elastic Common Schema) log format.
 *
 * @author BareMetalCode
 */
@Getter
public enum ExtraECSValues {

    SERVICE_NAME("quarkus.application.name", "service.name"),
    SERVICE_VERSION("quarkus.application.version", "service.version"),
    SERVICE_ENV("quarkus.profile", "service.environment"),
    ECS_VERSION("ecs.version", "1.12.2"),
    DATA_STREAM_TYPE("data_stream.type", "logs");

    private final String ecsExtraField;
    private final String value;

    ExtraECSValues(final String ecsExtraField, final String value) {

        this.ecsExtraField = ecsExtraField;
        this.value         = value;
    }
}
