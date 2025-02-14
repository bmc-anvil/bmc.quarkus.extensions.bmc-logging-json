package com.bmc.extensions.loggingjson.runtime.models.enums;

import lombok.Getter;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
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
