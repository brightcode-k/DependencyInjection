package DI_Pack;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class DI_Class {

    private final Set<Class> neededClass = new HashSet<>();
    private final Set<Class> instantiableClasses = new HashSet<>();
    private final Set<Class> singletonClasses = new HashSet<>();
    private final Map<Class, Object> singletonInstance = new HashMap<>();
    private final Map<Class, Class> interfaceMappings = new HashMap<>();
    private final Map<Class, Provider> providedClass = new HashMap<>();


    private <T> T NewInstant(Class<T> type) {
        final Constructor<T> constructor = findConstructor(type);
        final Parameter[] parameters = constructor.getParameters();
        final List<Object> arguments = Arrays.stream(parameters).map(param -> {
            if (param.getType().equals(Provider.class)) {
                return ProviderArgument(param);
            } else {
                return Instant(param.getType());
            }
        }).collect(Collectors.toList());
        try {
            final T newInstance = constructor.newInstance(arguments.toArray());
            markAsInstantiable(type);
            if (type.isAnnotationPresent(Singleton.class) || singletonClasses.contains(type)) {
                singletonInstance.put(type, newInstance);
            }
            return newInstance;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public <T> void bindInterface(Class<T> interfaceType, Class<? extends T> implementationType) {
        interfaceMappings.put(interfaceType, implementationType);
    }

    public <T> void bindProvider(Class<T> classType, Provider<T> provider) {
        providedClass.put(classType, provider);
    }

    public <T> void bindInstance(Class<T> classType, T instance) {
        bindProvider(classType, () -> instance);
    }

    private Provider ProviderArgument(Parameter param) {
        ParameterizedType typeParam = (ParameterizedType) param.getParameterizedType();
        final Type providerType = typeParam.getActualTypeArguments()[0];
        return () -> DI_Class.this.Instant((Class) providerType);
    }

    private void markAsInstantiable(Class type) {
        instantiableClasses.add(type);
    }

    @SuppressWarnings("unchecked")
    private <T> T ProviderInstant(Class<T> type) {
        final Provider<T> provider = providedClass.get(type);
        return provider.get();
    }

    @SuppressWarnings("unchecked")
    public <T> T Instant(Class<T> requestedType) {
        Class<T> type = requestedType;
        if (requestedType.isInterface()) {
            if (interfaceMappings.containsKey(requestedType)) {
                type = interfaceMappings.get(requestedType);
            } else if (providedClass.containsKey(requestedType)) {
                return ProviderInstant(requestedType);
            }
        }
        if (!requestedType.isInterface() && Modifier.isAbstract(requestedType.getModifiers())) {
            if (providedClass.containsKey(requestedType)) {
                return ProviderInstant(requestedType);
            }
        }
        neededClass.add(type);
        if (singletonInstance.containsKey(type)) {
            return (T) singletonInstance.get(type);
        }
        if (providedClass.containsKey(type)) {
            final T instanceFromProvider = ProviderInstant(type);
            markAsInstantiable(type);
            if (type.isAnnotationPresent(Singleton.class) || singletonClasses.contains(type)) {
                singletonInstance.put(type, instanceFromProvider);
            }
            return instanceFromProvider;
        }
        return NewInstant(type);
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> findConstructor(Class<T> type) {
        final Constructor<?>[] constructors = type.getConstructors();
        if (constructors.length > 1) {

            final List<Constructor<?>> constructorsWithInject = Arrays
                    .stream(constructors)
                    .filter(c -> c.isAnnotationPresent(Inject.class))
                    .collect(Collectors.toList());

            return (Constructor<T>) constructorsWithInject.get(0);
        } else {
            return (Constructor<T>) constructors[0];
        }
    }
}