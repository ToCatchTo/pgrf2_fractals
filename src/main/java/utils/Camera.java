package utils;

import static utils.GluUtils.gluLookAt;

public class Camera {
    private double x, y, z;
    private double azimuth, zenith;
    private boolean valid;
    private double dirX, dirY, dirZ;

    public Camera() {
        x = 40.0;
        y = 40.0;
        z = 20.0;
        azimuth = Math.PI * 1.25;
        zenith = -0.4;
        valid = false;
    }

    private void computeMatrix() {
        dirX = Math.cos(zenith) * Math.cos(azimuth);
        dirY = Math.cos(zenith) * Math.sin(azimuth);
        dirZ = Math.sin(zenith);

        valid = true;
    }

    public void setMatrix() {
        if (!valid) computeMatrix();
        gluLookAt(
                x, y, z,
                x + dirX, y + dirY, z + dirZ,
                0, 0, 1
        );
    }

    public void forward(double speed) {
        if (!valid) computeMatrix();
        x += dirX * speed;
        y += dirY * speed;
        z += dirZ * speed;
    }

    public void backward(double speed) {
        forward(-speed);
    }

    public void right(double speed) {
        x += Math.cos(azimuth - Math.PI / 2) * speed;
        y += Math.sin(azimuth - Math.PI / 2) * speed;
    }

    public void left(double speed) {
        right(-speed);
    }

    public void addAzimuth(double angle) {
        azimuth += angle;
        valid = false;
    }

    public void addZenith(double angle) {
        if (zenith + angle <= Math.PI / 2 - 0.01 && zenith + angle >= -Math.PI / 2 + 0.01) {
            zenith += angle;
            valid = false;
        }
    }
}