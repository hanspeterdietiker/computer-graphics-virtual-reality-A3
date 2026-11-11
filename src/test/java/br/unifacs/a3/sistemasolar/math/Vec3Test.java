package br.unifacs.a3.sistemasolar.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class Vec3Test {

    private static final float TOLERANCE = 1e-6f;

    @Test
    void addSumsComponents() {
        assertVec(new Vec3(5, 7, 9), new Vec3(1, 2, 3).add(new Vec3(4, 5, 6)));
    }

    @Test
    void subtractSubtractsComponents() {
        assertVec(new Vec3(-3, -3, -3), new Vec3(1, 2, 3).subtract(new Vec3(4, 5, 6)));
    }

    @Test
    void scaleMultipliesEveryComponent() {
        assertVec(new Vec3(2, -4, 6), new Vec3(1, -2, 3).scale(2));
    }

    @Test
    void dotIsSumOfProducts() {
        assertEquals(32f, new Vec3(1, 2, 3).dot(new Vec3(4, 5, 6)), TOLERANCE);
        assertEquals(0f, Vec3.UNIT_X.dot(Vec3.UNIT_Y), TOLERANCE);
    }

    @Test
    void crossFollowsRightHandRule() {
        assertVec(Vec3.UNIT_Z, Vec3.UNIT_X.cross(Vec3.UNIT_Y));
        assertVec(Vec3.UNIT_X, Vec3.UNIT_Y.cross(Vec3.UNIT_Z));
        assertVec(Vec3.UNIT_Y, Vec3.UNIT_Z.cross(Vec3.UNIT_X));
        assertVec(new Vec3(-3, 6, -3), new Vec3(1, 2, 3).cross(new Vec3(4, 5, 6)));
    }

    @Test
    void crossIsPerpendicularToBothOperands() {
        Vec3 a = new Vec3(1.5f, -2f, 0.5f);
        Vec3 b = new Vec3(-0.3f, 4f, 2f);
        Vec3 c = a.cross(b);
        assertEquals(0f, c.dot(a), 1e-5f);
        assertEquals(0f, c.dot(b), 1e-5f);
    }

    @Test
    void lengthIsEuclideanNorm() {
        assertEquals(5f, new Vec3(3, 4, 0).length(), TOLERANCE);
        assertEquals(0f, Vec3.ZERO.length(), TOLERANCE);
    }

    @Test
    void normalizeProducesUnitVectorWithSameDirection() {
        Vec3 n = new Vec3(3, 4, 0).normalize();
        assertVec(new Vec3(0.6f, 0.8f, 0), n);
        assertEquals(1f, new Vec3(-7, 2, 11).normalize().length(), TOLERANCE);
    }

    @Test
    void normalizeRejectsZeroVector() {
        assertThrows(ArithmeticException.class, Vec3.ZERO::normalize);
    }

    @Test
    void operationsDoNotMutateOperands() {
        Vec3 a = new Vec3(1, 2, 3);
        a.add(Vec3.UNIT_X);
        a.scale(10);
        a.normalize();
        assertVec(new Vec3(1, 2, 3), a);
    }

    private static void assertVec(Vec3 expected, Vec3 actual) {
        assertEquals(expected.x(), actual.x(), TOLERANCE, "x de " + actual);
        assertEquals(expected.y(), actual.y(), TOLERANCE, "y de " + actual);
        assertEquals(expected.z(), actual.z(), TOLERANCE, "z de " + actual);
    }
}
