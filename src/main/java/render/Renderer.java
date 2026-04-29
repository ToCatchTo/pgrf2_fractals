package render;

import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import objects.*;
import utils.*;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static utils.GluUtils.gluPerspective;

public class Renderer {
    // General
    private TextRenderer textRenderer;
    private int width, height;
    private boolean isWireframeActive = false;
    private boolean isLightingActive = true;
    private int fractalDepth = 1;
    private int redTopFinal = 128;
    private int greenTopFinal = 128;
    private int blueTopFinal = 128;
    private int redBottomFinal = 128;
    private int greenBottomFinal = 128;
    private int blueBottomFinal = 128;
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
    // Light
    private Sphere lightSource;
    private float[] lightAmbient = { 0.2f, 0.2f, 0.2f, 1.0f };
    private float[] lightDiffuse = { 0.8f, 0.8f, 0.8f, 1.0f };
    private float[] lightSpecular = { 1.0f, 1.0f, 1.0f, 1.0f };
    // GUI
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private boolean showHelp = false;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // Window Resize
    public void resize(int newWidth, int newHeight) {
        if (newHeight == 0) newHeight = 1;
        this.width = newWidth;
        this.height = newHeight;
        textRenderer = new TextRenderer(width, height);
    }

    // Initialization
    public void init(long window) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);

        // GUI
        ImGui.createContext();
        imGuiGlfw.init(window, true);
        imGuiGl3.init("#version 130");

        // Text package
        textRenderer = new TextRenderer(width, height);
        oldmils = System.nanoTime();

        // Camera
        camera = new Camera();

        // Selected objects init
        if(!fractals.isEmpty()) {
            selectedFractal = fractals.get(selectedFractalIndex);
        }
        if(!basicObjects.isEmpty()) {
            selectedObject = basicObjects.get(selectedObjectIndex);
            selectedObject.setSelected(true);
        }

        // Light
        initLight();
    }

    // Repeats and redraws the scene
    public void display() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();

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

        // Light source
        if(isLightingActive) {
            renderLight();
        }

        // Render of 3D objects
        renderObjects();

        // Light source
        if(isLightingActive) {
            renderLight();
        }

        // Projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        // Perspective Switch
        if (per)
            gluPerspective(45, width / (float) height, 0.1f, 500.0f);
        else
            glOrtho(-20 * width / (float) height,
                    20 * width / (float) height,
                    -20, 20, 0.1f, 100.0f);

        // Render axis
        renderAxis();
        // Render of text
        renderText();
        // Render GUI
        renderGUI();
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

        glPushMatrix();

        for (Fractal fractal : fractals) {
            fractal.render();
        }

        for (BaseObject object : basicObjects) {
            if(object.getParentFractal() != null) {
                continue;
            }

            object.render();
        }

        glPopMatrix();
    }

    // Renders all text in the scene
    private void renderText() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        textRenderer.drawText("FPS: " + currentFps, 20, 30, Color.WHITE);

        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glPopMatrix();
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    // Renders XYZ axis
    public void renderAxis() {
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);

        glBegin(GL_LINES);
            // +X - Red
            glColor3f(1f, 0f, 0f);
            glVertex3f(0f, 0f, 0f);
            glVertex3f(100f, 0f, 0f);
            // +Y - Green
            glColor3f(0f, 1f, 0f);
            glVertex3f(0f, 0f, 0f);
            glVertex3f(0f, 100f, 0f);
            // +Z / Blue
            glColor3f(0f, 0f, 1f);
            glVertex3f(0f, 0f, 0f);
            glVertex3f(0f, 0f, 100f);
        glEnd();
    }

    // Key handler
    public void handleKey(int key, int action) {
        if (ImGui.getIO().getWantCaptureKeyboard()) return;

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
                        if(currentControlMode != ControlMode.OBJECT_SELECTION && !basicObjects.isEmpty()) {
                            currentControlMode = ControlMode.OBJECT_SELECTION;
                            if(selectedFractal != null) selectedFractal.setSelected(false);
                            selectedFractal = null;
                            selectedObject = basicObjects.get(selectedObjectIndex);
                            selectedObject.setSelected(true);
                            isFractalModeActive = false;
                        }
                    break;
                    case GLFW_KEY_F:
                        if(currentControlMode != ControlMode.FRACTAL_SELECTION && !fractals.isEmpty()) {
                            currentControlMode = ControlMode.FRACTAL_SELECTION;
                            if(selectedObject != null) selectedObject.setSelected(false);
                            selectedObject = null;
                            selectedFractal = fractals.get(selectedFractalIndex);
                            selectedFractal.setSelected(true);
                            isFractalModeActive = true;
                        }
                    break;
                    case GLFW_KEY_L:
                        isFractalModeActive = false;
                        currentControlMode = ControlMode.LIGHT_TRANSLATION;
                        if(selectedObject != null) selectedObject.setSelected(false);
                        selectedObject = lightSource;
                    break;
                    case GLFW_KEY_P:
                        per = !per;
                    break;
                    case GLFW_KEY_DELETE:
                        if(selectedObject != null) {
                            basicObjects.remove(selectedObjectIndex);
                            selectedObjectIndex = 0;
                            selectedObject = !basicObjects.isEmpty() ? basicObjects.get(selectedObjectIndex) : null;
                            if(selectedObject != null)  {
                                selectedObject.setSelected(true);
                            } else if(!fractals.isEmpty()) {
                                isFractalModeActive = true;
                                selectedFractal = fractals.get(selectedFractalIndex);
                                selectedFractal.setSelected(true);
                            }
                        } else if(selectedFractal != null) {
                            basicObjects.removeIf(obj -> obj.getParentFractal() == selectedFractal);
                            fractals.remove(selectedFractalIndex);
                            selectedFractalIndex = 0;
                            selectedFractal = !fractals.isEmpty() ? fractals.get(selectedFractalIndex) : null;
                            if(selectedFractal != null)  {
                                selectedFractal.setSelected(true);
                            } else if(!basicObjects.isEmpty()) {
                                isFractalModeActive = false;
                                selectedObject = basicObjects.get(selectedObjectIndex);
                                selectedObject.setSelected(true);
                            }
                        }
                    break;
                    case GLFW_KEY_X:
                        isWireframeActive = !isWireframeActive;
                        for (BaseObject object : basicObjects) {
                            object.setWireframed(isWireframeActive);
                        }

                        for(BaseObject fractal : fractals) {
                            fractal.setWireframed(isWireframeActive);
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
                }
            break;
        }
    }

    // Mouse button handler
    public void handleMouseButton(boolean down) {
        if (ImGui.getIO().getWantCaptureMouse()) return;

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

    // Light source initialization
    private void initLight() {
        glEnable(GL_DEPTH_TEST);

        // Creates light source object
        lightSource = new Sphere(1.0f, 20, 20, new float[]{1.0f, 1.0f, 0.0f});
        lightSource.move(20, 20, 30);

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_NORMALIZE);
        glShadeModel(GL_SMOOTH);

        // Ambient, diffuse, specular
        glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
        glLightfv(GL_LIGHT0, GL_SPECULAR, lightSpecular);

        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);

        // Shininess
        glMaterialf(GL_FRONT, GL_SHININESS, 50.0f);
        glMaterialfv(GL_FRONT, GL_SPECULAR, new float[]{1.0f, 1.0f, 1.0f, 1.0f});
    }

    // Light source render
    private void renderLight() {
        float[] position = {
                lightSource.getPosX(),
                lightSource.getPosY(),
                lightSource.getPosZ(),
                1.0f
        };
        glLightfv(GL_LIGHT0, GL_POSITION, position);
        lightSource.render();
        glEnable(GL_LIGHTING);
    }

    // GUI layout
    private void renderGUI() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        ImGui.setNextWindowSize(400, 550);
        ImGui.setNextWindowPos(width - ImGui.getWindowWidth() - 15, 15, ImGuiCond.Always);
        ImGui.begin("Control panel");

        // Scene
        BaseObject currentObject;

        // Fractals
        ImInt depth = new ImInt(fractalDepth);

        if (!basicObjects.isEmpty() || !fractals.isEmpty()) {
            // Scene
            if(!fractals.isEmpty() && isFractalModeActive) {
                currentObject = selectedFractal;
            } else if(fractals.isEmpty() && isFractalModeActive) {
                isFractalModeActive = false;
                selectedObject = basicObjects.get(selectedObjectIndex);
                currentObject = selectedObject;
            } else if(basicObjects.isEmpty()) {
                isFractalModeActive = true;
                selectedFractal = fractals.get(selectedFractalIndex);
                currentObject = selectedFractal;
            } else {
                currentObject = selectedObject;
            }
            ImBoolean wireframe = new ImBoolean(isWireframeActive);
            ImBoolean lighting = new ImBoolean(isLightingActive);
            ImBoolean perspective = new ImBoolean(per);

            // Objects
            ImFloat posX = new ImFloat(currentObject.getPosX());
            ImFloat posY = new ImFloat(currentObject.getPosY());
            ImFloat posZ = new ImFloat(currentObject.getPosZ());
            ImFloat rotX = new ImFloat(currentObject.getAngleX());
            ImFloat rotY = new ImFloat(currentObject.getAngleY());
            ImFloat rotZ = new ImFloat(currentObject.getAngleZ());
            ImFloat scaleXYZ = new ImFloat(currentObject.getScale());

            // Light
            ImFloat lightPosX = new ImFloat(lightSource.getPosX());
            ImFloat lightPosY = new ImFloat(lightSource.getPosY());
            ImFloat lightPosZ = new ImFloat(lightSource.getPosZ());

            // Colors
            ImInt redTop;
            ImInt greenTop;
            ImInt blueTop;
            ImInt redBottom;
            ImInt greenBottom;
            ImInt blueBottom;

            if(currentObject != lightSource) {
                redTop = new ImInt((int)(currentObject.getColorTop()[0] * 255));
                greenTop = new ImInt((int)(currentObject.getColorTop()[1] * 255));
                blueTop = new ImInt((int)(currentObject.getColorTop()[2] * 255));
                redBottom = new ImInt((int)(currentObject.getColorBottom()[0] * 255));
                greenBottom = new ImInt((int)(currentObject.getColorBottom()[1] * 255));
                blueBottom = new ImInt((int)(currentObject.getColorBottom()[2] * 255));
            } else {
                redTop = new ImInt(redTopFinal);
                greenTop = new ImInt(greenTopFinal);
                blueTop = new ImInt(blueTopFinal);
                redBottom = new ImInt(redBottomFinal);
                greenBottom = new ImInt(greenBottomFinal);
                blueBottom = new ImInt(blueBottomFinal);
            }

            // Textures
            ImInt texturesIndex = null;
            String[] textures = {"None" ,"onyx.jpg", "rocks.jpg", "wood.jpg"};
            for (int i = 1; i < textures.length; i++) {
                if(currentObject.getTextureName() == textures[i]) {
                    texturesIndex = new ImInt(i);
                }
            }

            if(texturesIndex == null) texturesIndex = new ImInt(0);

            ImGui.textColored(1f, 1f, 0f, 1f, "Scene details");
            ImGui.separator();
            ImGui.indent();
            String selectedObjectText = isFractalModeActive ? "Selected fractal: " + selectedFractal.getType().toString().toLowerCase() : "Selected object: " + selectedObject.getClass().getSimpleName().toLowerCase();
            ImGui.text(selectedObjectText);
            ImGui.text("Current control mode: " + currentControlMode);
            if (ImGui.checkbox("Wireframe mode", wireframe)) {
                isWireframeActive = wireframe.get();
                for (BaseObject object : basicObjects) {
                    object.setWireframed(isWireframeActive);
                }
                for (BaseObject fractal : fractals) {
                    fractal.setWireframed(isWireframeActive);
                }
            }
            if (ImGui.checkbox("Lighting", lighting)) {
                isLightingActive = lighting.get();
            }
            if (ImGui.checkbox("Perspective", perspective)) {
                per = perspective.get();
            }
            ImGui.unindent();
            ImGui.separator();

            // Object position
            ImGui.textColored(1f, 1f, 0f, 1f, "Position");
            ImGui.separator();
            ImGui.indent();
            // X
            ImGui.text("x:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##posX", posX)) {
                currentObject.setPosX(posX.floatValue());
            }
            // Y
            ImGui.text("y:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##posY", posY)) {
                currentObject.setPosY(posY.floatValue());
            }
            // Z
            ImGui.text("z:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##posZ", posZ)) {
                currentObject.setPosZ(posZ.floatValue());
            }
            ImGui.unindent();
            ImGui.separator();

            // Object rotation
            ImGui.textColored(1f, 1f, 0f, 1f, "Rotation");
            ImGui.separator();
            ImGui.indent();
            // X
            ImGui.text("x:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##rotX", rotX)) {
                currentObject.setAngleX(rotX.floatValue());
            }
            // Y
            ImGui.text("y:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##rotY", rotY)) {
                currentObject.setAngleY(rotY.floatValue());
            }
            // Z
            ImGui.text("z:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##rotZ", rotZ)) {
                currentObject.setAngleZ(rotZ.floatValue());
            }
            ImGui.unindent();
            ImGui.separator();

            // Object scale
            ImGui.textColored(1f, 1f, 0f, 1f, "Scale");
            ImGui.separator();
            ImGui.indent();
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##scaleXYZ", scaleXYZ)) {
                currentObject.setScale(scaleXYZ.floatValue());
            }
            ImGui.unindent();
            ImGui.separator();

            // Light position
            ImGui.textColored(1f, 1f, 0f, 1f, "Light Source");
            ImGui.separator();
            ImGui.indent();
            // X
            ImGui.text("x:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##lightPosX", lightPosX)) {
                lightSource.setPosX(lightPosX.floatValue());
            }
            // Y
            ImGui.text("y:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##lightPosY", lightPosY)) {
                lightSource.setPosY(lightPosY.floatValue());
            }
            // Z
            ImGui.text("z:");
            ImGui.sameLine(50);
            ImGui.setNextItemWidth(100);
            if (ImGui.inputFloat("##lightPosZ", lightPosZ)) {
                lightSource.setPosZ(lightPosZ.floatValue());
            }
            ImGui.unindent();
            ImGui.separator();

            // Colors
            ImGui.textColored(1f, 1f, 0f, 1f, "Colors");
            ImGui.separator();
            ImGui.indent();
            if (ImGui.beginTable("RGBColorTable", 3)) {
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.setNextItemWidth(-1);
                if (ImGui.inputInt("##redTop", redTop)) {
                    if(redTop.get() > 255) redTop.set(255);
                    else if(redTop.get() < 0) redTop.set(0);
                    redTopFinal = redTop.get();
                    colorize(redTop.get(), greenTop.get(), blueTop.get(), redBottom.get(), greenBottom.get(), blueBottom.get());
                }
                ImGui.tableNextColumn();
                ImGui.setNextItemWidth(-1);
                if (ImGui.inputInt("##greenTop", greenTop)) {
                    if(greenTop.get() > 255) greenTop.set(255);
                    else if(greenTop.get() < 0) greenTop.set(0);
                    greenTopFinal = greenTop.get();
                    colorize(redTop.get(), greenTop.get(), blueTop.get(), redBottom.get(), greenBottom.get(), blueBottom.get());
                }
                ImGui.tableNextColumn();
                ImGui.setNextItemWidth(-1);
                if (ImGui.inputInt("##blueTop", blueTop)) {
                    if(blueTop.get() > 255) blueTop.set(255);
                    else if(blueTop.get() < 0) blueTop.set(0);
                    blueTopFinal = blueTop.get();
                    colorize(redTop.get(), greenTop.get(), blueTop.get(), redBottom.get(), greenBottom.get(), blueBottom.get());
                }
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.setNextItemWidth(-1);
                if(ImGui.inputInt("##redBottom", redBottom)) {
                    if(redBottom.get() > 255) redBottom.set(255);
                    else if(redBottom.get() < 0) redBottom.set(0);
                    redBottomFinal = redBottom.get();
                    colorize(redTop.get(), greenTop.get(), blueTop.get(), redBottom.get(), greenBottom.get(), blueBottom.get());
                }
                ImGui.tableNextColumn();
                ImGui.setNextItemWidth(-1);
                if (ImGui.inputInt("##greenBottom", greenBottom)) {
                    if(greenBottom.get() > 255) greenBottom.set(255);
                    else if(greenBottom.get() < 0) greenBottom.set(0);
                    greenBottomFinal = greenBottom.get();
                    colorize(redTop.get(), greenTop.get(), blueTop.get(), redBottom.get(), greenBottom.get(), blueBottom.get());
                }
                ImGui.tableNextColumn();
                ImGui.setNextItemWidth(-1);
                if (ImGui.inputInt("##blueBottom", blueBottom)) {
                    if(blueBottom.get() > 255) blueBottom.set(255);
                    else if(blueBottom.get() < 0) blueBottom.set(0);
                    blueBottomFinal = blueBottom.get();
                    colorize(redTop.get(), greenTop.get(), blueTop.get(), redBottom.get(), greenBottom.get(), blueBottom.get());
                }
                ImGui.endTable();
            }
            ImGui.unindent();
            ImGui.separator();

            // Textures combo box
            ImGui.textColored(1f, 1f, 0f, 1f, "Textures");
            ImGui.separator();
            ImGui.indent();
            ImGui.text("Texture options:");
            ImGui.sameLine(150);
            ImGui.setNextItemWidth(200);
            if (ImGui.combo("##textureComboBox", texturesIndex, textures)) {
                if(textures[texturesIndex.get()] == "None") {
                    currentObject.setTexture(0, "");

                    if(currentObject.getClass() == Fractal.class) {
                        ((Fractal) currentObject).generate();
                    }
                } else {
                    currentObject.setTexture(TextureLoader.loadTexture(textures[texturesIndex.get()]), textures[texturesIndex.get()]);
                }
            }
            ImGui.unindent();
            ImGui.separator();
        }

        // Object buttons
        ImGui.textColored(1f, 1f, 0f, 1f, "Objects");
        ImGui.separator();
        ImGui.indent();
        if (ImGui.button("Add Cube")) {
            isFractalModeActive = false;
            if(selectedFractal != null) selectedFractal.setSelected(false);
            if(selectedObject != null) selectedObject.setSelected(false);
            selectedFractal = null;
            selectedFractalIndex = 0;

            Cube cube = new Cube(10.0f, 10.0f, 10.0f, new float[] {0.5f, 0.5f, 0.5f}, new float[] {0.5f, 0.5f, 0.5f});
            cube.setSelected(true);
            selectedObject = cube;
            basicObjects.add(cube);
            selectedObjectIndex = basicObjects.size() - 1;
        }
        ImGui.sameLine(100);
        if (ImGui.button("Add Pyramid")) {
            isFractalModeActive = false;
            if(selectedFractal != null) selectedFractal.setSelected(false);
            if(selectedObject != null) selectedObject.setSelected(false);
            selectedFractal = null;
            selectedFractalIndex = 0;

            Pyramid pyramid = new Pyramid(10.0f, 10.0f, 10.0f, new float[] {0.5f, 0.5f, 0.5f}, new float[] {0.5f, 0.5f, 0.5f});
            pyramid.setSelected(true);
            selectedObject = pyramid;
            basicObjects.add(pyramid);
            selectedObjectIndex = basicObjects.size() - 1;
        }
        ImGui.unindent();
        ImGui.separator();

        // Fractal text fields and buttons
        ImGui.textColored(1f, 1f, 0f, 1f, "Fractals");
        ImGui.separator();
        ImGui.indent();
        ImGui.text("Depth:");
        ImGui.sameLine(80);
        ImGui.setNextItemWidth(100);
        if (ImGui.inputInt("##fractalDepth", depth)) {
            if(depth.get() < 0) {
                depth.set(0);
            }
            fractalDepth = depth.get();
        }
        if (ImGui.button("Add Menger Sponge")) {
            isFractalModeActive = true;
            if(selectedFractal != null) selectedFractal.setSelected(false);
            if(selectedObject != null) selectedObject.setSelected(false);
            selectedObject = null;
            selectedObjectIndex = 0;

            Fractal fractal = new Fractal(fractalDepth, 10f, 10f, 10f, 0f, 10f, new float[] {0.5f, 0.5f, 0.5f}, new float[] {0.5f, 0.5f, 0.5f}, FractalType.MENGER_SPONGE);
            fractal.setSelected(true);
            selectedFractal = fractal;
            fractals.add(fractal);
            basicObjects.addAll(fractal.getObjectList());
            selectedFractalIndex = fractals.size() - 1;
        }
        ImGui.sameLine(163);
        if (ImGui.button("Add Sierpinski Pyramid")) {
            isFractalModeActive = true;
            if(selectedFractal != null) selectedFractal.setSelected(false);
            if(selectedObject != null) selectedObject.setSelected(false);
            selectedObject = null;
            selectedObjectIndex = 0;

            Fractal fractal = new Fractal(fractalDepth, 10f, 10f, 10f, 0f, 10f, new float[] {0.5f, 0.5f, 0.5f}, new float[] {0.5f, 0.5f, 0.5f}, FractalType.SIERPINSKI_PYRAMID);
            fractal.setSelected(true);
            selectedFractal = fractal;
            fractals.add(fractal);
            basicObjects.addAll(fractal.getObjectList());
            selectedFractalIndex = fractals.size() - 1;
        }
        ImGui.unindent();
        ImGui.textColored(1f, 1f, 0f, 1f, "Help");
        ImGui.separator();
        ImGui.indent();
        if (ImGui.button("Show Help")) {
            showHelp = !showHelp;
        }

        ImGui.end();

        if (showHelp) {
            ImGui.setNextWindowSize(500, 400, ImGuiCond.FirstUseEver);

            ImBoolean pOpen = new ImBoolean(showHelp);

            if (ImGui.begin("Help & Information", pOpen)) {
                ImGui.textColored(1f, 1f, 0f, 1f, "General information ");
                ImGui.separator();
                ImGui.bulletText("Project name: Application for interactive visualization of 3D objects and fractals");
                ImGui.bulletText("Author: Tomas Terc");
                ImGui.bulletText("Subject/course: PGRF2 - CV04");
                ImGui.bulletText("Submit date: 29.04.2026");

                ImGui.spacing();
                ImGui.separator();
                ImGui.textColored(1f, 1f, 0f, 1f, "Key control");
                ImGui.separator();
                ImGui.bulletText("WASD & Mouse: Camera control");
                ImGui.bulletText("P: Toggle Perspective/Orthographic view");
                ImGui.spacing();
                ImGui.separator();
                ImGui.bulletText("T: Translation mode (Move)");
                ImGui.bulletText("R: Rotation mode");
                ImGui.bulletText("Z: Scaling mode (Size)");
                ImGui.bulletText("L: Light source manipulation");
                ImGui.bulletText("C: Clear / Disable all modes");
                ImGui.spacing();
                ImGui.separator();
                ImGui.bulletText("O: Basic object selection (incl. individual fractal parts)");
                ImGui.bulletText("F: Fractal selection (selects entire structure)");
                ImGui.bulletText("Delete: Remove selected object");
                ImGui.bulletText("X: Toggle Wireframe mode");
                ImGui.spacing();
                ImGui.separator();
                ImGui.textWrapped("While in T, R, Z, O, or F modes, use:");
                ImGui.bulletText("+, - and Arrows: Adjust values (position, rotation, depth...)");
            }
            ImGui.end();

            showHelp = pOpen.get();
        }

        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    private void colorize(int rT, int gT, int bT, int rB, int gB, int bB) {
        float[] topColor = new float[] { rT / 255.0f, gT / 255.0f, bT / 255.0f };
        float[] bottomColor = new float[] { rB / 255.0f, gB / 255.0f, bB / 255.0f };

        if(isFractalModeActive) {
            selectedFractal.setColorTop(topColor);
            selectedFractal.setColorBottom(bottomColor);
            selectedFractal.generate();
        } else {
            selectedObject.setColorTop(topColor);
            selectedObject.setColorBottom(bottomColor);
        }
    }
}