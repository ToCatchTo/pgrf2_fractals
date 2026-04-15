package objects;

import utils.FractalType;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Fractal extends BaseObject {
    private int depth;
    private float width, length, height;
    private float baseZ;
    private float gradientLevel;
    private float[] colorTop, colorBottom;
    private FractalType type;
    private ArrayList<BaseObject> objectList = new ArrayList<BaseObject>();

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
    public Fractal(int depth, float width, float length, float height, float baseZ, float gradientLevel, float[] colorTop, float[] colorBottom, FractalType type) {
        this.depth = depth;
        this.width = width;
        this.length = length;
        this.height = height;
        this.baseZ = baseZ;
        this.gradientLevel = gradientLevel;
        this.colorTop = colorTop;
        this.colorBottom = colorBottom;
        this.type = type;

        generate();
    }

    private void generate() {
        if (type == FractalType.SIERPINSKI_PYRAMID) {
            generateSierpinski(depth, width, length, height, baseZ, gradientLevel, colorTop, colorBottom, 0, 0, 0);
        } else if (type == FractalType.MENGER_SPONGE) {
            generateMenger(depth, width, length, height, baseZ, gradientLevel, colorTop, colorBottom, 0, 0, 0);
        }
    }

    @Override
    public void render() {
        glPushMatrix();

        applyTransformations();

        // Jednoduše nakreslíme všechny objekty, které jsme si při startu uložili
        for (BaseObject object : objectList) {
            object.render();
        }

        glPopMatrix();
    }

    private void generateSierpinski(int depth, float width, float length, float height, float baseZ, float gradientLevel, float[] colorTop, float[] colorBottom, float x, float y, float z) {
        // Ending condition
        if (depth == 0) {
            // Checks for color difference
            if(colorTop == colorBottom || gradientLevel == 0) {
                Pyramid pyramid = new Pyramid(width, length, height, colorTop, colorTop, this);
                objectList.add(pyramid);
            } else {
                // Calculate interpolated color based on the current Z level
                float[] bottomColorInterpolated = lerp(colorBottom, colorTop, baseZ / gradientLevel);
                float[] topColorInterpolated = lerp(colorBottom, colorTop, (baseZ + height) / gradientLevel);

                Pyramid pyramid = new Pyramid(width, length, height, topColorInterpolated, bottomColorInterpolated, this);

                pyramid.move(x, y, z);
                objectList.add(pyramid);
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

        // Horní pyramida - k Z přičteme nH, X a Y zůstávají
        generateSierpinski(depth - 1, nW, nL, nH, baseZ + nH, gradientLevel, colorTop, colorBottom, x, y, z + nH);

        // Spodní 4 pyramidy - k X a Y musíme PŘIČÍST offsety
        generateSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorTop, colorBottom, x - offW, y - offL, z);
        generateSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorTop, colorBottom, x + offW, y - offL, z);
        generateSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorTop, colorBottom, x + offW, y + offL, z);
        generateSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorTop, colorBottom, x - offW, y + offL, z);
    }

    private void generateMenger(int depth, float width, float length, float height, float baseZ, float gradientLevel, float[] colorTop, float[] colorBottom, float x, float y, float z) {
        if (depth == 0) {
            if(colorTop == colorBottom || gradientLevel == 0) {
                Cube cube = new Cube(width, length, height, colorTop, colorBottom, this);
                objectList.add(cube);
            } else {
                // Calculate interpolated color based on the current Z level
                float[] bottomColorInterpolated = lerp(colorBottom, colorTop, baseZ / gradientLevel);
                float[] topColorInterpolated = lerp(colorBottom, colorTop, (baseZ + height) / gradientLevel);

                Cube cube = new Cube(width, length, height, topColorInterpolated, bottomColorInterpolated, this);

                cube.move(x, y, z);
                objectList.add(cube);
            }

            return;
        }

        float nW = width / 3.0f;
        float nL = length / 3.0f;
        float nH = height / 3.0f;

        // Iterate through a 3x3x3 grid
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {

                    // LOGIKA VYNECHÁNÍ STŘEDŮ (Mengerova houba)
                    // Kostka se vykreslí pouze pokud jsou alespoň dvě souřadnice nenulové
                    // (vynecháváme absolutní střed a středy všech stěn)
                    int absI = Math.abs(i);
                    int absJ = Math.abs(j);
                    int absK = Math.abs(k);

                    if (absI + absJ + absK > 1) {
                        // Matematický výpočet nové pozice pro vnořenou kostku
                        float newX = x + (i * nW);
                        float newY = y + (j * nL);
                        float newZ = z + (k * nH);

                        // Rekurzivní volání pro hlubší úroveň
                        generateMenger(
                                depth - 1,
                                nW, nL, nH,
                                baseZ + (k * nH),
                                gradientLevel,
                                colorTop,
                                colorBottom,
                                newX, newY, newZ
                        );
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

    public ArrayList<BaseObject> getObjectList() {
        return objectList;
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
        this.rotAngle += angle;
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
    }

    // Scaling logic
    @Override
    public void scale(float s) {
        this.scaleXYZ *= s;
    }

    @Override
    public void applyTransformations() {
        glTranslatef(this.posX, this.posY, this.posZ);
        glRotatef(this.rotAngle, this.rotX, this.rotY, this.rotZ);
        glScalef(this.scaleXYZ, this.scaleXYZ, this.scaleXYZ);
    }
}