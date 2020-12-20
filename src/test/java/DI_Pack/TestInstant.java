package DI_Pack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Test_Intstant {

    private static class CustomInterfaceExample implements CustomInterface {
    }

    interface CustomInterface {
    }

    static abstract class AbstractExample {
    }

    private DI_Class DI_Class;
    private CustomInterfaceExample interfaceExample;
    private AbstractExample abstractExample;

    @BeforeEach
    void setUp() {
        DI_Class = new DI_Class();
        interfaceExample = new CustomInterfaceExample();
        abstractExample = new AbstractExample(){};
    }

    @Test
    void same_instances_interface_implementations() {
        DI_Class.bindInstance(CustomInterfaceExample.class, interfaceExample);

        final CustomInterfaceExample instance = DI_Class.Instant(CustomInterfaceExample.class);

        assertThat(instance).isSameAs(interfaceExample);
    }

    @Test
    void same_interface_instances() {
        DI_Class.bindInstance(CustomInterface.class, interfaceExample);

        final CustomInterface instance = DI_Class.Instant(CustomInterface.class);

        assertThat(instance).isSameAs(interfaceExample);
    }
}