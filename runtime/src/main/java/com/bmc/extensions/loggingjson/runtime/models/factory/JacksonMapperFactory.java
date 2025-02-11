package com.bmc.extensions.loggingjson.runtime.models.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import com.bmc.extensions.loggingjson.runtime.config.properties.JsonConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import static com.bmc.extensions.loggingjson.runtime.utils.DateTimeUtils.configureClientDateTime;

/**
 * FIXME: add documentation: focus on "description", "why", "how", "caveats"[...] more that simple descriptions, as those should be
 *        inferred from code and names as much as possible.
 *
 * @author BareMetalCode
 */
public class JacksonMapperFactory {

    private JacksonMapperFactory() {}

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addCustomSerializers(final List<String> customSerializer, final ObjectMapper mapper) {
        final SimpleModule module                   = new SimpleModule();
        final ClassLoader  threadContextClassLoader = Thread.currentThread().getContextClassLoader();

        for (final String serializerName : customSerializer) {
            try {
                final Class<?>                              serializer     = threadContextClassLoader.loadClass(serializerName);
                final ParameterizedType                     genericType    = (ParameterizedType) serializer.getGenericSuperclass();
                final Class<?>                              serializerType = (Class<?>) genericType.getActualTypeArguments()[0];
                final Constructor<? extends JsonSerializer> constructor    = serializer.asSubclass(JsonSerializer.class).getDeclaredConstructor();

                module.addSerializer(serializerType, constructor.newInstance());

            } catch (Exception e) {
                System.err.printf("Failure Loading custom serializer [%s].\n" +
                                  "Be sure to use the full class name including the package for your custom serializer.", serializerName);
            }
        }

        mapper.registerModule(module);
    }

    public static ObjectMapper getJacksonMapper(final JsonConfig jsonConfig) {
        final ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(configureClientDateTime(jsonConfig));

        Optional.ofNullable(jsonConfig.clientSerializers.customSerializers)
                .map(customSerializers -> customSerializers
                        .values()
                        .stream()
                        .map(customSerializer -> customSerializer.className)
                        .toList())
                .ifPresent(customSerializers -> addCustomSerializers(customSerializers, mapper));

        return mapper;
    }

}
