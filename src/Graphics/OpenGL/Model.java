package Graphics.OpenGL;

import static org.lwjgl.opengl.GL20.*;

public class Model {
    // the amount of times this has been drawn
    private int drawCount;

    // the version id
    private int v_id;

    // the texture id
    private int t_id;

    // the index id
    private int i_id;

    public Model(float[] vertices, float[] texCoords) {
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
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, v_id);
        glVertexAttribPointer(0, 3, GL_FLOAT, false,0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, t_id);
        glVertexAttribPointer(1, 2, GL_FLOAT, false,0, 0);

        glDrawArrays(GL_QUADS, 0, drawCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }
}
