package id.ac.ui.cs.advprog.authjwt.config;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeneralUtilsTest {

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<GeneralUtils> constructor = GeneralUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        GeneralUtils instance = constructor.newInstance();
        assertNotNull(instance);
    }
}
