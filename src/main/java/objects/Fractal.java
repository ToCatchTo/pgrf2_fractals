package objects;

import static org.lwjgl.opengl.GL11.*;

public class Fractal {
    /**
     * @param depth          Depth of recursion
     * @param width          Width of the whole fractal
     * @param length         Length of the whole fractal
     * @param height         Height of the whole fractal
     * @param baseZ          Current Z position of the fractal
     * @param gradientLevel  Controls the level of gradient
     * @param colorTop       Top color of the fractal
     * @param colorBottom    Bottom color of the fractal
    */
    public static void renderSierpinski(int depth, float width, float length, float height, float baseZ, float gradientLevel, float[] colorTop, float[] colorBottom) {
        // Ending condition
        if (depth == 0) {
            // Checks for color difference
            if(colorTop == colorBottom || gradientLevel == 0) {
                Pyramid.render(width, length, height, colorTop, colorTop);
            } else {
                // Calculate interpolated color based on the current Z level
                float[] bCol = lerp(colorBottom, colorTop, baseZ / gradientLevel);
                float[] tCol = lerp(colorBottom, colorTop, (baseZ + height) / gradientLevel);
                Pyramid.render(width, length, height, tCol, bCol);
            }

            return;
        }

        // New halved parameters
        float nW = width / 2.0f;
        float nL = length / 2.0f;
        float nH = height / 2.0f;

        // Divide for better handling
        float offW = nW / 2.0f;
        float offL = nL / 2.0f;

        // Apex pyramid
        glPushMatrix();
        glTranslatef(0, 0, nH);
        renderSierpinski(depth - 1, nW, nL, nH, baseZ + nH, gradientLevel, colorBottom, colorTop);
        glPopMatrix();

        // Positions for bottom pyramids
        float[][] pos = {
                {-offW, -offL},
                { offW, -offL},
                { offW,  offL},
                {-offW,  offL}
        };

        // Bottom pyramids
        for (float[] p : pos) {
            glPushMatrix();
            glTranslatef(p[0], p[1], 0);
            renderSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorBottom, colorTop);
            glPopMatrix();
        }
    }

    /**
     * @param depth          Depth of recursion
     * @param width          Width of the whole fractal
     * @param length         Length of the whole fractal
     * @param height         Height of the whole fractal
     * @param baseZ          Current Z position of the fractal
     * @param gradientLevel  Controls the level of gradient
     * @param colorTop       Top color of the fractal
     * @param colorBottom    Bottom color of the fractal
    */
    public static void renderMenger(int depth, float width, float length, float height, float baseZ, float gradientLevel, float[] colorTop, float[] colorBottom) {
        if (depth == 0) {
            float[] bCol = lerp(colorBottom, colorTop, baseZ / gradientLevel);
            float[] tCol = lerp(colorBottom, colorTop, (baseZ + height) / gradientLevel);
            Cube.render(width, length, height, tCol, bCol);
            return;
        }

        float nW = width / 3.0f;
        float nL = length / 3.0f;
        float nH = height / 3.0f;

        // Iterate through a 3x3x3 grid
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {

                    // Logic to skip the center cubes of each face and the core
                    int absX = Math.abs(x);
                    int absY = Math.abs(y);
                    int absZ = Math.abs(z);

                    if (absX + absY + absZ > 1) {
                        glPushMatrix();
                        glTranslatef(x * nW, y * nL, z * nH);
                        // baseZ is adjusted by the actual Z displacement
                        renderMenger(depth - 1, nW, nL, nH, baseZ + (z * nH), gradientLevel, colorTop, colorBottom);
                        glPopMatrix();
                    }
                }
            }
        }
    }

    // Color interpolation for gradients
    private static float[] lerp(float[] a, float[] b, float t) {
        t = Math.max(0, Math.min(1, t));
        return new float[]{
                a[0] + (b[0] - a[0]) * t,
                a[1] + (b[1] - a[1]) * t,
                a[2] + (b[2] - a[2]) * t
        };
    }
}