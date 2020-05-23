package Graphics.Rendering;

import Graphics.OpenGL.Shader;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Assets.Tiles.Letter;

import static java.lang.Character.toUpperCase;

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
     * renders the flat object(s) as specified in the subclasses of the FlatRender
     */
    public abstract void render();

    /**
     * prepares the shader, camera and transform for rendering
     */
    public void prepareRender() {
        renderer.setShader(shader);
        renderer.setCamera(camera);
        renderer.setTransform(transform);
    }

    /**
     * Renders the word {@code word} in the input, in white or black text, depending on {@code white}.
     * This method assumes the shader and its corresponding transform and camera are prepared already
     *
     * @param toRender the word to render
     * @param white whether the text is to be white or black
     */
    void renderString(String toRender, boolean white) {
        // we check if toRender has an even length as this would impact where the center of the text is
        boolean evenLength = toRender.length() % 2 == 0;
        // we calculate the middle of the text
        int mid = toRender.length() / 2;

        for ( int i = 0; i < toRender.length(); i++ ) {
            String c = String.valueOf( toUpperCase( toRender.charAt(i) ) );
            // deal with the special case of spaces
            if ( c.equals(" ") ) {
                c = "SPACE";
            }

            renderer.renderTile(
                    // get the correct letter, white or black from the Letter enum
                    Letter.valueOf( white ? c + "_WHITE" : c ).getTexture(),
                    // if toRender has even length, we want to displace by an additional half
                    evenLength ? i - mid + 0.5f : i - mid,
                    0,
                    TileRenderer.FLOOR);
        }
    }

    /**
     * Renders the word {@code word} in the input, in white or black text, depending on {@code white}.
     * This method assumes the shader and its corresponding transform and camera are prepared already
     *
     * @param toRender the word to render
     * @param white whether the text is to be white or black
     * @param x the x coordinate to add to the normal x coordinate of the {@code renderWord} method
     * @param y the y coordinate to render at
     */
    void renderString(String toRender, boolean white, float x, float y) {
        // we check if toRender has an even length as this would impact where the center of the text is
        boolean evenLength = toRender.length() % 2 == 0;
        // we calculate the middle of the text
        int mid = toRender.length() / 2;

        for ( int i = 0; i < toRender.length(); i++ ) {
            String c = String.valueOf( toUpperCase( toRender.charAt(i) ) );
            // deal with the special case of spaces
            if ( c.equals(" ") ) {
                c = "SPACE";
            }

            renderer.renderTile(
                    // get the correct letter, white or black from the Letter enum
                    Letter.valueOf( white ? c + "_WHITE" : c ).getTexture(),
                    // if toRender has even length, we want to displace by an additional half
                    (evenLength ? i - mid + 0.5f : i - mid) + x,
                    y,
                    TileRenderer.FLOOR);
        }
    }
}
