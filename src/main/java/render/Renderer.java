package render;

import objects.Cube;
import objects.Fractal;
import objects.Pyramid;
import utils.Camera;
import utils.TextRenderer;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static utils.GluUtils.gluPerspective;

public class Renderer {
    private TextRenderer textRenderer;
    private int width, height;
    // FPS
    private int frames = 0;
    private long oldmils;
    private int currentFps = 0;
    // Perspective
    private boolean per = true;
    // Camera
    private Camera camera;
    private boolean wDown, sDown, aDown, dDown;
    private double lastMouseX, lastMouseY;
    private boolean isLooking = false;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // Wireframe mode maybe implement?

        textRenderer = new TextRenderer(width, height);
        oldmils = System.nanoTime();

        camera = new Camera();
    }

    public void display() {
        glViewport(0, 0, width, height);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        updateFPS();

        // Calculations for smooth camera movement
        float deltaTime = 1.0f / (currentFps > 0 ? currentFps : 60);
        float moveSpeed = 20.0f * deltaTime;

        if (wDown) camera.forward(moveSpeed);
        if (sDown) camera.backward(moveSpeed);
        if (aDown) camera.left(moveSpeed);
        if (dDown) camera.right(moveSpeed);

        // Projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        // Perspective Switch
        if (per)
            gluPerspective(45, width / (float) height, 0.1f, 100.0f);
        else
            glOrtho(-20 * width / (float) height,
                    20 * width / (float) height,
                    -20, 20, 0.1f, 100.0f);

        // Model and view
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // Aplikujeme transformaci kamery (nahrazuje statické gluLookAt)
        camera.setMatrix();

        // Render of 3D objects
        renderObjects();
        // Render of text
        renderText();
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
        glEnable(GL_DEPTH_TEST);

        renderAxis();

        glPushMatrix();
        Fractal.drawSierpinskiPyramid(3, 10.0f, 10.0f);
        glPopMatrix();
    }

    private void renderText() {
        glDisable(GL_DEPTH_TEST);

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        textRenderer.drawText("FPS: " + currentFps, 20, 30, Color.WHITE);
        textRenderer.drawText("Projekt: Fraktály", 20, 60, Color.WHITE);

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
    }

    public void renderAxis() {
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
    }

    public void handleKey(int key, int action) {
        boolean isDown = (action != GLFW_RELEASE);
        if (key == GLFW_KEY_W) wDown = isDown;
        if (key == GLFW_KEY_S) sDown = isDown;
        if (key == GLFW_KEY_A) aDown = isDown;
        if (key == GLFW_KEY_D) dDown = isDown;
    }

    public void handleMouseButton(boolean down) {
        isLooking = down;
    }

    public void handleMouseMotion(double x, double y) {
        if (isLooking) {
            double dx = x - lastMouseX;
            double dy = y - lastMouseY;

            camera.addAzimuth(-dx * 0.005);
            camera.addZenith(-dy * 0.005);
        }
        lastMouseX = x;
        lastMouseY = y;
    }
}