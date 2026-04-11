package objects;

import static org.lwjgl.opengl.GL11.*;

public class Fractal {

    /**
     * Vykreslí Sierpińského pyramidu pomocí rekurze.
     * @param depth Hloubka rekurze (0 = základní pyramida, 1+ = fraktál)
     * @param size Délka podstavy aktuální pyramidy
     * @param height Výška aktuální pyramidy
     */
    public static void drawSierpinskiPyramid(int depth, float size, float height) {
        // 1. BÁZOVÝ PŘÍPAD: Konec rekurze
        if (depth == 0) {
            Pyramid.render(size, height);
            return; // Zastavíme zanořování a vykreslíme finální těleso
        }

        // 2. REKURZIVNÍ KROK: Příprava rozměrů pro menší pyramidy
        float newSize = size / 2.0f;
        float newHeight = height / 2.0f;

        // O kolik se musíme posunout od středu, abychom se trefili do rohů
        float offset = newSize / 2.0f;

        // Vykreslíme 5 menších pyramid (4 v rozích podstavy, 1 nahoře jako špičku)

        // --- HORNÍ PYRAMIDA (Špička) ---
        glPushMatrix(); // Uložíme aktuální střed
        glTranslatef(0.0f, 0.0f, newHeight); // Vyskočíme po ose Z nahoru
        drawSierpinskiPyramid(depth - 1, newSize, newHeight); // Zavoláme znovu samu sebe s depth-1
        glPopMatrix(); // Vrátíme se do původního středu

        // --- LEVÁ PŘEDNÍ ---
        glPushMatrix();
        glTranslatef(-offset, -offset, 0.0f);
        drawSierpinskiPyramid(depth - 1, newSize, newHeight);
        glPopMatrix();

        // --- PRAVÁ PŘEDNÍ ---
        glPushMatrix();
        glTranslatef(offset, -offset, 0.0f);
        drawSierpinskiPyramid(depth - 1, newSize, newHeight);
        glPopMatrix();

        // --- PRAVÁ ZADNÍ ---
        glPushMatrix();
        glTranslatef(offset, offset, 0.0f);
        drawSierpinskiPyramid(depth - 1, newSize, newHeight);
        glPopMatrix();

        // --- LEVÁ ZADNÍ ---
        glPushMatrix();
        glTranslatef(-offset, offset, 0.0f);
        drawSierpinskiPyramid(depth - 1, newSize, newHeight);
        glPopMatrix();
    }
}