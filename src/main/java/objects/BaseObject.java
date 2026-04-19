package objects;

import static org.lwjgl.opengl.GL11.*;

public abstract class BaseObject {
    protected float posX = 0, posY = 0, posZ = 0;
    protected float angleX = 0, angleY = 0, angleZ = 0;
    protected float scaleXYZ = 1;
    protected boolean isSelected = false;
    protected int textureId = 0;
    protected String textureName = "";
    protected Fractal parentFractal = null;
    protected boolean isWireframed = false;
    protected float[] colorTop, colorBottom;

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

    public void setTexture(int textureId, String textureName) {
        this.textureId = textureId;
        this.textureName = textureName;
    }

    public void setParentFractal(Fractal parentFractal) {
        this.parentFractal = parentFractal;
    }

    public void setScale(float scaleXYZ) {
        this.scaleXYZ = scaleXYZ;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setPosZ(float posZ) {
        this.posZ = posZ;
    }

    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public void setAngleY(float angleY) {
        this.angleY = angleY;
    }

    public void setAngleZ(float angleZ) {
        this.angleZ = angleZ;
    }

    public void setWireframed(boolean wireframed) {
        isWireframed = wireframed;
    }

    public void setColorTop(float[] colorTop) {
        this.colorTop = colorTop;
    }

    public void setColorBottom(float[] colorBottom) {
        this.colorBottom = colorBottom;
    }

    // Getters
    public float getPosX() { return posX; }

    public float getPosY() { return posY; }

    public float getPosZ() { return posZ; }

    public int getTextureId() { return textureId; }

    public Fractal getParentFractal() { return parentFractal; }

    public float getScale() { return scaleXYZ; }

    public float getAngleX() {
        return angleX;
    }

    public float getAngleY() {
        return angleY;
    }

    public float getAngleZ() {
        return angleZ;
    }

    public String getTextureName() {
        return textureName;
    }
}