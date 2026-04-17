package objects;

import static org.lwjgl.opengl.GL11.*;

public class Sphere extends BaseObject {
    private float radius;
    private int slices, stacks;
    private float[] color;

    public Sphere(float radius, int slices, int stacks, float[] color) {
        this.radius = radius;
        this.slices = slices;
        this.stacks = stacks;
        this.color = color;
    }

    // Render logic
    @Override
    public void render() {
        glPushMatrix();
        applyTransformations();

        glPushAttrib(GL_LIGHTING_BIT);
        glDisable(GL_LIGHTING);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glColor3fv(this.color);

        drawGeometry();

        glPopAttrib();
        glPopMatrix();
    }

    // Topology
    private void drawGeometry() {
        for (int i = 0; i < stacks; i++) {
            double lat0 = Math.PI * (-0.5 + (double) (i) / stacks);
            double z0 = Math.sin(lat0) * radius;
            double r0 = Math.cos(lat0) * radius;

            double lat1 = Math.PI * (-0.5 + (double) (i + 1) / stacks);
            double z1 = Math.sin(lat1) * radius;
            double r1 = Math.cos(lat1) * radius;

            glBegin(GL_QUAD_STRIP);
                for (int j = 0; j <= slices; j++) {
                    double lng = 2 * Math.PI * (double) (j - 1) / slices;
                    double x = Math.cos(lng);
                    double y = Math.sin(lng);

                    glVertex3d(x * r1, y * r1, z1);
                    glVertex3d(x * r0, y * r0, z0);
                }
            glEnd();
        }
    }

    // Translation logic
    public void move(float x, float y, float z) {
        this.posX += x;
        this.posY += y;
        this.posZ += z;
    }

    // Rotation logic
    public void rotate(float angle, float x, float y, float z) {
        this.rotAngle += angle;
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
    }

    // Scaling logic
    public void scale(float s) {
        this.scaleXYZ *= s;
    }

    public void applyTransformations() {
        glTranslatef(posX, posY, posZ);
        glRotatef(rotAngle, rotX, rotY, rotZ);
        glScalef(scaleXYZ, scaleXYZ, scaleXYZ);
    }
}