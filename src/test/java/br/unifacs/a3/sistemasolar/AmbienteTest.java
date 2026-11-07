package br.unifacs.a3.sistemasolar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Teste de fumaça do ambiente (SS-01): garante que o JUnit roda, que a versão do Java é
 * a esperada e que as classes do JOGL estão no classpath. Não cria contexto OpenGL.
 */
class AmbienteTest {

    @Test
    void javaEh17OuSuperior() {
        assertTrue(Runtime.version().feature() >= 17,
                "Java 17+ é necessário, encontrado: " + Runtime.version());
    }

    @Test
    void joglEstaNoClasspath() {
        assertDoesNotThrow(() -> Class.forName("com.jogamp.opengl.GLProfile", false,
                getClass().getClassLoader()));
        assertDoesNotThrow(() -> Class.forName("com.jogamp.opengl.awt.GLCanvas", false,
                getClass().getClassLoader()));
    }
}
