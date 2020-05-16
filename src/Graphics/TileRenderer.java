package Graphics;

import Graphics.OpenGL.Model;
import Graphics.OpenGL.Shader;
import Graphics.OpenGL.Texture;
import Graphics.Transforming.Camera;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Quaternionf;
import Levels.Framework.joml.Vector3f;

import static java.lang.Math.*;

public class TileRenderer {

    private Camera camera;
    private Shader shader;

    private static TileRenderer renderer;

    // the models available for rendering
    private final Model ceilingModel;   // bound to 0
    private final Model rightModel;     // bound to 1
    private final Model floorModel;     // bound to 2
    private final Model faceModel;      // bound to 3
    private final Model leftModel;      // bound to 4

    /**
     * Sets up a new TileRenderer object, instantiating the basic model
     */
    private TileRenderer() {
        // the texture coordinates are the same for all
        final float[] textures = new float[] {
                0, 1,
                1, 1,
                1, 0,
                0, 0,
        };

        // initialize the models
        ceilingModel = new Model(
                new float[] {
                        1, -1, 2,
                        -1, -1, 2,
                        -1, 1, 2,
                        1, 1, 2,
                },
                textures
        );

        rightModel = new Model(
                new float[] {
                        1, -1, 0,
                        1, 1, 0,
                        1, 1, 2,
                        1, -1, 2,
                },
                textures
        );

        floorModel = new Model(
                new float[] {
                        1, -1, 0,
                        -1, -1, 0,
                        -1, 1, 0,
                        1, 1, 0,
                },
                textures
        );

        faceModel = new Model(
                new float[] {
                        1, -1, 0,
                        -1, -1, 0,
                        -1, -1, 2,
                        1, -1, 2,
                },
                textures
        );

        leftModel = new Model(
                new float[] {
                        -1, -1, 0,
                        -1, 1, 0,
                        -1, 1, 2,
                        -1, -1, 2,
                },
                textures
        );
    }

    /**
     * returns the singleton instance of {@code TileRenderer}
     *
     * @return {@code TileRenderer}
     */
    public static TileRenderer getInstance() {
        if ( renderer == null ) {
            renderer = new TileRenderer();
        }

        return renderer;
    }

    /**
     * sets the camera, which is necessary for the camera projection when rendering
     *
     * @param camera the camera to use when rendering
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * sets the shader that is to be used when rendering,
     *
     * @param shader the shader to use when rendering
     */
    public void setShader(Shader shader) {
        this.shader = shader;
        shader.bind();
    }

    /**
     * Renders a basic tile with the specified texture at the specified location
     *
     * @param texture the texture to apply to the model
     * @param x the x coordinate to draw the tile at
     * @param y the y coordinate to draw the at
     * @param model the model to render based on assignments above
     */
    public void renderTile(Texture texture, float x, float y, int model ) {
        // calculate the position of the tile
        Matrix4f tilePosition = new Matrix4f().translate( new Vector3f(x * 2f, y * 2f, 0 ) );

        // offset the position w.r.t. the camera projection and the provided transformation matrix
        Matrix4f target = new Matrix4f();
        camera.getProjection().mul( target );
        target.mul( tilePosition );

        // set the shader uniforms, so the proper position and texture is used
        shader.setUniform("tilePosition", tilePosition );
        shader.setUniform("sampler", 0);

        // bind the provided texture
        texture.bind(0);

        // and finally render the selected model
        switch ( model ) {
            case 0:
                ceilingModel.render();
                break;
            case 1:
                rightModel.render();
                break;
            case 2:
                floorModel.render();
                break;
            case 3:
                faceModel.render();
                break;
            case 4:
                leftModel.render();
                break;
            default:
                throw new IllegalArgumentException( "No model with index " + model );
        }
    }
}
