package utils;

import org.lwjgl.BufferUtils;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import static org.lwjgl.opengl.GL11.*;

public class TextRenderer {
    private int textureID;
    private BufferedImage image;
    private Graphics2D g2d;
    private int width, height;

    public TextRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        init();
    }

    private void init() {
        // 1. Připravíme Java "plátno"
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = image.createGraphics();

        // Nastavení vyhlazování pro hezké písmo
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // 2. Registrace textury v OpenGL
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Nastavení parametrů textury
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    public void drawText(String text, int x, int y, Color color) {
        // Vyčistíme část obrázku (průhledná barva)
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height); // Pro jednoduchost čistíme vše, nebo jen oblast textu
        g2d.setComposite(AlphaComposite.SrcOver);

        // Namalujeme text Javo-vským způsobem
        g2d.setColor(color);
        g2d.drawString(text, x, y);

        // Teď musíme přenést pixely z BufferedImage do OpenGL textury
        updateTexture();

        // Nakonec vykreslíme čtverec s touto texturou přes obrazovku
        renderQuad();
    }

    private void updateTexture() {
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // G
                buffer.put((byte) (pixel & 0xFF));         // B
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
            }
        }
        buffer.flip();

        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    }

    private void renderQuad() {
        // Přepneme na 2D režim (Orthographic projection) - zjednodušeně pomocí glBegin
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);

        glColor4f(1, 1, 1, 1); // Reset barvy pro texturu
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex2f(-1, 1);
        glTexCoord2f(1, 0); glVertex2f(1, 1);
        glTexCoord2f(1, 1); glVertex2f(1, -1);
        glTexCoord2f(0, 1); glVertex2f(-1, -1);
        glEnd();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

//    public void resize(int w, int h) {
//        this.width = w;
//        this.height = h;
//        init(); // Re-inicializace pro novou velikost okna
//    }
}