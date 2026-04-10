package render;

import utils.TextRenderer;

import java.awt.*;
import java.awt.image.BufferStrategy;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private TextRenderer textRenderer;
    private int width, height;
    private int frames = 0;
    private long lastTime = System.nanoTime();
    private int currentFps = 0;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void init() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        textRenderer = new TextRenderer(width, height);
        lastTime = System.nanoTime();
    }

    public void display() {
        // 1. PŘÍPRAVA (Vymytí štětců a plátna)
        // Vyčistí barevný buffer a hloubkový buffer (pro 3D)
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Nastavení výřezu na aktuální velikost okna
        glViewport(0, 0, width, height);

        // Resetování matic (aby se kreslilo od středu)
        glLoadIdentity();

        // 2. VLASTNÍ GRAFIKA (To, co jsi chtěl původně)
        // Zde budeš mít později logiku pro fraktály
        glBegin(GL_TRIANGLES);
        glColor3f(1f, 0f, 0f);
        glVertex2f(-1f, -1);
        glColor3f(0f, 1f, 0f);
        glVertex2f(1, 0);
        glColor3f(0f, 0f, 1f);
        glVertex2f(0, 1);
        glEnd();

        updateFPS();
        renderText();
    }

    private void updateFPS() {
        frames++;
        long currentTime = System.nanoTime();
        long delta = currentTime - lastTime;

        if (delta >= 200_000_000) {
            currentFps = (int) (frames * (1_000_000_000.0 / delta));
            frames = 0;
            lastTime = currentTime;
        }
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