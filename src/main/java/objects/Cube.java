package objects;

import static org.lwjgl.opengl.GL11.*;

public class Cube {

    /**
     * Vykreslí barevnou kostku se středem v [0,0,0].
     * @param size Celková délka hrany kostky.
     */
    public static void render(float size) {
        float h = size / 2.0f; // Polovina velikosti pro centrování

        glBegin(GL_QUADS);

        // Přední stěna (Osa Y z tvého pohledu)
        glColor3f(1.0f, 0.0f, 0.0f); // Červená
        glVertex3f(-h, -h,  h);
        glVertex3f( h, -h,  h);
        glVertex3f( h,  h,  h);
        glVertex3f(-h,  h,  h);

        // Zadní stěna
        glColor3f(0.0f, 1.0f, 0.0f); // Zelená
        glVertex3f( h, -h, -h);
        glVertex3f(-h, -h, -h);
        glVertex3f(-h,  h, -h);
        glVertex3f( h,  h, -h);

        // Horní stěna (Osa Z nahoru)
        glColor3f(0.0f, 0.0f, 1.0f); // Modrá
        glVertex3f(-h,  h,  h);
        glVertex3f( h,  h,  h);
        glVertex3f( h,  h, -h);
        glVertex3f(-h,  h, -h);

        // Spodní stěna
        glColor3f(1.0f, 1.0f, 0.0f); // Žlutá
        glVertex3f(-h, -h, -h);
        glVertex3f( h, -h, -h);
        glVertex3f( h, -h,  h);
        glVertex3f(-h, -h,  h);

        // Pravá stěna
        glColor3f(1.0f, 0.0f, 1.0f); // Fialová
        glVertex3f( h, -h,  h);
        glVertex3f( h, -h, -h);
        glVertex3f( h,  h, -h);
        glVertex3f( h,  h,  h);

        // Levá stěna
        glColor3f(0.0f, 1.0f, 1.0f); // Tyrkysová
        glVertex3f(-h, -h, -h);
        glVertex3f(-h, -h,  h);
        glVertex3f(-h,  h,  h);
        glVertex3f(-h,  h, -h);

        glEnd();
    }
}