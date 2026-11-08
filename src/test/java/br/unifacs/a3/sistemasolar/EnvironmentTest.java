package br.unifacs.a3.sistemasolar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EnvironmentTest {

    @Test
    void javaIs17OrNewer() {
        assertTrue(Runtime.version().feature() >= 17,
                "Java 17+ é necessário, encontrado: " + Runtime.version());
    }

    @Test
    void joglIsOnClasspath() {
        assertDoesNotThrow(() -> Class.forName("com.jogamp.opengl.GLProfile", false,
                getClass().getClassLoader()));
        assertDoesNotThrow(() -> Class.forName("com.jogamp.opengl.awt.GLCanvas", false,
                getClass().getClassLoader()));
    }
}
