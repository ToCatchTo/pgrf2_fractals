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

    public Fractal getParentFractal() { return parentFractal; }

    public void render() {
        float halfWidth = width / 2.0f;
        float halfLength = length / 2.0f;
        float halfHeight = height / 2.0f;

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
    }
}