package Graphics;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Renderer {
    // the amount of times this has been drawn
    private int drawCount;

    // the version id
    private int v_id;

    // the texture id
    private int t_id;

    // the index id
    private int i_id;

    public Renderer(float[] vertices, float[] texCoords) {
        drawCount = vertices.length / 3;

        v_id = glGenBuffers();
        // bind v_id to GL_ARRAY_BUFFER
        glBindBuffer(GL_ARRAY_BUFFER, v_id);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        t_id = glGenBuffers();
        // bind t_id to GL_ARRAY_BUFFER
        glBindBuffer(GL_ARRAY_BUFFER, t_id);
        glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW);

        // unbind from the array buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        i_id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
    }

    public void render() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glBindBuffer(GL_ARRAY_BUFFER, v_id);
        glVertexPointer(3, GL_FLOAT, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, t_id);
        glTexCoordPointer(2, GL_FLOAT, 0, 0);

        glDrawArrays(GL_QUADS, 0, drawCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    }
}
