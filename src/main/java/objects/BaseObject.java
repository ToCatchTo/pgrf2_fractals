package objects;

import static org.lwjgl.opengl.GL11.*;

public abstract class BaseObject {
    protected float posX = 0, posY = 0, posZ = 0;
    protected float rotAngle = 0, rotX = 0, rotY = 0, rotZ = 1;
    protected float scaleX = 1, scaleY = 1, scaleZ = 1;

    // Translation logic
        public void move(float x, float y, float z) {
            this.posX += x;
            this.posY += y;
            this.posZ += z;
        }

    // Rotation logic
    public void rotate(float angle, float x, float y, float z) {
        this.rotAngle += angle;
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
    }

    // Scaling logic
    public void scale(float s) {
        this.scaleX *= s;
        this.scaleY *= s;
        this.scaleZ *= s;
    }

    public abstract void render();

    protected void applyTransformations() {
        glTranslatef(posX, posY, posZ);
        glRotatef(rotAngle, rotX, rotY, rotZ);
        glScalef(scaleX, scaleY, scaleZ);
    }
}