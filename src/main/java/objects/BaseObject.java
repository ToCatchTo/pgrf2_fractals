package objects;

import static org.lwjgl.opengl.GL11.*;

public abstract class BaseObject {
    protected float posX = 0, posY = 0, posZ = 0;
    protected float rotAngle = 0, rotX = 0, rotY = 0, rotZ = 1;
    protected float scaleXYZ = 1;

    public abstract void move(float x, float y, float z);

    public abstract void rotate(float angle, float x, float y, float z);

    public abstract void scale(float s);

    public abstract void render();

    protected abstract void applyTransformations();
}