package com.bmc.extensions.loggingjson.runtime.infrastructure.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import com.bmc.extensions.loggingjson.runtime.config.JsonConfig;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Utility class for configuring custom Jackson JSON serializers dynamically.
 * <p>
 * This class provides functionality to register user-defined custom serializers with an {@link ObjectMapper}, enabling flexible serializer
 * configurations.
 * <p>
 * This feature allows an application to modify the output of a given data type by creating its own serializer and injecting it via the application
 * configuration file by using its fully qualified name.
 * <p>
 * The custom serializers are expected to extend Jackson's {@link JsonSerializer} and provide a no-argument constructor.
 * <p>
 * NOTE: These serializers are intended to mainly affect the client objects but could have some consequences on the logger itself in the future if
 * the logger adds general purpose serializers that may be overridden here, these custom loggers will win.
 * <p>
 * Because reflection is expensive, this loading is done only once at application bootstrap time.
 *
 * @author BareMetalCode
 */
public class SerializerUtils {

    private SerializerUtils() {}

    /**
     * Registers custom serializers, if any are defined, to the provided {@link ObjectMapper} instance.
     * <br>
     * This method retrieves the custom serializers from the provided {@link JsonConfig}, dynamically loads them into
     * a {@link SimpleModule}, and registers the module with the given {@link ObjectMapper}.
     * <br>
     * Note: The custom serializers must be fully qualified class names of classes that extend {@link JsonSerializer}
     * and must have a no-argument constructor.
     *
     * @param jsonConfig the configuration source containing client-defined custom serializers
     * @param mapper the {@link ObjectMapper} instance to which the custom serializers will be registered
     */
    public static void addCustomSerializersIfAny(final JsonConfig jsonConfig, final ObjectMapper mapper) {

        final Map<String, String> customSerializers = jsonConfig.clientSerializers().customSerializers();
        if (customSerializers == null || customSerializers.isEmpty()) {
            return;
        }

        final SimpleModule module            = new SimpleModule();
        final ClassLoader  threadClassLoader = Thread.currentThread().getContextClassLoader();

        customSerializers.values().stream()
                         .toList()
                         .forEach(serializerName -> instantiateAndAddSerializer(serializerName, threadClassLoader, module));

        mapper.registerModule(module);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void instantiateAndAddSerializer(final String serializerName, final ClassLoader threadClassLoader, final SimpleModule module) {

        try {
            final Class<?>                              serializer     = threadClassLoader.loadClass(serializerName);
            final ParameterizedType                     genericType    = (ParameterizedType) serializer.getGenericSuperclass();
            final Class<?>                              serializerType = (Class<?>) genericType.getActualTypeArguments()[0];
            final Constructor<? extends JsonSerializer> constructor    = serializer.asSubclass(JsonSerializer.class).getDeclaredConstructor();

            module.addSerializer(serializerType, constructor.newInstance());

        } catch (final Exception e) {
            // FIXME: decide to fail or report...
            System.err.printf("Failure Loading custom serializer [%s].\n"
                              + "Be sure to use the full class name including the package for your custom serializer.", serializerName);
        }
    }

}
