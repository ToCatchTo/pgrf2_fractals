package app;

import render.Renderer;
import view.Window;

public class Main {
    public static void main(String[] args) {
        new Window(800, 600, new Renderer(800, 600)).run();
    }
}