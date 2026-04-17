package objects;

import static org.lwjgl.opengl.GL11.*;

public abstract class BaseObject {
    protected float posX = 0, posY = 0, posZ = 0;
    protected float angleX = 0, angleY = 0, angleZ = 0;
    protected float scaleXYZ = 1;
    protected boolean isSelected = false;
    protected int textureId = 0;
    protected Fractal parentFractal = null;

    // Transformations
    public abstract void move(float x, float y, float z);

    public abstract void rotate(float angle, float x, float y, float z);

    public abstract void scale(float s);

    protected abstract void applyTransformations();

    // Render
    public abstract void render();

    // Setters
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setTexture(int textureId) {
        this.textureId = textureId;
    }

    public void setParentFractal(Fractal parentFractal) {
        this.parentFractal = parentFractal;
    }

    // Getters
    public float getPosX() { return posX; }

    public float getPosY() { return posY; }

    public float getPosZ() { return posZ; }

    public int getTextureId() { return textureId; }

    public Fractal getParentFractal() { return parentFractal; }
}