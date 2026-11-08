package br.unifacs.a3.sistemasolar.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RendererTest {

    private static final long ONE_SECOND = 1_000_000_000L;
    private static final double TOLERANCE = 1e-9;

    @Test
    void firstFrameHasZeroDelta() {
        Renderer renderer = new Renderer();
        assertEquals(0.0, renderer.computeDeltaSeconds(123_456_789L), TOLERANCE);
    }

    @Test
    void deltaIsElapsedSecondsBetweenFrames() {
        Renderer renderer = new Renderer();
        long t0 = 5 * ONE_SECOND;
        renderer.computeDeltaSeconds(t0);
        assertEquals(1.0 / 60.0, renderer.computeDeltaSeconds(t0 + ONE_SECOND / 60), 1e-6);
        assertEquals(0.010, renderer.computeDeltaSeconds(t0 + ONE_SECOND / 60 + 10_000_000L), TOLERANCE);
    }

    @Test
    void deltaWorksWithNegativeNanoTime() {
        Renderer renderer = new Renderer();
        renderer.computeDeltaSeconds(-ONE_SECOND);
        assertEquals(0.1, renderer.computeDeltaSeconds(-ONE_SECOND + ONE_SECOND / 10), TOLERANCE);
    }

    @Test
    void deltaIsClampedWhenLoopStalls() {
        Renderer renderer = new Renderer();
        renderer.computeDeltaSeconds(0L);
        assertEquals(Renderer.MAX_DELTA_SECONDS, renderer.computeDeltaSeconds(3 * ONE_SECOND), TOLERANCE);
    }

    @Test
    void deltaIsNeverNegative() {
        Renderer renderer = new Renderer();
        renderer.computeDeltaSeconds(ONE_SECOND);
        assertEquals(0.0, renderer.computeDeltaSeconds(ONE_SECOND - 1_000L), TOLERANCE);
    }

    @Test
    void sixtyFramesAddUpToOneSecond() {
        Renderer renderer = new Renderer();
        double total = 0.0;
        for (int i = 0; i <= 60; i++) {
            total += renderer.computeDeltaSeconds(i * ONE_SECOND / 60);
        }
        assertEquals(1.0, total, 1e-6);
    }

    @Test
    void aspectRatioIsWidthOverHeight() {
        assertEquals(1280.0 / 720.0, Renderer.computeAspectRatio(1280, 720), TOLERANCE);
        assertEquals(0.5, Renderer.computeAspectRatio(400, 800), TOLERANCE);
    }

    @Test
    void aspectRatioDoesNotDivideByZeroWhenMinimized() {
        assertEquals(1280.0, Renderer.computeAspectRatio(1280, 0), TOLERANCE);
        assertEquals(1.0, Renderer.computeAspectRatio(0, 0), TOLERANCE);
    }
}
