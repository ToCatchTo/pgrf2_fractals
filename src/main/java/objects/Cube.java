package objects;

import static org.lwjgl.opengl.GL11.*;

public class Cube extends BaseObject {
    private Fractal parentFractal;
    private float width, height, length;
    private float[] colorTop, colorBottom;

    public Cube(float width, float length, float height, float[] colorTop, float[] colorBottom, Fractal parentFractal) {
        this.width = width; this.length = length; this.height = height;
        this.colorTop = colorTop; this.colorBottom = colorBottom;
        this.parentFractal = parentFractal;
    }

    public Cube(float width, float length, float height, float[] colorTop, float[] colorBottom) {
        this.width = width; this.length = length; this.height = height;
        this.colorTop = colorTop; this.colorBottom = colorBottom;
        this.parentFractal = null;
    }

    public Fractal getParentFractal() { return parentFractal; }

    @Override
    public void render() {
        glPushMatrix();

        float halfWidth = width / 2.0f;
        float halfLength = length / 2.0f;
        float halfHeight = height / 2.0f;

        applyTransformations();

        glBegin(GL_QUADS);
        // Top
        glColor3fv(colorTop);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, halfHeight);
        glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, -halfLength, halfHeight);
        glTexCoord2f(1.0f, 1.0f); glVertex3f(halfWidth, halfLength, halfHeight);
        glTexCoord2f(0.0f, 1.0f); glVertex3f(-halfWidth, halfLength, halfHeight);
        // Bottom
        glColor3fv(colorBottom);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, -halfHeight);
        glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, -halfLength, -halfHeight);
        glTexCoord2f(1.0f, 1.0f); glVertex3f(halfWidth, halfLength, -halfHeight);
        glTexCoord2f(0.0f, 1.0f); glVertex3f(-halfWidth, halfLength, -halfHeight);
        // Front
        glColor3fv(colorBottom); glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, -halfHeight);
        glColor3fv(colorBottom); glTexCoord2f(1.0f, 0.0f); glVertex3f( halfWidth, -halfLength, -halfHeight);
        glColor3fv(colorTop); glTexCoord2f(1.0f, 1.0f); glVertex3f( halfWidth, -halfLength,  halfHeight);
        glColor3fv(colorTop); glTexCoord2f(0.0f, 1.0f); glVertex3f(-halfWidth, -halfLength,  halfHeight);
        // Back
        glColor3fv(colorBottom); glTexCoord2f(0.0f, 0.0f); glVertex3f( halfWidth,  halfLength, -halfHeight);
        glColor3fv(colorBottom); glTexCoord2f(1.0f, 0.0f); glVertex3f(-halfWidth,  halfLength, -halfHeight);
        glColor3fv(colorTop); glTexCoord2f(1.0f, 1.0f); glVertex3f(-halfWidth,  halfLength,  halfHeight);
        glColor3fv(colorTop); glTexCoord2f(0.0f, 1.0f); glVertex3f( halfWidth,  halfLength,  halfHeight);
        // Left
        glColor3fv(colorBottom); glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth,  halfLength, -halfHeight);
        glColor3fv(colorBottom); glTexCoord2f(1.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, -halfHeight);
        glColor3fv(colorTop); glTexCoord2f(1.0f, 1.0f); glVertex3f(-halfWidth, -halfLength,  halfHeight);
        glColor3fv(colorTop); glTexCoord2f(0.0f, 1.0f); glVertex3f(-halfWidth,  halfLength,  halfHeight);
        // Right
        glColor3fv(colorBottom); glTexCoord2f(0.0f, 0.0f); glVertex3f( halfWidth, -halfLength, -halfHeight);
        glColor3fv(colorBottom); glTexCoord2f(1.0f, 0.0f); glVertex3f( halfWidth,  halfLength, -halfHeight);
        glColor3fv(colorTop); glTexCoord2f(1.0f, 1.0f); glVertex3f( halfWidth,  halfLength,  halfHeight);
        glColor3fv(colorTop); glTexCoord2f(0.0f, 1.0f); glVertex3f( halfWidth, -halfLength,  halfHeight);
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