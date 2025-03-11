package com.bmc.extensions.loggingjson.runtime.models;

import lombok.Getter;

@Getter
public final class KV {

    private final String key;
    private final Object value;

    private KV(final String key, final Object value) {

        this.key   = key;
        this.value = value;
    }

    public static KV of(final String key, final Object value) {

        return new KV(key, value);
    }

}
