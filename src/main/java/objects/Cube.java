package objects;

import static org.lwjgl.opengl.GL11.*;

public class Cube {
    /**
     * @param width         Width of the object 
     * @param length        Length of the object 
     * @param height        Height of the object 
     * @param colorTop      Top color of the object
     * @param colorBottom   Bottom color of the object
     */
    public static void render(float width, float length, float height, float[] colorTop, float[] colorBottom) {
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