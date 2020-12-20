package DI_Pack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;

import static org.assertj.core.api.Assertions.assertThat;


class InterfaceTest {

    interface First {
    }

    interface Second {
    }

    public static class FirstExample implements First {
    }

    public static class SecondExample implements First, Second {
    }

    @Singleton interface CheckIfSingleton {
    }

    public static class NonSingleton implements CheckIfSingleton {
    }

    private DI_Class DI_Class;

    @BeforeEach
    void setUp() {
        DI_Class = new DI_Class();
    }

    @Test
    void Bind() {
        DI_Class.bindInterface(First.class, FirstExample.class);

        final First instance = DI_Class.Instant(First.class);
        assertThat(instance).isNotNull().isInstanceOf(FirstExample.class);
    }

    @Test
    void Override() {
        DI_Class.bindInterface(First.class, FirstExample.class);
        DI_Class.bindInterface(First.class, SecondExample.class);

        final First instance = DI_Class.Instant(First.class);

        assertThat(instance).isNotNull().isInstanceOf(SecondExample.class);
    }

    @Test
    void interfaces_not_same() {
        DI_Class.bindInterface(CheckIfSingleton.class, NonSingleton.class);

        final CheckIfSingleton firstInstance = DI_Class.Instant(CheckIfSingleton.class);
        final CheckIfSingleton secondInstance = DI_Class.Instant(CheckIfSingleton.class);

        assertThat(firstInstance).isNotSameAs(secondInstance);
    }
}