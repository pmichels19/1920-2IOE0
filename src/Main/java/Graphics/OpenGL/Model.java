package Graphics.OpenGL;

import static org.lwjgl.opengl.GL20.*;

public class Model {
    private static Model[] models;

    // the amount in the model
    private final int drawCount;

    // the version id
    private final int v_id;

    // the texture id
    private final int t_id;

    // the normal id
    private final int n_id;

    /**
     * returns the 5 basic models that you can use to render. They will be initialised upon the first call of this method
     *
     * @return {@code models}
     */
    public static Model[] getModels() {
        if (models == null) {
            // the texture coordinates are the same for all models
            final float[] textures = new float[]{ 0, 1,       1, 1,       1, 0,       0, 0 };
            // we only allow the use of 5 different models, one for each of:
            models = new Model[]{
                    // the top of a cube
                    new Model(
                            new float[]{ 1, -1, 2,       -1, -1, 2,      -1, 1, 2,       1, 1, 2 },
                            textures,
                            new float[] {
                                    0, 0, 1,
                                    0, 0, 1,
                                    0, 0, 1,
                                    0, 0, 1
                            }
                    ),
                    // the right side of a cube
                    new Model(
                            new float[]{ 1, -1, 0,       1, 1, 0,        1, 1, 2,        1, -1, 2 },
                            textures,
                            new float[] {
                                    1, 0, 0,
                                    1, 0, 0,
                                    1, 0, 0,
                                    1, 0, 0
                            }
                    ),
                    // the bottom of a cube
                    new Model(
                            new float[]{ 1, -1, 0,       -1, -1, 0,      -1, 1, 0,       1, 1, 0 },
                            textures,
                            new float[] {
                                    0, 0, 1,
                                    0, 0, 1,
                                    0, 0, 1,
                                    0, 0, 1
                            }
                    ),
                    // the front of a cube
                    new Model(
                            new float[]{ 1, -1, 0,       -1, -1, 0,      -1, -1, 2,      1, -1, 2 },
                            textures,
                            new float[] {
                                    0, -1, 0,
                                    0, -1, 0,
                                    0, -1, 0,
                                    0, -1, 0
                            }
                    ),
                    // the left side of a cube
                    new Model(
                            new float[]{ -1, -1, 0,      -1, 1, 0,       -1, 1, 2,       -1, -1, 2 },
                            textures,
                            new float[] {
                                    -1, 0, 0,
                                    -1, 0, 0,
                                    -1, 0, 0,
                                    -1, 0, 0
                            }
                    )
            };
        }

        return models;
    }

    private Model(float[] vertices, float[] texCoords, float[] normals) {
        drawCount = vertices.length / 3;

        v_id = glGenBuffers();
        // bind v_id to GL_ARRAY_BUFFER
        glBindBuffer(GL_ARRAY_BUFFER, v_id);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        t_id = glGenBuffers();
        // bind t_id to GL_ARRAY_BUFFER
        glBindBuffer(GL_ARRAY_BUFFER, t_id);
        glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW);

        n_id = glGenBuffers();
        // bind t_id to GL_ARRAY_BUFFER
        glBindBuffer(GL_ARRAY_BUFFER, n_id);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

        // unbind from the array buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // the index id
        int i_id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, i_id);
    }

    public void render() {
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, v_id);
        glVertexAttribPointer(0, 3, GL_FLOAT, false,0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, t_id);
        glVertexAttribPointer(1, 2, GL_FLOAT, false,0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, n_id);
        glVertexAttribPointer(2, 3, GL_FLOAT, false,0, 0);

        glDrawArrays(GL_QUADS, 0, drawCount);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
    }
}
