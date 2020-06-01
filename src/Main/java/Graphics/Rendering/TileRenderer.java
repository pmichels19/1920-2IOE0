package Graphics.Rendering;

import Graphics.OpenGL.Model;
import Graphics.OpenGL.Shader;
import Graphics.OpenGL.Texture;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Characters.Character;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector3f;

public class TileRenderer {

    // variables to make sure the render ends up in the right position
    private Camera camera;
    private Transform transform;
    private Shader shader;

    private boolean normalMapping = false;
    private Texture normalMap;

    private static TileRenderer renderer;

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
    }

    /**
     * returns the singleton instance of {@code TileRenderer}
     *
     * @return {@code TileRenderer}
     */
    public static TileRenderer getInstance() {
        if (renderer == null) {
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

    /**
     * sets the world transformation which is then passed on to shader currently used by
     *
     * @param transform the transformation to apply to the render
     */
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
        this.shader.bind();
    }

    /**
     * Renders a basic tile with the specified texture at the specified location
     *
     * @param texture the texture to apply to the model
     * @param x       the x coordinate to draw the tile at
     * @param y       the y coordinate to draw the tile at
     * @param model   the model to render based on assignments above
     */
    public void renderTile(Texture texture, float x, float y, int model) {
        if (model < CEILS || model > LEFTS) {
            throw new IllegalArgumentException("model specified does not exist");
        }

        // bind the shader
        shader.bind();

        // calculate the position of the tile
        Matrix4f tilePosition = new Matrix4f().translate(new Vector3f(x * 2f, y * 2f, 0));

        // set the shader uniforms, so the proper position and texture is used
        shader.setUniform("tilePosition", tilePosition );
        shader.setUniform("diffuseMap", 0);
        shader.setUniform("normalMapping", 0);

        // bind the provided texture
        texture.bind(0);

        // bind normalMapping
        if (normalMapping) {
            shader.setUniform("normalMap", 1);
            shader.setUniform("normalMapping", 1);
            normalMap.bind(1);
            normalMapping = false;
        }

        // and finally render the selected model
        Model.getModels()[model].render();
    }


    /**
     * Renders a basic tile with the specified texture at the specified location
     *
     * @param character   the character to render based on assignments above
     */

    public void renderCharacter( Character character) {

        // bind the shader
        shader.bind();


        // set the shader uniforms, so the proper position and texture is used
//        shader.setUniform("tilePosition", tilePosition);
        shader.setUniform("diffuseMap", 0);
        shader.setUniform("normalMapping", 0);

        // bind the provided texture
        character.getModel().getTexture().bind(0);

        // and finally render the selected model
        character.render(shader);

    }

    public void addNormalMap(Texture normalMap) {
        normalMapping = true;
        this.normalMap = normalMap;
    }


}
