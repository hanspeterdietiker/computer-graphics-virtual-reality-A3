package br.unifacs.a3.sistemasolar.exception;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AwtInternalsNotOpenExceptionTest {

    @Test
    void messageTellsWhichFlagIsRequired() {
        String message = new AwtInternalsNotOpenException().getMessage();
        assertTrue(message.contains(AwtInternalsNotOpenException.REQUIRED_FLAG), message);
    }

    @Test
    void throwIfNotOpenMatchesModuleAccess() {
        if (AwtInternalsNotOpenException.isAwtInternalsOpen()) {
            assertDoesNotThrow(AwtInternalsNotOpenException::throwIfNotOpen);
        } else {
            assertThrows(AwtInternalsNotOpenException.class, AwtInternalsNotOpenException::throwIfNotOpen);
        }
    }
}
