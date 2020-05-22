package Graphics.Rendering;

import Levels.Assets.Tiles.Letter;
import Levels.Framework.joml.Vector3f;

import static java.lang.Math.*;

public class PauseScreen extends FlatRender {
    // whether the game has been paused by the player
    private boolean isPaused;

    public PauseScreen() {
        super();
        isPaused = false;
    }

    @Override
    public void render() {
        transform.setScale( new Vector3f( 1080f/1920f, 1, 1 ) );

        prepareRender();
        renderWord("test", false);
    }

    /**
     * Renders the word {@code word} in the input, in white or black text, depending on {@code white}
     *
     * @param toRender the word to render
     * @param white whether the text is to be white or black
     */
    private void renderWord(String toRender, boolean white) {
        renderer.renderTile(Letter.valueOf("B_WHITE").getTexture(), 0, 0, TileRenderer.FLOOR );
    }
}