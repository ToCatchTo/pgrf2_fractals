package geometry;

import java.util.Optional;

public class Vec3D {
    private final double x, y, z;

    public Vec3D() {
        x = y = z = 0.0f;
    }

    public Vec3D(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3D(final Vec3D v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vec3D add(final Vec3D v) {
        return new Vec3D(x + v.x, y + v.y, z + v.z);
    }

    public Vec3D sub(final Vec3D v) {
        return new Vec3D(x - v.x, y - v.y, z - v.z);
    }

    public Vec3D mul(final Vec3D v) {
        return new Vec3D(x * v.x, y * v.y, z * v.z);
    }

    public double dot(final Vec3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3D cross(final Vec3D v) {
        return new Vec3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y
                * v.x);
    }

    public Optional<Vec3D> normalized() {
        final double len = length();
        if (len == 0.0)
            return Optional.empty();
        return Optional.of(new Vec3D(x / len, y / len, z / len));
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
