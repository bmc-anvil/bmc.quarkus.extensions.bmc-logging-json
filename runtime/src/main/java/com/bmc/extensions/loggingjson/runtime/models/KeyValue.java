package com.bmc.extensions.loggingjson.runtime.models;

import lombok.Getter;

@Getter
public final class KeyValue {

    private final String key;
    private final Object value;

    private KeyValue(final String key, final Object value) {

        this.key   = key;
        this.value = value;
    }

    public static KeyValue of(final String key, final Object value) {

        return new KeyValue(key, value);
    }

}
