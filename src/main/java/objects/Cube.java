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
        applyTransformations();

        // 1. Vykreslení plné kostky
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        drawGeometry(this.colorTop, this.colorBottom);

        // 2. Vykreslení žlutého výběru
        if (this.isSelected()) {
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glDisable(GL_TEXTURE_2D); // Zajištění čisté barvy bez textury

            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            glLineWidth(3.0f);

            // Posun hran mírně před plochu, aby nedocházelo k problikávání (Z-fighting)
            glEnable(GL_POLYGON_OFFSET_LINE);
            glPolygonOffset(-1.0f, -1.0f);

            float[] yellow = {1.0f, 1.0f, 0.0f};
            drawGeometry(yellow, yellow);

            glPopAttrib();
        }

        glPopMatrix();
    }

    private void drawGeometry(float[] colorT, float[] colorB) {
        float hW = width / 2.0f;
        float hL = length / 2.0f;
        float hH = height; // Předpokládáme výšku od nuly nahoru jako u pyramidy

        glBegin(GL_QUADS);

        // Horní stěna
        glColor3fv(colorT);
        glVertex3f(-hW, hL, hH);  glVertex3f(hW, hL, hH);
        glVertex3f(hW, -hL, hH); glVertex3f(-hW, -hL, hH);

        // Spodní stěna
        glColor3fv(colorB);
        glVertex3f(-hW, hL, 0);   glVertex3f(hW, hL, 0);
        glVertex3f(hW, -hL, 0);  glVertex3f(-hW, -hL, 0);

        // Přední stěna
        glColor3fv(colorB); glVertex3f(-hW, -hL, 0);  glVertex3f(hW, -hL, 0);
        glColor3fv(colorT); glVertex3f(hW, -hL, hH); glVertex3f(-hW, -hL, hH);

        // Zadní stěna
        glColor3fv(colorB); glVertex3f(-hW, hL, 0);   glVertex3f(hW, hL, 0);
        glColor3fv(colorT); glVertex3f(hW, hL, hH);  glVertex3f(-hW, hL, hH);

        // Levá stěna
        glColor3fv(colorB); glVertex3f(-hW, -hL, 0);  glVertex3f(-hW, hL, 0);
        glColor3fv(colorT); glVertex3f(-hW, hL, hH); glVertex3f(-hW, -hL, hH);

        // Pravá stěna
        glColor3fv(colorB); glVertex3f(hW, -hL, 0);   glVertex3f(hW, hL, 0);
        glColor3fv(colorT); glVertex3f(hW, hL, hH);  glVertex3f(hW, -hL, hH);

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