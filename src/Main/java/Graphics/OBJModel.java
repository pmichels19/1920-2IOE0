package Graphics;

import Graphics.OpenGL.Shader;
import Graphics.OpenGL.Texture;
import Levels.Framework.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class OBJModel {
    private static final Vector3f DEFAULT_COLOUR = new Vector3f(1.0f, 1.0f, 1.0f);

    // the amount of vertices in the model
    private final int vertexCount;

    // the id of the VAO
    private int vaoId;

    // list that holds the vboIDs
    private List<Integer> vboIdList;

    // Color
    private Vector3f colour;

    // Texture
    private Texture texture;

    // Normal map
    private Texture normalMap;

    /**
     * Creates a model from a specified list of vertices, texture coordinates, normal vectors and indices.
     *
     * @param vertices list of vertex positions
     * @param textCoords list of texture coordinates
     * @param normals list of normal vectors
     * @param indices list of indices
     */
    public OBJModel(float[] vertices, float[] textCoords, float[] normals, int[] indices) {
        FloatBuffer verticesBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            colour = OBJModel.DEFAULT_COLOUR;
            vertexCount = indices.length;
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
            vboIdList = new ArrayList<>();
            // Create the VAO and bind it
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            //Create VBO, bind it and put positions data into it
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
            verticesBuffer.put(vertices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // set the texture VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // set the normal VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Set the the indices VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            // Unbind the VBO
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            // Unbind the VAO
            glBindVertexArray(0);


        } finally {
            // Free the heap of allocated memory
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);

            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);

            }

            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);

            }

            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);

            }
        }


    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getColour() {
        return this.colour;
    }

    public Texture getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
    }

    public boolean hasNormalMap(){
        return this.normalMap != null;
    }

    /**
     * Renders the mesh with a specific shader
     * @param shader shader to bind uniforms to
     */
    public void render(Shader shader) {
        if (isTextured()) {
            this.texture.bind(0);
            shader.setUniform("sampler", 0);
        }

         if (hasNormalMap()) {
            shader.setUniform("normalMap", 1);
            shader.setUniform("normalMapping", 1);
            normalMap.bind(1);
        } else {
             shader.setUniform("normalMapping", 0);
         }



        glEnable(GL_DEPTH_TEST);
        // Bind to the VAO

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glPushMatrix();

        // Draw the indices
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
//        glDrawArrays(GL_TRIANGLES, vertexCount, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);

        glDisable(GL_DEPTH_TEST);
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void CleanUp() {

    }

}
