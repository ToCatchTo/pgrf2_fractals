package objects;

import static org.lwjgl.opengl.GL11.*;

public class Pyramid extends BaseObject {
    private Fractal parentFractal;
    private float width, height, length;
    private float[] colorTop, colorBottom;

    public Pyramid(float width, float length, float height, float[] colorTop, float[] colorBottom, Fractal parentFractal) {
        this.width = width; this.length = length; this.height = height;
        this.colorTop = colorTop; this.colorBottom = colorBottom;
        this.parentFractal = parentFractal;
    }

    public Pyramid(float width, float length, float height, float[] colorTop, float[] colorBottom) {
        this.width = width; this.length = length; this.height = height;
        this.colorTop = colorTop; this.colorBottom = colorBottom;
        this.parentFractal = null;
    }

    public Fractal getParentFractal() { return parentFractal; }

    @Override
    public void render() {
        glPushMatrix();

        applyTransformations();

        // Divide for better handling
        float halfWidth = width / 2.0f;
        float halfLength = length / 2.0f;

        glBegin(GL_TRIANGLES);
            // Front
            glColor3fv(colorTop);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
            glColor3fv(colorBottom); glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, -halfLength, 0);
            glColor3fv(colorBottom); glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, 0);
            // Right
            glColor3fv(colorTop);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
            glColor3fv(colorBottom); glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, halfLength, 0);
            glColor3fv(colorBottom); glTexCoord2f(0.0f, 0.0f); glVertex3f(halfWidth, -halfLength, 0);
            // Back
            glColor3fv(colorTop);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
            glColor3fv(colorBottom); glTexCoord2f(1.0f, 0.0f); glVertex3f(-halfWidth, halfLength, 0);
            glColor3fv(colorBottom); glTexCoord2f(0.0f, 0.0f); glVertex3f(halfWidth, halfLength, 0);
            // Left
            glColor3fv(colorTop);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
            glColor3fv(colorBottom); glTexCoord2f(1.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, 0);
            glColor3fv(colorBottom); glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, halfLength, 0);
        glEnd();

        // Base
        glBegin(GL_QUADS);
            glColor3fv(colorBottom);
            glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, 0);
            glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, -halfLength, 0);
            glTexCoord2f(1.0f, 1.0f); glVertex3f(halfWidth, halfLength, 0);
            glTexCoord2f(0.0f, 1.0f); glVertex3f(-halfWidth, halfLength, 0);
        glEnd();

        glPopMatrix();
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