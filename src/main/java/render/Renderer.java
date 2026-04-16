package render;

import objects.BaseObject;
import objects.Cube;
import objects.Fractal;
import objects.Pyramid;
import utils.Camera;
import utils.ControlMode;
import utils.FractalType;
import utils.TextRenderer;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static utils.GluUtils.gluPerspective;

public class Renderer {
    // General
    private TextRenderer textRenderer;
    private int width, height;
    private int fractalList;
    // Object management
    private ControlMode currentControlMode = ControlMode.NONE;
    private ArrayList<Fractal> fractals = new ArrayList<Fractal>();
    private ArrayList<BaseObject> basicObjects = new ArrayList<BaseObject>();
    private int selectedObjectIndex = 0;
    private BaseObject selectedObject = null;
    private int selectedFractalIndex = 0;
    private Fractal selectedFractal = null;
    private boolean isFractalModeActive = false;
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

    // Initialization
    public void init() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);

        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // Wireframe mode maybe implement?

        textRenderer = new TextRenderer(width, height);
        oldmils = System.nanoTime();

        camera = new Camera();

        renderTestScene();

        if(!fractals.isEmpty()) {
            selectedFractal = fractals.get(selectedFractalIndex);
        }

        if(!basicObjects.isEmpty()) {
            selectedObject = basicObjects.get(selectedObjectIndex);
            selectedObject.setSelected(true);
        }

        // Create a list that is located directly at GPUs memory for better performance
        // fractalList = glGenLists(1);
        // glNewList(fractalList, GL_COMPILE);
        // Fractal.renderMenger(3, 10f, 10f, 10f, 0f, 10f, new float[] {1f, 0f, 0f}, new float[] {0f, 1f, 0f});
        // glEndList();
    }

    // Function that repeats and redraws the scene
    public void display() {
        glViewport(0, 0, width, height);

        // Cleaning up
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        updateFPS();

        // Calculations for smooth camera movement
        float deltaTime = 1.0f / (currentFps > 0 ? currentFps : 60);
        float movetranslationStep = 20.0f * deltaTime;

        if (wDown) camera.forward(movetranslationStep);
        if (sDown) camera.backward(movetranslationStep);
        if (aDown) camera.left(movetranslationStep);
        if (dDown) camera.right(movetranslationStep);

        // Model and view
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // Camera set up
        camera.setMatrix();

        // Render of 3D objects
        renderObjects();

        // Projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        System.out.println("Current Mode: " + currentControlMode + " | " + "Fractal mode: " + isFractalModeActive + " | " + "Selected object: " + selectedObject + " | " + "Selected fractal: " + selectedFractal);

        // Perspective Switch
        if (per)
            gluPerspective(45, width / (float) height, 0.1f, 100.0f);
        else
            glOrtho(-20 * width / (float) height,
                    20 * width / (float) height,
                    -20, 20, 0.1f, 100.0f);

        // Render of text
        renderText();
    }

    // FPS calculation
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

    // Renders all objects in the scene
    private void renderObjects() {
        glEnable(GL_DEPTH_TEST);

        renderAxis();

        glPushMatrix();
//        Fractal.renderSierpinski(4, 10.0f, 10.0f, 10.0f, 0.0f, 10.0f, new float[] {1f, 0f, 0f}, new float[] {0f, 1f, 0f});
//        glTranslatef(20, 0, 0);
//        Fractal.renderMenger(4, 10.0f, 10.0f, 10.0f, 0.0f, 10.0f, new float[] {1f, 0f, 0f}, new float[] {0f, 1f, 0f});
//        glCallList(fractalList);

        for (Fractal fractal : fractals) {
            fractal.render();
        }

        for (BaseObject object : basicObjects) {
            object.render();
        }

        glPopMatrix();
    }

    // Renders all text in the scene
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

    // Renders XYZ axis
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

    // Key handler
    public void handleKey(int key, int action) {
        // Camera movement
        boolean isDown = (action != GLFW_RELEASE);
        if (key == GLFW_KEY_W) wDown = isDown;
        if (key == GLFW_KEY_S) sDown = isDown;
        if (key == GLFW_KEY_A) aDown = isDown;
        if (key == GLFW_KEY_D) dDown = isDown;

        float translationStep = 1.0f;
        int rotationStep = 15;
        float scaleStep = 0.05f;

        // Key handling horror tree
        switch (action) {
            // Tap
            case GLFW_PRESS:
                switch (key) {
                    case GLFW_KEY_LEFT:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(translationStep, 0, 0);
                            } else {
                                selectedObject.move(translationStep, 0, 0);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(rotationStep, 1f, 0f, 0f);
                            } else {
                                selectedObject.rotate(rotationStep, 1f, 0f, 0f);
                            }
                        } else if(currentControlMode == ControlMode.OBJECT_SELECTION) {
                            selectedObjectIndex--;

                            if(selectedObjectIndex < 0) {
                                selectedObjectIndex = basicObjects.size() - 1;
                            }

                            selectedObject.setSelected(false);
                            selectedObject = basicObjects.get(selectedObjectIndex);
                            selectedObject.setSelected(true);
                        } else if(currentControlMode == ControlMode.FRACTAL_SELECTION) {
                            selectedFractalIndex--;

                            if(selectedFractalIndex < 0) {
                                selectedFractalIndex = fractals.size() - 1;
                            }

                            selectedFractal.setSelected(false);
                            selectedFractal = fractals.get(selectedFractalIndex);
                            selectedFractal.setSelected(true);
                        }
                    break;
                    case GLFW_KEY_RIGHT:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(-translationStep, 0, 0);
                            } else {
                                selectedObject.move(-translationStep, 0, 0);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(-rotationStep, 1f, 0f, 0f);
                            } else {
                                selectedObject.rotate(-rotationStep, 1f, 0f, 0f);
                            }
                        } else if(currentControlMode == ControlMode.OBJECT_SELECTION) {
                            selectedObjectIndex++;

                            if(selectedObjectIndex > basicObjects.size() - 1) {
                                selectedObjectIndex = 0;
                            }

                            selectedObject.setSelected(false);
                            selectedObject = basicObjects.get(selectedObjectIndex);
                            selectedObject.setSelected(true);

                        } else if(currentControlMode == ControlMode.FRACTAL_SELECTION) {
                            selectedFractalIndex++;

                            if(selectedFractalIndex > fractals.size() - 1) {
                                selectedFractalIndex = 0;
                            }

                            selectedFractal.setSelected(false);
                            selectedFractal = fractals.get(selectedFractalIndex);
                            selectedFractal.setSelected(true);
                        }
                    break;
                    case GLFW_KEY_KP_ADD:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(0, 0, translationStep);
                            } else {
                                selectedObject.move(0, 0, translationStep);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(rotationStep, 0f, 0f, 1f);
                            } else {
                                selectedObject.rotate(rotationStep, 0f, 0f, 1f);
                            }
                        }
                    break;
                    case GLFW_KEY_KP_SUBTRACT:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(0, 0, -translationStep);
                            } else {
                                selectedObject.move(0, 0, -translationStep);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(-rotationStep, 0f, 0f, 1f);
                            } else {
                                selectedObject.rotate(-rotationStep, 0f, 0f, 1f);
                            }
                        }
                    break;
                    case GLFW_KEY_UP:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(0, translationStep, 0);
                            } else {
                                selectedObject.move(0, translationStep, 0);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(rotationStep, 0f, 1f, 0f);
                            } else {
                                selectedObject.rotate(rotationStep, 0f, 1f, 0f);
                            }
                        } else if(currentControlMode == ControlMode.SCALE) {
                            if (isFractalModeActive) {
                                selectedFractal.scale(1 + scaleStep);
                            } else {
                                selectedObject.scale(1 + scaleStep);
                            }
                        }
                    break;
                    case GLFW_KEY_DOWN:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(0, -translationStep, 0);
                            } else {
                                selectedObject.move(0, -translationStep, 0);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(-rotationStep, 0f, 1f, 0f);
                            } else {
                                selectedObject.rotate(-rotationStep, 0f, 1f, 0f);
                            }
                        } else if(currentControlMode == ControlMode.SCALE) {
                            if (isFractalModeActive) {
                                selectedFractal.scale(1 - scaleStep);
                            } else {
                                selectedObject.scale(1 - scaleStep);
                            }
                        }
                    break;
                    case GLFW_KEY_T:
                        currentControlMode = ControlMode.TRANSLATION;
                    break;
                    case GLFW_KEY_R:
                        currentControlMode = ControlMode.ROTATION;
                    break;
                    case GLFW_KEY_Z:
                        currentControlMode = ControlMode.SCALE;
                    break;
                    case GLFW_KEY_C:
                        currentControlMode = ControlMode.NONE;
                    break;
                    case GLFW_KEY_O:
                        if(currentControlMode != ControlMode.OBJECT_SELECTION) {
                            currentControlMode = ControlMode.OBJECT_SELECTION;
                            if(selectedFractal != null) selectedFractal.setSelected(false);
                            selectedFractal = null;
                            selectedObject = basicObjects.get(selectedObjectIndex);
                            selectedObject.setSelected(true);
                            isFractalModeActive = false;
                        }
                    break;
                    case GLFW_KEY_F:
                        if(currentControlMode != ControlMode.FRACTAL_SELECTION) {
                            currentControlMode = ControlMode.FRACTAL_SELECTION;
                            if(selectedObject != null) selectedObject.setSelected(false);
                            selectedObject = null;
                            selectedFractal = fractals.get(selectedFractalIndex);
                            selectedFractal.setSelected(true);
                            isFractalModeActive = true;
                        }
                    break;
                }
            break;
            // Holding
            case GLFW_REPEAT:
                switch (key) {
                    case GLFW_KEY_LEFT:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(translationStep, 0, 0);
                            } else {
                                selectedObject.move(translationStep, 0, 0);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(rotationStep, 1f, 0f, 0f);
                            } else {
                                selectedObject.rotate(rotationStep, 1f, 0f, 0f);
                            }
                        } else if(currentControlMode == ControlMode.OBJECT_SELECTION) {
                            selectedObjectIndex--;
                        } else if(currentControlMode == ControlMode.FRACTAL_SELECTION) {
                            selectedFractalIndex--;
                        }
                    break;
                    case GLFW_KEY_RIGHT:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(-translationStep, 0, 0);
                            } else {
                                selectedObject.move(-translationStep, 0, 0);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(-rotationStep, 1f, 0f, 0f);
                            } else {
                                selectedObject.rotate(-rotationStep, 1f, 0f, 0f);
                            }
                        } else if(currentControlMode == ControlMode.OBJECT_SELECTION) {
                            selectedObjectIndex++;
                        } else if(currentControlMode == ControlMode.FRACTAL_SELECTION) {
                            selectedFractalIndex++;
                        }
                    break;
                    case GLFW_KEY_KP_ADD:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(0, 0, translationStep);
                            } else {
                                selectedObject.move(0, 0, translationStep);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(rotationStep, 0f, 0f, 1f);
                            } else {
                                selectedObject.rotate(rotationStep, 0f, 0f, 1f);
                            }
                        }
                    break;
                    case GLFW_KEY_KP_SUBTRACT:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(0, 0, -translationStep);
                            } else {
                                selectedObject.move(0, 0, -translationStep);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(-rotationStep, 0f, 0f, 1f);
                            } else {
                                selectedObject.rotate(-rotationStep, 0f, 0f, 1f);
                            }
                        }
                    break;
                    case GLFW_KEY_UP:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(0, translationStep, 0);
                            } else {
                                selectedObject.move(0, translationStep, 0);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(rotationStep, 0f, 1f, 0f);
                            } else {
                                selectedObject.rotate(rotationStep, 0f, 1f, 0f);
                            }
                        } else if(currentControlMode == ControlMode.SCALE) {
                            if (isFractalModeActive) {
                                selectedFractal.scale(1 + scaleStep);
                            } else {
                                selectedObject.scale(1 + scaleStep);
                            }
                        }
                    break;
                    case GLFW_KEY_DOWN:
                        if(currentControlMode == ControlMode.TRANSLATION) {
                            if (isFractalModeActive) {
                                selectedFractal.move(0, -translationStep, 0);
                            } else {
                                selectedObject.move(0, -translationStep, 0);
                            }
                        } else if(currentControlMode == ControlMode.ROTATION) {
                            if (isFractalModeActive) {
                                selectedFractal.rotate(-rotationStep, 0f, 1f, 0f);
                            } else {
                                selectedObject.rotate(-rotationStep, 0f, 1f, 0f);
                            }
                        } else if(currentControlMode == ControlMode.SCALE) {
                            if (isFractalModeActive) {
                                selectedFractal.scale(1 - scaleStep);
                            } else {
                                selectedObject.scale(1 - scaleStep);
                            }
                        }
                    break;
                }
            break;
        }
    }

    // Mouse button handler
    public void handleMouseButton(boolean down) {
        isLooking = down;
    }

    // Mouse movement handler
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

    public void renderTestScene() {
        Cube cube = new Cube(10.0f, 10.0f, 10.0f, new float[] {1f, 0f, 0f}, new float[] {0f, 1f, 0f});
        basicObjects.add(cube);

        Pyramid pyramid = new Pyramid(10.0f, 10.0f, 10.0f, new float[] {1f, 0f, 0f}, new float[] {0f, 1f, 0f});
        basicObjects.add(pyramid);
        pyramid.move(20f, 0f, 0f);

        Fractal sierpinskiPyramid = new Fractal(4, 10f, 10f, 10f, 0f, 10f, new float[] {1f, 0f, 0f}, new float[] {0f, 1f, 0f}, FractalType.SIERPINSKI_PYRAMID);
        fractals.add(sierpinskiPyramid);
        sierpinskiPyramid.move(40f, 0f, 0f);

        Fractal mengerSponge = new Fractal(2, 10f, 10f, 10f, 0f, 10f, new float[] {1f, 0f, 0f}, new float[] {0f, 1f, 0f}, FractalType.MENGER_SPONGE);
        fractals.add(mengerSponge);
        mengerSponge.move(60f, 0f, 0f);
    }
}