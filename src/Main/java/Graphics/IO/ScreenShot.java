package Graphics.IO;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class ScreenShot {
    public static void takeScreenShot(String filePath) {
        glReadBuffer( GL_FRONT );

        int bpp = 4;
        ByteBuffer buffer = BufferUtils.createByteBuffer( 1920 * 1080 * bpp );

        glReadPixels(0, 0, 1920, 1080, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        File file = new File( filePath  );
        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < 1920; x++)
        {
            for(int y = 0; y < 1080; y++)
            {
                int i = (x + (1920 * y)) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, 1080 - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
