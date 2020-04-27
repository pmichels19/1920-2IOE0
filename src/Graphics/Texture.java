package Graphics;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

    private int id;
    private int width;
    private int height;

    /**
     * Creates a new Texture object with the texture file specified in the String {@code filepath}
     *
     * @param filepath the path to a texture
     */
    public Texture (String filepath) {
        BufferedImage bufImg;

        try {
            bufImg = ImageIO.read( new File(filepath) );

            width = bufImg.getWidth();
            height = bufImg.getHeight();

            int[] pixelValues = bufImg.getRGB(0, 0, width, height, null, 0, width);

            ByteBuffer pixels = toByteBuffer(pixelValues);

            // we set the id
            id = glGenTextures();

            // bind the texture
            glBindTexture(GL_TEXTURE_2D, id);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Transforms the color values found in the {@code values} array into a ByteBuffer for compatibility with openGL
     *
     * @param values the color values to transform
     * @return {@code pixels}
     */
    private ByteBuffer toByteBuffer(int[] values) {
        // there are 4 color variables: r, g, b and a, so multiply width * height by 4
        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

        // loop over all the pixels in the texture provided
        for (int i = 0; i < width; i++) {

            // loop over all the color values in said pixel
            for (int j = 0; j < height; j++) {
                int pixel = values[i * width + j];

                // take bitwise ands with the pixel value to obtain colors
                pixels.put( (byte) ((pixel >> 16) & 255) );      // RED
                pixels.put( (byte) ((pixel >> 8) & 255) );       // GREEN
                pixels.put( (byte) (pixel & 255) );              // BLUE
                pixels.put( (byte) ((pixel >> 24) & 255) );      // ALPHA
            }
        }

        // openGL wants the texture flipped for some reason
        pixels.flip();

        return pixels;
    }

    /**
     * binds the texture so it can be displayed on screen
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
}
