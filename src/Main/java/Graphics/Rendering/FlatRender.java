package Graphics.Rendering;

import Graphics.OpenGL.Shader;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;

/**
 * class for rendering things with the flatShader
 */
public abstract class FlatRender {
    /**
     * the TileRenderer object that is to be used by all subclasses
     */
    static final TileRenderer renderer = TileRenderer.getInstance();

    /**
     * the Shader that can be used for all subclasses, as to prevent making multiple shader objects
     */
    static final Shader shader = new Shader("flatShader");

    /**
     * the camera that can be moved around for the FlatRenders
     */
    final Camera camera;

    /**
     * The transformations to be applied to the renders
     */
    final Transform transform;

    /**
     * in the constructor we just initialize a new(, empty) camera and transform object
     */
    public FlatRender() {
        camera = new Camera();
        transform = new Transform();
    }

    /**
     * prepares the shader, camera and transform for rendering
     */
    public void prepareRender() {
        renderer.setShader(shader);
        renderer.setCamera(camera);
        renderer.setTransform(transform);
    }

    /**
     * renders the flat object(s) as specified in the subclasses of the FlatRender
     */
    public abstract void render();
}
