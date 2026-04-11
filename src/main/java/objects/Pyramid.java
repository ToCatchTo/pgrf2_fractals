package objects;

import static org.lwjgl.opengl.GL11.*;

public class Pyramid {
    /**
     * @param width         Width of the object
     * @param length        Length of the object
     * @param height        Height of the object
     * @param colorTop      Top color of the object
     * @param colorBottom   Bottom color of the object
    */

    public static void render(float width, float length, float height, float[] colorTop, float[] colorBottom) {
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
    }
}