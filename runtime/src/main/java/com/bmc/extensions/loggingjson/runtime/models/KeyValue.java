package com.bmc.extensions.loggingjson.runtime.models;

import java.util.AbstractMap.SimpleImmutableEntry;

import lombok.Getter;

/**
 * FIXME: this could be replaced by {@link SimpleImmutableEntry}.
 */
@Getter
public final class KeyValue {

    private final String key;
    private final Object value;

    private KeyValue(final String key, final Object value) {

        this.key   = key;
        this.value = value;
    }

    /**
     * probably remove considering class FIXME above.
     *
     * @param key   placeholder
     * @param value placeholder
     *
     * @return placeholder
     */
    public static KeyValue of(final String key, final Object value) {

        return new KeyValue(key, value);
    }

}
