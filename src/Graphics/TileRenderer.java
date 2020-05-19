package Graphics;

import Graphics.OpenGL.Model;
import Graphics.OpenGL.Shader;
import Graphics.OpenGL.Texture;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector3f;

public class TileRenderer {

    // variables to make sure the render ends up in the right position
    private Camera camera;
    private Transform transform;
    private Shader shader;

    private static TileRenderer renderer;

    // the models available for rendering
    private final Model ceilingModel;   // bound to 0
    private final Model rightModel;     // bound to 1
    private final Model floorModel;     // bound to 2
    private final Model faceModel;      // bound to 3
    private final Model leftModel;      // bound to 4

    // the bindings to the models
    public static final int CEILS = 0;
    public static final int RIGHT = 1;
    public static final int FLOOR = 2;
    public static final int FACES = 3;
    public static final int LEFTS = 4;

    /**
     * Sets up a new TileRenderer object, instantiating the basic model
     */
    private TileRenderer() {
        // the texture coordinates are the same for all models
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
                textures,
                new float[] {
                        0, 0, 1,
                        0, 0, 1,
                        0, 0, 1,
                        0, 0, 1
                }
        );

        rightModel = new Model(
                new float[] {
                        1, -1, 0,
                        1, 1, 0,
                        1, 1, 2,
                        1, -1, 2,
                },
                textures,
                new float[] {
                        1, 0, 0,
                        1, 0, 0,
                        1, 0, 0,
                        1, 0, 0
                }
        );

        floorModel = new Model(
                new float[] {
                        1, -1, 0,
                        -1, -1, 0,
                        -1, 1, 0,
                        1, 1, 0,
                },
                textures,
                new float[] {
                        0, 0, 1,
                        0, 0, 1,
                        0, 0, 1,
                        0, 0, 1
                }
        );

        faceModel = new Model(
                new float[] {
                        1, -1, 0,
                        -1, -1, 0,
                        -1, -1, 2,
                        1, -1, 2,
                },
                textures,
                new float[] {
                        0, -1, 0,
                        0, -1, 0,
                        0, -1, 0,
                        0, -1, 0
                }
        );

        leftModel = new Model(
                new float[] {
                        -1, -1, 0,
                        -1, 1, 0,
                        -1, 1, 2,
                        -1, -1, 2,
                },
                textures,
                new float[] {
                        -1, 0, 0,
                        -1, 0, 0,
                        -1, 0, 0,
                        -1, 0, 0
                }
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
        shader.setCamera(camera);
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
        shader.setTransform(transform);
    }

    /**
     * sets the shader that is to be used when rendering,
     *
     * @param shader the shader to use when rendering
     */
    public void setShader(Shader shader) {
        this.shader = shader;
    }

    /**
     * Renders a basic tile with the specified texture at the specified location
     *
     * @param texture the texture to apply to the model
     * @param x the x coordinate to draw the tile at
     * @param y the y coordinate to draw the tile at
     * @param model the model to render based on assignments above
     */
    public void renderTile(Texture texture, float x, float y, int model) {
        // bind the shader
        shader.bind();

        // calculate the position of the tile
        Matrix4f tilePosition = new Matrix4f().translate( new Vector3f(x * 2f, y * 2f, 0 ) );

        // set the shader uniforms, so the proper position and texture is used
        shader.setUniform("tilePosition", tilePosition );
        shader.setUniform("sampler", 0);

        // bind the provided texture
        texture.bind(0);

        // and finally render the selected model
        switch ( model ) {
            case CEILS:
                ceilingModel.render();
                break;
            case RIGHT:
                rightModel.render();
                break;
            case FLOOR:
                floorModel.render();
                break;
            case FACES:
                faceModel.render();
                break;
            case LEFTS:
                leftModel.render();
                break;
            default:
                throw new IllegalArgumentException( "No model with index " + model );
        }
    }
}
