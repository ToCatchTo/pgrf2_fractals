package view;

import org.lwjgl.opengl.GL;
import render.Renderer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width;
    private int height;
    private final Renderer renderer;
    private long window;
    private String title = "Semestrální projekt PGRF2: Tomáš Terč";

    public Window(int width, int height, Renderer renderer) {
        this.width = width;
        this.height = height;
        this.renderer = renderer;
    }

    public void run() {
        init();

        loop();

        // Closes the window and cleans everything
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void init() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Sets up window "properties"
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Creates window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        // Set up callbacks
        glfwSetFramebufferSizeCallback(window, (win, newWidth, newHeight) -> {
            this.width = newWidth;
            this.height = newHeight;
            renderer.resize(newWidth, newHeight);
        });

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(win, true);
            }
            renderer.handleKey(key, action);
        });

        glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_1) {
                renderer.handleMouseButton(action == GLFW_PRESS);
            }
        });

        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            renderer.handleMouseMotion(xpos, ypos);
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Locked 60 fps
        glfwShowWindow(window);
    }

    public void loop() {
        GL.createCapabilities();

        renderer.init(window);

        while (!glfwWindowShouldClose(window)) {
            glfwMakeContextCurrent(window);
            renderer.display();
            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }
}