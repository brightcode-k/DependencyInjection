package DI_Pack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.assertThat;


class InstanceProviderTest {

    public interface InstanceProvider {
        <T> T Instant(Class<T> type);
    }

    public static class Example {
    }

    @Singleton
    public static class SingletonExample {
    }

    private DI_Class DI_Class;

    @BeforeEach
    void setUp() {
        DI_Class = new DI_Class();
    }

    @Test
    void instanceShouldNotBeNull() {
        DI_Class.bindProvider(InstanceProvider.class, () -> DI_Class::Instant);
        final InstanceProvider provider = DI_Class.Instant(InstanceProvider.class);

        final Example example = provider.Instant(Example.class);

        assertThat(example).isNotNull();
    }

    @Test
    void singletonsShouldBeSame() {
        DI_Class.bindProvider(InstanceProvider.class, () -> DI_Class::Instant);
        final InstanceProvider provider = DI_Class.Instant(InstanceProvider.class);

        final SingletonExample singleton1 = provider.Instant(SingletonExample.class);
        final SingletonExample singleton2 = provider.Instant(SingletonExample.class);
        final SingletonExample singleton3 = DI_Class.Instant(SingletonExample.class);

        assertThat(singleton1).isSameAs(singleton2);
        assertThat(singleton2).isSameAs(singleton3);
    }
}