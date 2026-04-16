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

        // 1. Vykreslení plného objektu (Standardní barvy)
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        drawGeometry(this.colorTop, this.colorBottom);

        // 2. Vykreslení žlutého zvýraznění (pouze pokud je objekt vybrán)
        if (this.isSelected()) {
            glPushAttrib(GL_ALL_ATTRIB_BITS); // Uložíme aktuální nastavení OpenGL

            // Přepneme na vykreslování pouze hran (wireframe)
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glLineWidth(2.5f); // Nastavení tloušťky čáry výběru

            // Prevence Z-fightingu: posuneme čáry kousek před plochy, aby neproblikávaly
            glEnable(GL_POLYGON_OFFSET_LINE);
            glPolygonOffset(-1.0f, -1.0f);

            // Nastavíme jasně žlutou barvu pro všechny vrcholy
            float[] yellow = {1.0f, 1.0f, 0.0f};
            drawGeometry(yellow, yellow);

            glPopAttrib(); // Vrátíme původní nastavení OpenGL (barvy, polygonMode atd.)
        }

        glPopMatrix();
    }

    private void drawGeometry(float[] colorT, float[] colorB) {
        float halfWidth = width / 2.0f;
        float halfLength = length / 2.0f;

        glBegin(GL_TRIANGLES);
        // Front
        glColor3fv(colorT);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
        glColor3fv(colorB);    glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, -halfLength, 0);
        glColor3fv(colorB);    glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, 0);
        // Right
        glColor3fv(colorT);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
        glColor3fv(colorB);    glTexCoord2f(1.0f, 0.0f); glVertex3f(halfWidth, halfLength, 0);
        glColor3fv(colorB);    glTexCoord2f(0.0f, 0.0f); glVertex3f(halfWidth, -halfLength, 0);
        // Back
        glColor3fv(colorT);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
        glColor3fv(colorB);    glTexCoord2f(1.0f, 0.0f); glVertex3f(-halfWidth, halfLength, 0);
        glColor3fv(colorB);    glTexCoord2f(0.0f, 0.0f); glVertex3f(halfWidth, halfLength, 0);
        // Left
        glColor3fv(colorT);    glTexCoord2f(0.5f, 1.0f); glVertex3f(0, 0, height);
        glColor3fv(colorB);    glTexCoord2f(1.0f, 0.0f); glVertex3f(-halfWidth, -halfLength, 0);
        glColor3fv(colorB);    glTexCoord2f(0.0f, 0.0f); glVertex3f(-halfWidth, halfLength, 0);
        glEnd();

        glBegin(GL_QUADS);
        // Base
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