package objects;

import utils.FractalType;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Fractal extends BaseObject {
    private int depth;
    private float width, length, height;
    private float baseZ;
    private float gradientLevel;
    private FractalType type;
    private ArrayList<BaseObject> objectList = new ArrayList<BaseObject>();

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

    // Generates fractal based on given type and makes calculations only once for better performance
    public void generate() {
        objectList.clear();

        if (type == FractalType.SIERPINSKI_PYRAMID) {
            generateSierpinski(depth, width, length, height, baseZ, gradientLevel, colorTop, colorBottom, 0, 0, 0);
        } else if (type == FractalType.MENGER_SPONGE) {
            generateMenger(depth, width, length, height, baseZ, gradientLevel, colorTop, colorBottom, 0, 0, 0);
        }
    }

    // Render logic
    @Override
    public void render() {
        glPushMatrix();

        applyTransformations();

        // Fractal has texture
        if (this.textureId != 0) {
            for (BaseObject object : objectList) {
                object.setTexture(this.textureId, this.textureName);
                object.render();
            }
        } else {
            for (BaseObject object : objectList) {
                object.render();
            }
        }

        for (BaseObject object : objectList) {
            object.setWireframed(this.isWireframed);
        }

        // Fractal is selected
        if (this.isSelected()) {
            drawBoundingShape();
        }

        glPopMatrix();
    }

    // Sierpinski pyramid
    private void generateSierpinski(int depth, float width, float length, float height, float baseZ, float gradientLevel, float[] colorTop, float[] colorBottom, float x, float y, float z) {
        // Ending condition
        if (depth == 0) {
            // Checks for color difference
            if(colorTop == colorBottom || gradientLevel == 0) {
                Pyramid pyramid = new Pyramid(width, length, height, colorTop, colorTop);
                pyramid.setParentFractal(this);
                objectList.add(pyramid);
            } else {
                // Calculate interpolated color based on the current Z level
                float[] bottomColorInterpolated = lerp(colorBottom, colorTop, baseZ / gradientLevel);
                float[] topColorInterpolated = lerp(colorBottom, colorTop, (baseZ + height) / gradientLevel);

                Pyramid pyramid = new Pyramid(width, length, height, topColorInterpolated, bottomColorInterpolated);
                pyramid.setParentFractal(this);

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

        // Apex pyramid
        generateSierpinski(depth - 1, nW, nL, nH, baseZ + nH, gradientLevel, colorTop, colorBottom, x, y, z + nH);

        // Base 4 pyramids
        generateSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorTop, colorBottom, x - offW, y - offL, z);
        generateSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorTop, colorBottom, x + offW, y - offL, z);
        generateSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorTop, colorBottom, x + offW, y + offL, z);
        generateSierpinski(depth - 1, nW, nL, nH, baseZ, gradientLevel, colorTop, colorBottom, x - offW, y + offL, z);
    }

    // Menger sponge
    private void generateMenger(int depth, float width, float length, float height, float baseZ, float gradientLevel, float[] colorTop, float[] colorBottom, float x, float y, float z) {
        // Ending condition
        if (depth == 0) {
            // Checks for color difference
            if(colorTop == colorBottom || gradientLevel == 0) {
                Cube cube = new Cube(width, length, height, colorTop, colorBottom);
                cube.setParentFractal(this);
                objectList.add(cube);
            } else {
                // Calculate interpolated color based on the current Z level
                float[] bottomColorInterpolated = lerp(colorBottom, colorTop, baseZ / gradientLevel);
                float[] topColorInterpolated = lerp(colorBottom, colorTop, (baseZ + height) / gradientLevel);

                Cube cube = new Cube(width, length, height, topColorInterpolated, bottomColorInterpolated);
                cube.setParentFractal(this);

                cube.move(x, y, z);
                objectList.add(cube);
            }

            return;
        }

        // New halved parameters
        float nW = width / 3.0f;
        float nL = length / 3.0f;
        float nH = height / 3.0f;

        // Divides cube to 3x3x3
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    int absI = Math.abs(i);
                    int absJ = Math.abs(j);
                    int absK = Math.abs(k);

                    if (absI + absJ + absK > 1) {
                        float newX = x + (i * nW);
                        float newY = y + (j * nL);
                        float newZ = z + ((k + 1) * nH);

                        generateMenger(
                                depth - 1,
                                nW, nL, nH,
                                baseZ + ((k + 1) * nH),
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

    // Render of selection wireframe
    private void drawBoundingShape() {
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glLineWidth(2.5f);

        // Little offset
        glEnable(GL_POLYGON_OFFSET_LINE);
        glPolygonOffset(-2.0f, -2.0f);

        glColor3f(1.0f, 1.0f, 0.0f);

        float hW = this.width / 2.0f;
        float hL = this.length / 2.0f;
        float h = this.height;

        if (this.type == FractalType.MENGER_SPONGE) {
            glBegin(GL_QUADS);
                // Top and bottom
                glVertex3f(-hW, hL, h);  glVertex3f(hW, hL, h);  glVertex3f(hW, -hL, h); glVertex3f(-hW, -hL, h);
                glVertex3f(-hW, hL, 0);  glVertex3f(hW, hL, 0);  glVertex3f(hW, -hL, 0); glVertex3f(-hW, -hL, 0);
                // Front and back
                glVertex3f(-hW, -hL, 0); glVertex3f(hW, -hL, 0); glVertex3f(hW, -hL, h); glVertex3f(-hW, -hL, h);
                glVertex3f(-hW, hL, 0);  glVertex3f(hW, hL, 0);  glVertex3f(hW, hL, h);  glVertex3f(-hW, hL, h);
                // Left and right
                glVertex3f(-hW, -hL, 0); glVertex3f(-hW, hL, 0); glVertex3f(-hW, hL, h); glVertex3f(-hW, -hL, h);
                glVertex3f(hW, -hL, 0);  glVertex3f(hW, hL, 0);  glVertex3f(hW, hL, h);  glVertex3f(hW, -hL, h);
            glEnd();
        }
        else if (this.type == FractalType.SIERPINSKI_PYRAMID) {
            glBegin(GL_TRIANGLES);
                // Top
                glVertex3f(0, 0, h); glVertex3f(hW, -hL, 0); glVertex3f(-hW, -hL, 0);
                // Right
                glVertex3f(0, 0, h); glVertex3f(hW, hL, 0);  glVertex3f(hW, -hL, 0);
                // Back
                glVertex3f(0, 0, h); glVertex3f(-hW, hL, 0); glVertex3f(hW, hL, 0);
                // Left
                glVertex3f(0, 0, h); glVertex3f(-hW, -hL, 0); glVertex3f(-hW, hL, 0);
            glEnd();

            glBegin(GL_QUADS);
                // Base
                glVertex3f(-hW, -hL, 0); glVertex3f(hW, -hL, 0); glVertex3f(hW, hL, 0); glVertex3f(-hW, hL, 0);
            glEnd();
        }

        glPopAttrib();
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

    public ArrayList<BaseObject> getObjectList() {
        return objectList;
    }

    public FractalType getType() {
        return type;
    }
}