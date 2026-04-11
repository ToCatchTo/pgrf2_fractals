package objects;

import static org.lwjgl.opengl.GL11.*;

public class Pyramid {

    /**
     * Vykreslí barevnou pyramidu se středem podstavy v [0,0,0] a špičkou na ose Z.
     * @param baseSize Délka hrany čtvercové podstavy.
     * @param height Výška pyramidy od základny ke špičce.
     */
//    public static void render(float baseSize, float height) {
//        float h = baseSize / 2.0f; // Polovina základny
//
//        glBegin(GL_TRIANGLES);
//        // Přední stěna
//        glColor3f(1.0f, 0.0f, 0.0f);
//        glVertex3f(0, 0, height); // Špička
//        glVertex3f(h, -h, 0);     // Pravý dolní
//        glVertex3f(-h, -h, 0);    // Levý dolní
//
//        // Pravá stěna
//        glColor3f(0.0f, 1.0f, 0.0f);
//        glVertex3f(0, 0, height);
//        glVertex3f(h, h, 0);
//        glVertex3f(h, -h, 0);
//
//        // Zadní stěna
//        glColor3f(0.0f, 0.0f, 1.0f);
//        glVertex3f(0, 0, height);
//        glVertex3f(-h, h, 0);
//        glVertex3f(h, h, 0);
//
//        // Levá stěna
//        glColor3f(1.0f, 1.0f, 0.0f);
//        glVertex3f(0, 0, height);
//        glVertex3f(-h, -h, 0);
//        glVertex3f(-h, h, 0);
//        glEnd();
//
//        // Podstava
//        glBegin(GL_QUADS);
//        glColor3f(0.3f, 0.3f, 0.3f);
//        glVertex3f(-h, -h, 0);
//        glVertex3f(h, -h, 0);
//        glVertex3f(h, h, 0);
//        glVertex3f(-h, h, 0);
//        glEnd();
//    }

    public static void render(float baseSize, float height) {
        float h = baseSize / 2.0f;

        // Boční stěny pomocí vějíře
        glBegin(GL_TRIANGLE_FAN);
        // 1. Společný vrchol (Apex) - Špička
        glColor3f(1.0f, 1.0f, 1.0f); // Bílá špička
        glVertex3f(0, 0, height);

        // 2. Rohy základny (musí jít dokola a uzavřít se)
        glColor3f(1.0f, 0.0f, 0.0f); glVertex3f(h, -h, 0);  // Pravý přední
        glColor3f(0.0f, 1.0f, 0.0f); glVertex3f(h, h, 0);   // Pravý zadní
        glColor3f(0.0f, 0.0f, 1.0f); glVertex3f(-h, h, 0);  // Levý zadní
        glColor3f(1.0f, 1.0f, 0.0f); glVertex3f(-h, -h, 0); // Levý přední

        // 3. Uzavření vějíře (znovu první roh základny)
        glColor3f(1.0f, 0.0f, 0.0f); glVertex3f(h, -h, 0);
        glEnd();

        // Podstava (tu musíme udělat zvlášť, vějíř ji nevykryje)
        glBegin(GL_QUADS);
        glColor3f(0.3f, 0.3f, 0.3f);
        glVertex3f(-h, -h, 0);
        glVertex3f(h, -h, 0);
        glVertex3f(h, h, 0);
        glVertex3f(-h, h, 0);
        glEnd();
    }
}
