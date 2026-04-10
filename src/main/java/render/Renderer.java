package render;

import utils.TextRenderer;

import java.awt.*;
import java.awt.image.BufferStrategy;

import static org.lwjgl.opengl.GL11.*;
import static utils.GluUtils.gluLookAt;
import static utils.GluUtils.gluPerspective;

public class Renderer {
    private TextRenderer textRenderer;
    private int width, height;
    // FPS
    private int frames = 0;
    private long oldmils;
    private int currentFps = 0;
    // Perspective
    private boolean per = false;
    // TODO: Camera

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);

        textRenderer = new TextRenderer(width, height);
        oldmils = System.nanoTime();
    }

    public void display() {
        glViewport(0, 0, width, height);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        updateFPS();

        // MV (Model and view)
        glMatrixMode(GL_MODELVIEW);

        glRotatef(1, 0, 0, 1);

        // P (Projection)
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        // Perspective Switch
        if (per)
            gluPerspective(45, width / (float) height, 0.1f, 100.0f);
        else
            glOrtho(-20 * width / (float) height,
                    20 * width / (float) height,
                    -20, 20, 0.1f, 100.0f);

        // Camera setup (position XYZ, point of direction XYZ, rotation XYZ)
        gluLookAt(40, 40, 20, 0, 0, 0, 0, 0, 0.8);

        // Render of 3D objects
        renderObjects();

        glDisable(GL_DEPTH_TEST);

        // Render of text
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        renderText();

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
    }

    private void updateFPS() {
        frames++;
        long mils = System.nanoTime();
        long delta = mils - oldmils;

        if (delta >= 200_000_000) {
            currentFps = (int) (frames * (1_000_000_000.0 / delta));
            frames = 0;
            oldmils = mils;
        }
    }

    private void renderObjects() {
        glBegin(GL_LINES);
        glColor3f(1f, 0f, 0f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(100f, 0f, 0f);
        glColor3f(0f, 1f, 0f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(0f, 100f, 0f);
        glColor3f(0f, 0f, 1f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(0f, 0f, 100f);
        glEnd();

        glBegin(GL_TRIANGLE_FAN);
        glColor3f(1.0f, 1.0f, 1.0f);
        glVertex3f(5.0f, 5.0f, 10.0f);
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(10.0f, 0.0f, 0.0f);
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(10.0f, 10.0f, 0.0f);
        glColor3f(1.0f, 1.0f, 0.0f);
        glVertex3f(0.0f, 10.0f, 0.0f);
        glEnd();
    }

    private void renderText() {
        textRenderer.drawText("FPS: " + currentFps, 20, 30, Color.WHITE);
        textRenderer.drawText("Tomas Terc - Projekt", 20, 60, Color.WHITE);
    }

//    public void reshape(int width, int height) {
//        this.width = width;
//        this.height = height;
//        if (textRenderer != null) {
//            textRenderer.resize(width, height);
//        }
//    }
}