package br.unifacs.a3.sistemasolar.math;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class Mat4Test {

    private static final float TOLERANCE = 1e-5f;

    @Test
    void identityHasOnesOnDiagonal() {
        assertArrayEquals(new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        }, Mat4.identity().toArray(), TOLERANCE);
    }

    @Test
    void identityIsNeutralForMultiply() {
        Mat4 m = Mat4.translate(1, 2, 3).multiply(Mat4.rotateY(30)).multiply(Mat4.scale(2, 3, 4));
        assertArrayEquals(m.toArray(), Mat4.identity().multiply(m).toArray(), TOLERANCE);
        assertArrayEquals(m.toArray(), m.multiply(Mat4.identity()).toArray(), TOLERANCE);
    }

    @Test
    void identityKeepsPoint() {
        assertVec(new Vec3(4, -5, 6), Mat4.identity().transformPoint(new Vec3(4, -5, 6)));
    }

    @Test
    void translateIsStoredColumnMajor() {
        float[] m = Mat4.translate(7, 8, 9).toArray();
        assertEquals(7f, m[12], TOLERANCE);
        assertEquals(8f, m[13], TOLERANCE);
        assertEquals(9f, m[14], TOLERANCE);
        assertEquals(7f, Mat4.translate(7, 8, 9).get(0, 3), TOLERANCE);
    }

    @Test
    void translateMovesPoint() {
        assertVec(new Vec3(11, 22, 33), Mat4.translate(10, 20, 30).transformPoint(new Vec3(1, 2, 3)));
        assertVec(new Vec3(11, 22, 33), Mat4.translate(new Vec3(10, 20, 30)).transformPoint(new Vec3(1, 2, 3)));
    }

    @Test
    void translateDoesNotMoveDirection() {
        assertVec(Vec3.UNIT_X, Mat4.translate(10, 20, 30).transformDirection(Vec3.UNIT_X));
    }

    @Test
    void rotateY90TakesXToMinusZ() {
        assertVec(new Vec3(0, 0, -1), Mat4.rotateY(90).transformPoint(Vec3.UNIT_X));
    }

    @Test
    void rotateX90TakesYToZ() {
        assertVec(Vec3.UNIT_Z, Mat4.rotateX(90).transformPoint(Vec3.UNIT_Y));
    }

    @Test
    void rotateZ90TakesXToY() {
        assertVec(Vec3.UNIT_Y, Mat4.rotateZ(90).transformPoint(Vec3.UNIT_X));
    }

    @Test
    void rotationPreservesLength() {
        Vec3 p = new Vec3(3, -4, 12);
        Mat4 r = Mat4.rotateX(17).multiply(Mat4.rotateY(-63)).multiply(Mat4.rotateZ(140));
        assertEquals(p.length(), r.transformPoint(p).length(), TOLERANCE);
    }

    @Test
    void scaleMultipliesEachAxis() {
        assertVec(new Vec3(2, 6, 12), Mat4.scale(2, 3, 4).transformPoint(new Vec3(1, 2, 3)));
        assertVec(new Vec3(5, 10, 15), Mat4.scale(5).transformPoint(new Vec3(1, 2, 3)));
    }

    @Test
    void multiplyAppliesRightOperandFirst() {
        Mat4 rotateThenTranslate = Mat4.translate(10, 0, 0).multiply(Mat4.rotateY(90));
        assertVec(new Vec3(10, 0, -1), rotateThenTranslate.transformPoint(Vec3.UNIT_X));

        Mat4 translateThenRotate = Mat4.rotateY(90).multiply(Mat4.translate(10, 0, 0));
        assertVec(new Vec3(0, 0, -11), translateThenRotate.transformPoint(Vec3.UNIT_X));
    }

    @Test
    void orbitHierarchyPlacesBodyOnCircle() {
        Mat4 orbit = Mat4.rotateY(90).multiply(Mat4.translate(20, 0, 0));
        assertVec(new Vec3(0, 0, -20), orbit.transformPoint(Vec3.ZERO));
    }

    @Test
    void perspectiveMatchesGluPerspectiveReference() {
        assertArrayEquals(new float[] {
                1.3579951f, 0, 0, 0,
                0, 2.4142136f, 0, 0,
                0, 0, -1.0002000f, -1,
                0, 0, -0.2000200f, 0
        }, Mat4.perspective(45, 16f / 9f, 0.1f, 1000f).toArray(), TOLERANCE);

        assertArrayEquals(new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, -1.2222222f, -1,
                0, 0, -2.2222222f, 0
        }, Mat4.perspective(90, 1, 1, 10).toArray(), TOLERANCE);
    }

    @Test
    void perspectiveMapsNearAndFarPlanesToNdcLimits() {
        Mat4 p = Mat4.perspective(60, 1.5f, 0.5f, 200f);
        assertEquals(-1f, ndcDepth(p, -0.5f), TOLERANCE);
        assertEquals(1f, ndcDepth(p, -200f), TOLERANCE);
    }

    @Test
    void perspectiveRejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> Mat4.perspective(0, 1, 0.1f, 10));
        assertThrows(IllegalArgumentException.class, () -> Mat4.perspective(180, 1, 0.1f, 10));
        assertThrows(IllegalArgumentException.class, () -> Mat4.perspective(45, 0, 0.1f, 10));
        assertThrows(IllegalArgumentException.class, () -> Mat4.perspective(45, 1, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> Mat4.perspective(45, 1, 10, 10));
    }

    @Test
    void lookAtFromZFiveTowardOriginIsTranslationByMinusFive() {
        Mat4 view = Mat4.lookAt(new Vec3(0, 0, 5), Vec3.ZERO, Vec3.UNIT_Y);
        assertArrayEquals(Mat4.translate(0, 0, -5).toArray(), view.toArray(), TOLERANCE);
        assertVec(Vec3.ZERO, view.transformPoint(new Vec3(0, 0, 5)));
        assertVec(new Vec3(0, 0, -5), view.transformPoint(Vec3.ZERO));
    }

    @Test
    void lookAtPutsTargetOnNegativeZAxis() {
        Vec3 eye = new Vec3(30, 40, -12);
        Vec3 target = new Vec3(1, -2, 3);
        Vec3 inView = Mat4.lookAt(eye, target, Vec3.UNIT_Y).transformPoint(target);
        assertEquals(0f, inView.x(), TOLERANCE);
        assertEquals(0f, inView.y(), TOLERANCE);
        assertEquals(-target.subtract(eye).length(), inView.z(), 1e-4f);
    }

    @Test
    void lookAtRejectsUpParallelToViewDirection() {
        assertThrows(ArithmeticException.class, () -> Mat4.lookAt(new Vec3(0, 5, 0), Vec3.ZERO, Vec3.UNIT_Y));
    }

    @Test
    void toArrayReturnsDefensiveCopy() {
        Mat4 m = Mat4.identity();
        m.toArray()[0] = 42;
        assertEquals(1f, m.toArray()[0], TOLERANCE);
    }

    @Test
    void fromColumnMajorRoundTrips() {
        float[] values = new float[16];
        for (int i = 0; i < values.length; i++) {
            values[i] = i;
        }
        Mat4 m = Mat4.fromColumnMajor(values);
        values[0] = 99;
        assertEquals(0f, m.get(0, 0), TOLERANCE);
        assertEquals(4f, m.get(0, 1), TOLERANCE);
        assertEquals(1f, m.get(1, 0), TOLERANCE);
        assertThrows(IllegalArgumentException.class, () -> Mat4.fromColumnMajor(new float[15]));
    }

    private static float ndcDepth(Mat4 projection, float viewZ) {
        float[] m = projection.toArray();
        float clipZ = m[10] * viewZ + m[14];
        float clipW = m[11] * viewZ + m[15];
        return clipZ / clipW;
    }

    private static void assertVec(Vec3 expected, Vec3 actual) {
        assertEquals(expected.x(), actual.x(), TOLERANCE, "x de " + actual);
        assertEquals(expected.y(), actual.y(), TOLERANCE, "y de " + actual);
        assertEquals(expected.z(), actual.z(), TOLERANCE, "z de " + actual);
    }
}
