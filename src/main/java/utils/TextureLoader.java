package utils;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureLoader {
    public static int loadTexture(String filepath) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        String resourcePath = "/textures/" + filepath;

        stbi_set_flip_vertically_on_load(true);

        ByteBuffer imageBuffer;
        try {
            imageBuffer = ioResourceToByteBuffer(resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("Nepodařilo se najít texturu: " + resourcePath, e);
        }

        ByteBuffer image = stbi_load_from_memory(imageBuffer, width, height, channels, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load a texture file! " + System.lineSeparator() + stbi_failure_reason());
        }

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

        stbi_image_free(image);

        return textureId;
    }

    private static ByteBuffer ioResourceToByteBuffer(String resource) throws IOException {
        ByteBuffer buffer;

        InputStream source = TextureLoader.class.getResourceAsStream(resource);

        if (source == null) {
            throw new IOException("Resource not found: " + resource);
        }

        try (ReadableByteChannel rbc = Channels.newChannel(source)) {
            buffer = BufferUtils.createByteBuffer(1024 * 1024);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) break;

                if (buffer.remaining() == 0) {
                    ByteBuffer newBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 2);
                    buffer.flip();
                    newBuffer.put(buffer);
                    buffer = newBuffer;
                }
            }
        }
        buffer.flip();
        return buffer;
    }
}