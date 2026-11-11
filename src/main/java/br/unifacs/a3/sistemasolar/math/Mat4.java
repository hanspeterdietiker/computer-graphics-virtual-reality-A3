package br.unifacs.a3.sistemasolar.math;

import java.util.Arrays;

public final class Mat4 {

    private static final int SIZE = 4;
    private static final int ELEMENT_COUNT = SIZE * SIZE;

    private final float[] elements;

    private Mat4(float[] elements) {
        this.elements = elements;
    }

    public static Mat4 identity() {
        float[] m = new float[ELEMENT_COUNT];
        m[index(0, 0)] = 1f;
        m[index(1, 1)] = 1f;
        m[index(2, 2)] = 1f;
        m[index(3, 3)] = 1f;
        return new Mat4(m);
    }

    public static Mat4 fromColumnMajor(float[] values) {
        if (values.length != ELEMENT_COUNT) {
            throw new IllegalArgumentException("Mat4 exige 16 valores, recebeu " + values.length);
        }
        return new Mat4(values.clone());
    }

    public static Mat4 translate(float tx, float ty, float tz) {
        Mat4 result = identity();
        result.set(0, 3, tx);
        result.set(1, 3, ty);
        result.set(2, 3, tz);
        return result;
    }

    public static Mat4 translate(Vec3 offset) {
        return translate(offset.x(), offset.y(), offset.z());
    }

    public static Mat4 rotateX(double degrees) {
        double radians = Math.toRadians(degrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        Mat4 result = identity();
        result.set(1, 1, cos);
        result.set(1, 2, -sin);
        result.set(2, 1, sin);
        result.set(2, 2, cos);
        return result;
    }

    public static Mat4 rotateY(double degrees) {
        double radians = Math.toRadians(degrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        Mat4 result = identity();
        result.set(0, 0, cos);
        result.set(0, 2, sin);
        result.set(2, 0, -sin);
        result.set(2, 2, cos);
        return result;
    }

    public static Mat4 rotateZ(double degrees) {
        double radians = Math.toRadians(degrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);
        Mat4 result = identity();
        result.set(0, 0, cos);
        result.set(0, 1, -sin);
        result.set(1, 0, sin);
        result.set(1, 1, cos);
        return result;
    }

    public static Mat4 scale(float sx, float sy, float sz) {
        Mat4 result = identity();
        result.set(0, 0, sx);
        result.set(1, 1, sy);
        result.set(2, 2, sz);
        return result;
    }

    public static Mat4 scale(float uniform) {
        return scale(uniform, uniform, uniform);
    }

    public static Mat4 perspective(double fovYDegrees, double aspect, double near, double far) {
        if (fovYDegrees <= 0 || fovYDegrees >= 180) {
            throw new IllegalArgumentException("fovY deve estar em (0, 180): " + fovYDegrees);
        }
        if (aspect <= 0) {
            throw new IllegalArgumentException("aspect deve ser positivo: " + aspect);
        }
        if (near <= 0 || far <= near) {
            throw new IllegalArgumentException("exige 0 < near < far: near=" + near + ", far=" + far);
        }
        double f = 1.0 / Math.tan(Math.toRadians(fovYDegrees) / 2.0);
        float[] m = new float[ELEMENT_COUNT];
        m[index(0, 0)] = (float) (f / aspect);
        m[index(1, 1)] = (float) f;
        m[index(2, 2)] = (float) ((far + near) / (near - far));
        m[index(2, 3)] = (float) ((2.0 * far * near) / (near - far));
        m[index(3, 2)] = -1f;
        return new Mat4(m);
    }

    public static Mat4 lookAt(Vec3 eye, Vec3 target, Vec3 up) {
        Vec3 f = target.subtract(eye).normalize();
        Vec3 s = f.cross(up).normalize();
        Vec3 u = s.cross(f);

        Mat4 result = identity();
        result.set(0, 0, s.x());
        result.set(0, 1, s.y());
        result.set(0, 2, s.z());
        result.set(0, 3, -s.dot(eye));
        result.set(1, 0, u.x());
        result.set(1, 1, u.y());
        result.set(1, 2, u.z());
        result.set(1, 3, -u.dot(eye));
        result.set(2, 0, -f.x());
        result.set(2, 1, -f.y());
        result.set(2, 2, -f.z());
        result.set(2, 3, f.dot(eye));
        return result;
    }

    public Mat4 multiply(Mat4 other) {
        float[] m = new float[ELEMENT_COUNT];
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                float sum = 0f;
                for (int k = 0; k < SIZE; k++) {
                    sum += get(row, k) * other.get(k, col);
                }
                m[index(row, col)] = sum;
            }
        }
        return new Mat4(m);
    }

    public Vec3 transformPoint(Vec3 p) {
        float x = get(0, 0) * p.x() + get(0, 1) * p.y() + get(0, 2) * p.z() + get(0, 3);
        float y = get(1, 0) * p.x() + get(1, 1) * p.y() + get(1, 2) * p.z() + get(1, 3);
        float z = get(2, 0) * p.x() + get(2, 1) * p.y() + get(2, 2) * p.z() + get(2, 3);
        float w = get(3, 0) * p.x() + get(3, 1) * p.y() + get(3, 2) * p.z() + get(3, 3);
        if (w != 1f && w != 0f) {
            return new Vec3(x / w, y / w, z / w);
        }
        return new Vec3(x, y, z);
    }

    public Vec3 transformDirection(Vec3 d) {
        return new Vec3(
                get(0, 0) * d.x() + get(0, 1) * d.y() + get(0, 2) * d.z(),
                get(1, 0) * d.x() + get(1, 1) * d.y() + get(1, 2) * d.z(),
                get(2, 0) * d.x() + get(2, 1) * d.y() + get(2, 2) * d.z());
    }

    public float get(int row, int col) {
        return elements[index(row, col)];
    }

    public float[] toArray() {
        return elements.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Mat4[");
        for (int row = 0; row < SIZE; row++) {
            if (row > 0) {
                sb.append("; ");
            }
            for (int col = 0; col < SIZE; col++) {
                if (col > 0) {
                    sb.append(", ");
                }
                sb.append(get(row, col));
            }
        }
        return sb.append(']').toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Mat4 other && Arrays.equals(elements, other.elements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    private void set(int row, int col, float value) {
        elements[index(row, col)] = value;
    }

    private static int index(int row, int col) {
        return col * SIZE + row;
    }
}
