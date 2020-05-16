package Graphics.OpenGL;

import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Framework.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int program;

    // the vertex shader
    private int vs;

    // the fragment shader
    private int fs;

    private final int WORLD, OBJECT, PROJECTION;

    public Shader(String filename) {
        program = glCreateProgram();

        // create and compile the vertex shader
        vs = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vs, readFile(filename + ".vs"));
        glCompileShader(vs);

        // check if the shader compiled correctly
        if (glGetShaderi(vs, GL_COMPILE_STATUS) != GL_TRUE) {
            // if something went wrong, we print an error and exit
            System.err.println( glGetShaderInfoLog(vs) );
            System.exit(1);
        }

        // do the same for the fragment shader
        fs = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fs, readFile(filename + ".fs"));
        glCompileShader(fs);

        if (glGetShaderi(fs, GL_COMPILE_STATUS) != GL_TRUE) {
            System.err.println( glGetShaderInfoLog(fs) );
            System.exit(1);
        }

        // attach the shaders to our program
        glAttachShader(program, vs);
        glAttachShader(program, fs);

        // the attributes found in the vertex shader
        glBindAttribLocation(program, 0, "vertices");
        glBindAttribLocation(program, 1, "textures");

        glLinkProgram(program);

        if ( glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE ) {
            // if the linking failed, print the error and exit
            System.err.println( glGetProgramInfoLog(program) );
            System.exit(1);
        }

        glValidateProgram(program);

        if ( glGetProgrami(program, GL_VALIDATE_STATUS) != GL_TRUE ) {
            // if the validating failed, print the error and exit
            System.err.println( glGetProgramInfoLog(program) );
            System.exit(1);
        }

        WORLD = glGetUniformLocation(program, "transformWorld");
        OBJECT = glGetUniformLocation(program, "transformObject");
        PROJECTION = glGetUniformLocation(program, "cameraProjection");
    }

    public void bind() {
        glUseProgram(program);
    }

    public void setUniform(String name, int value) {
        // find where the provided uniform is stored
        int location = glGetUniformLocation(program, name);

        // test if the location is a valid one
        if (location != -1) {
            // replace the uniform if it is
            glUniform1i(location, value);
        }
    }

    public void setCamera(Camera camera) {
        if (PROJECTION != -1)  {
            float[] matrix = new float[16];
            camera.getProjection().get(matrix);

            glUniformMatrix4fv(PROJECTION, false, matrix);
        }

        if (WORLD != -1)  {
            float[] matrix = new float[16];
            camera.getTransformation().get(matrix);

            glUniformMatrix4fv(WORLD, false, matrix);
        }
    }

    public void setTransform(Transform transform) {
        if (OBJECT != -1)  {
            float[] matrix = new float[16];
            transform.getTransformation().get(matrix);

            glUniformMatrix4fv(OBJECT, false, matrix);
        }
    }

    public void setUniform(String name, Matrix4f value) {
        // find where the provided uniform is stored
        int location = glGetUniformLocation(program, name);

        // fill a floatBuffer with the values in the matrix provided
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);

        // test if the location is a valid one
        if (location != -1) {
            // replace the uniform if it is
            glUniformMatrix4fv(location, false, buffer);
        }
    }

    private String readFile(String filename) {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader;

        try {
            reader = new BufferedReader( new FileReader(new File("src/Graphics/Shaders/" + filename) ) );
            String line;

            while ( (line = reader.readLine()) != null ) {
                builder.append(line);

                // we have to append a new line, otherwise the shader will not work
                builder.append("\n");
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
