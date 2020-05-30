package Graphics.OpenGL;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    private final int id;

    /**
     * Creates a new Texture object with the texture file specified in the String {@code filepath}
     *
     * @param filepath the path to a texture
     */
    public Texture (String filepath) {
        // the stbi_load requires buffers to store the width and height in
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);

        // use STBImage to load the texture
        ByteBuffer data = stbi_load( filepath, width, height, components, 4 );

        // generate a texture id
        id = glGenTextures();
        // get the width and height of the texture
        int img_width = width.get();
        int img_height = height.get();

        // bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img_width, img_height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    }

    /**
     * binds the texture so it can be displayed on screen
     */
    public void bind( int sampler ) {
        if (sampler >= 0 && sampler <= 31) {
            glActiveTexture(GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, id);
        }
    }
}
