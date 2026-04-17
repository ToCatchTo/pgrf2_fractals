package objects;

import static org.lwjgl.opengl.GL11.*;

public class Pyramid extends BaseObject {
    private float width, height, length;
    private float[] colorTop, colorBottom;

    public Pyramid(float width, float length, float height, float[] colorTop, float[] colorBottom) {
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

        // Render of pyramid
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
        float halfWidth = width / 2.0f;
        float halfLength = length / 2.0f;

        glBegin(GL_TRIANGLES);
        // Front
        glNormal3f(0.0f, -height, halfLength);
        glColor3fv(colorT);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
        glColor3fv(colorB);    glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, -halfLength, 0);
        glColor3fv(colorB);    glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, 0);
        // Right
        glNormal3f(height, 0.0f, halfWidth);
        glColor3fv(colorT);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
        glColor3fv(colorB);    glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, halfLength, 0);
        glColor3fv(colorB);    glTexCoord2f(0.0f, 0.0f); glVertex3f(halfWidth, -halfLength, 0);
        // Back
        glNormal3f(0.0f, height, halfLength);
        glColor3fv(colorT);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
        glColor3fv(colorB);    glTexCoord2f(1.0f, 0.0f); glVertex3f(-halfWidth, halfLength, 0);
        glColor3fv(colorB);    glTexCoord2f(0.0f, 0.0f); glVertex3f(halfWidth, halfLength, 0);
        // Left
        glNormal3f(-height, 0.0f, halfWidth);
        glColor3fv(colorT);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
        glColor3fv(colorB);    glTexCoord2f(1.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, 0);
        glColor3fv(colorB);    glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, halfLength, 0);
        glEnd();

        // Base
        glBegin(GL_QUADS);
        glNormal3f(0.0f, 0.0f, -1.0f);
        glColor3fv(colorB);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, 0);
        glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, -halfLength, 0);
        glTexCoord2f(1.0f, 1.0f); glVertex3f(halfWidth, halfLength, 0);
        glTexCoord2f(0.0f, 1.0f); glVertex3f(-halfWidth, halfLength, 0);
        glEnd();
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