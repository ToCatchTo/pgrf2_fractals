package objects;

import static org.lwjgl.opengl.GL11.*;

public class Cube extends BaseObject {
    private float width, height, length;
    private float[] colorTop, colorBottom;

    public Cube(float width, float length, float height, float[] colorTop, float[] colorBottom) {
        this.width = width; this.length = length; this.height = height;
        this.colorTop = colorTop; this.colorBottom = colorBottom;
    }

    // Render logic
    @Override
    public void render() {
        glPushMatrix();
        applyTransformations();
        glDisable(GL_TEXTURE_2D);

        // Helper variables
        float[] finalTop = this.colorTop;
        float[] finalBottom = this.colorBottom;

        // Object has texture
        if (this.textureId != 0) {
            // Binds assigned texture
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, this.textureId);
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

            // Sets colors of the object to white to be able to have texture + lighting
            finalTop = new float[]{1.0f, 1.0f, 1.0f};
            finalBottom = new float[]{1.0f, 1.0f, 1.0f};
        }

        // Render of cube
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        drawGeometry(finalTop, finalBottom);

        // Render of selection wireframe
        if (this.isSelected()) {
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glDisable(GL_LIGHTING);
            glDisable(GL_TEXTURE_2D);

            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glLineWidth(2.5f);

            // Little offset
            glEnable(GL_POLYGON_OFFSET_LINE);
            glPolygonOffset(-1.0f, -1.0f);

            float[] yellow = {1.0f, 1.0f, 0.0f};
            drawGeometry(yellow, yellow);

            glPopAttrib();
        }

        glPopMatrix();
    }

    // Topology
    private void drawGeometry(float[] colorT, float[] colorB) {
        float hW = width / 2.0f;
        float hL = length / 2.0f;
        float hH = height;

        glBegin(GL_QUADS);

        // Top
        glNormal3f(0.0f, 0.0f, 1.0f);
        glColor3fv(colorT);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-hW, hL, hH);
        glTexCoord2f(1.0f, 0.0f); glVertex3f(hW, hL, hH);
        glTexCoord2f(1.0f, 1.0f); glVertex3f(hW, -hL, hH);
        glTexCoord2f(0.0f, 1.0f); glVertex3f(-hW, -hL, hH);

        // Bottom
        glNormal3f(0.0f, 0.0f, -1.0f);
        glColor3fv(colorB);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-hW, hL, 0);
        glTexCoord2f(1.0f, 0.0f); glVertex3f(hW, hL, 0);
        glTexCoord2f(1.0f, 1.0f); glVertex3f(hW, -hL, 0);
        glTexCoord2f(0.0f, 1.0f); glVertex3f(-hW, -hL, 0);

        // Front
        glNormal3f(0.0f, -1.0f, 0.0f);
        glColor3fv(colorB); glTexCoord2f(0.0f, 0.0f); glVertex3f(-hW, -hL, 0);
        glColor3fv(colorB); glTexCoord2f(1.0f, 0.0f); glVertex3f(hW, -hL, 0);
        glColor3fv(colorT); glTexCoord2f(1.0f, 1.0f); glVertex3f(hW, -hL, hH);
        glColor3fv(colorT); glTexCoord2f(0.0f, 1.0f); glVertex3f(-hW, -hL, hH);

        // Back
        glNormal3f(0.0f, 1.0f, 0.0f);
        glColor3fv(colorB); glTexCoord2f(0.0f, 0.0f); glVertex3f(-hW, hL, 0);
        glColor3fv(colorB); glTexCoord2f(1.0f, 0.0f); glVertex3f(hW, hL, 0);
        glColor3fv(colorT); glTexCoord2f(1.0f, 1.0f); glVertex3f(hW, hL, hH);
        glColor3fv(colorT); glTexCoord2f(0.0f, 1.0f); glVertex3f(-hW, hL, hH);

        // Left
        glNormal3f(-1.0f, 0.0f, 0.0f);
        glColor3fv(colorB); glTexCoord2f(0.0f, 0.0f); glVertex3f(-hW, -hL, 0);
        glColor3fv(colorB); glTexCoord2f(1.0f, 0.0f); glVertex3f(-hW, hL, 0);
        glColor3fv(colorT); glTexCoord2f(1.0f, 1.0f); glVertex3f(-hW, hL, hH);
        glColor3fv(colorT); glTexCoord2f(0.0f, 1.0f); glVertex3f(-hW, -hL, hH);

        // Right
        glNormal3f(1.0f, 0.0f, 0.0f);
        glColor3fv(colorB); glTexCoord2f(0.0f, 0.0f); glVertex3f(hW, -hL, 0);
        glColor3fv(colorB); glTexCoord2f(1.0f, 0.0f); glVertex3f(hW, hL, 0);
        glColor3fv(colorT); glTexCoord2f(1.0f, 1.0f); glVertex3f(hW, hL, hH);
        glColor3fv(colorT); glTexCoord2f(0.0f, 1.0f); glVertex3f(hW, -hL, hH);

        glEnd();
    }

    // Translation logic
    @Override
    public void move(float x, float y, float z) {
        this.posX += x;
        this.posY += y;
        this.posZ += z;
    }

    // Rotation logic
    @Override
    public void rotate(float angle, float x, float y, float z) {
        if (x != 0) this.angleX += angle;
        if (y != 0) this.angleY += angle;
        if (z != 0) this.angleZ += angle;
    }

    // Scaling logic
    @Override
    public void scale(float s) {
        this.scaleXYZ *= s;
    }

    @Override
    public void applyTransformations() {
        glTranslatef(posX, posY, posZ);
        glRotatef(angleZ, 0, 0, 1);
        glRotatef(angleY, 0, 1, 0);
        glRotatef(angleX, 1, 0, 0);
        glScalef(scaleXYZ, scaleXYZ, scaleXYZ);
    }
}