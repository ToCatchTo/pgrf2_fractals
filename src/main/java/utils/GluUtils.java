package utils;

import geometry.Vec3D;

import static org.lwjgl.opengl.GL11.glMultMatrixd;

public class GluUtils {
    public static void gluLookAt(double ex, double ey, double ez,
                                 double ax, double ay, double az,
                                 double ux, double uy, double uz) {
        Vec3D e = new Vec3D(ex, ey, ez);
        Vec3D a = new Vec3D(ax, ay, az);
        Vec3D u = new Vec3D(ux, uy, uz);
        Vec3D z = e.sub(a).normalized().orElse(new Vec3D());
        Vec3D x = u.cross(z).normalized().orElse(new Vec3D());
        Vec3D y = z.cross(x).normalized().orElse(new Vec3D());
        double[] m = new double[16];
        m[0] = x.getX();
        m[4] = x.getY();
        m[8] = x.getZ();
        m[1] = y.getX();
        m[5] = y.getY();
        m[9] = y.getZ();
        m[2] = z.getX();
        m[6] = z.getY();
        m[10] = z.getZ();
        m[12] = -x.dot(e);
        m[13] = -y.dot(e);
        m[14] = -z.dot(e);
        m[15] = 1;
        glMultMatrixd(m);
    }

    public static void gluPerspective(double fov, double aspect, double zNear, double zFar) {
        double[] m = new double[16];
        m[0] = 1 / (aspect * Math.tan(Math.toRadians(fov) / 2));
        m[5] = 1 / (Math.tan(Math.toRadians(fov) / 2));
        m[11] = -1;
        m[10] = (zFar + zNear) / (zNear - zFar);
        m[14] = 2 * zFar * zNear / (zNear - zFar);

        glMultMatrixd(m);
    }
}
