package utils;

import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureLoader {
    public static int loadTexture(String filepath) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        String fixedFilePath = "src/main/resources/textures/" + filepath;

        // STB image načítá obrázky shora dolů, OpenGL zdola nahoru, takže obrázek převrátíme
        stbi_set_flip_vertically_on_load(true);

        // Načtení obrázku do ByteBufferu
        ByteBuffer image = stbi_load(fixedFilePath, width, height, channels, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load a texture file! " + System.lineSeparator() + stbi_failure_reason());
        }

        // Vygenerování ID textury v OpenGL
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Nastavení parametrů textury (jak se má chovat při zvětšení/zmenšení)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Odeslání dat do grafické karty
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

        // Uvolnění paměti z RAM (už to je ve VRAM grafiky)
        stbi_image_free(image);

        return textureId;
    }
}