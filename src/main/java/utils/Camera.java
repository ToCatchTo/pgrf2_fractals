package utils;

import static utils.GluUtils.gluLookAt;

public class Camera {
    private double x, y, z;
    private double azimuth, zenith;
    private boolean valid;

    // Vypočítané hodnoty kam se díváme
    private double dirX, dirY, dirZ;

    public Camera() {
        // Výchozí pozice (stejná, jakou jsi měl v Renderer.java)
        x = 40.0;
        y = 40.0;
        z = 20.0;
        // Výchozí natočení, aby kamera koukala zhruba na střed (0,0,0)
        azimuth = Math.PI * 1.25;
        zenith = -0.4;
        valid = false;
    }

    // --- VÝPOČET MATICE (Inspirace z GLCamera) ---
    private void computeMatrix() {
        // Výpočet směrového vektoru pomocí sférických souřadnic pro osu Z nahoru
        dirX = Math.cos(zenith) * Math.cos(azimuth);
        dirY = Math.cos(zenith) * Math.sin(azimuth);
        dirZ = Math.sin(zenith);

        valid = true;
    }

    public void setMatrix() {
        if (!valid) computeMatrix();

        // gluLookAt(Odkud_X, Odkud_Y, Odkud_Z, Kam_X, Kam_Y, Kam_Z, Up_X, Up_Y, Up_Z)
        gluLookAt(
                x, y, z,
                x + dirX, y + dirY, z + dirZ,
                0, 0, 1 // Osa Z je nahoře
        );
    }

    // --- POHYB (WASD) ---
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
        // Krok doprava znamená úhel o 90 stupňů (PI/2) menší než azimuth
        x += Math.cos(azimuth - Math.PI / 2) * speed;
        y += Math.sin(azimuth - Math.PI / 2) * speed;
    }

    public void left(double speed) {
        right(-speed);
    }

    // --- ROZHLÍŽENÍ (Myš) ---
    public void addAzimuth(double angle) {
        azimuth += angle;
        valid = false; // Musíme přepočítat matici
    }

    public void addZenith(double angle) {
        // Omezení, abychom si nezlomili krk (max 90 stupňů nahoru/dolů)
        if (zenith + angle <= Math.PI / 2 - 0.01 && zenith + angle >= -Math.PI / 2 + 0.01) {
            zenith += angle;
            valid = false; // Musíme přepočítat matici
        }
    }
}