package br.unifacs.a3.sistemasolar.math;

public record Vec3(float x, float y, float z) {

    public static final Vec3 ZERO = new Vec3(0f, 0f, 0f);
    public static final Vec3 UNIT_X = new Vec3(1f, 0f, 0f);
    public static final Vec3 UNIT_Y = new Vec3(0f, 1f, 0f);
    public static final Vec3 UNIT_Z = new Vec3(0f, 0f, 1f);

    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }

    public Vec3 subtract(Vec3 other) {
        return new Vec3(x - other.x, y - other.y, z - other.z);
    }

    public Vec3 scale(float factor) {
        return new Vec3(x * factor, y * factor, z * factor);
    }

    public float dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x);
    }

    public float length() {
        return (float) Math.sqrt((double) x * x + (double) y * y + (double) z * z);
    }

    public Vec3 normalize() {
        float length = length();
        if (length == 0f) {
            throw new ArithmeticException("Não é possível normalizar o vetor nulo");
        }
        return scale(1f / length);
    }
}
