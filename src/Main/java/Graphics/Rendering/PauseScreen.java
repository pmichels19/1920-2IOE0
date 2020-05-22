package Graphics.Rendering;

import Levels.Assets.Tiles.Letter;
import Levels.Framework.joml.Vector3f;

import static java.lang.Character.toUpperCase;

public class PauseScreen extends FlatRender {
    // whether the game has been paused by the player
    private boolean paused;

    public PauseScreen() {
        super();
        paused = false;
    }

    @Override
    public void render() {
        transform.setScale( new Vector3f( 0.1f * 1080f/1920f, 0.1f, 1 ) );

        prepareRender();
        renderWord("game paused", true);
    }

    /**
     * toggles the pause screen on and off
     */
    public void togglePause() {
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    /**
     * Renders the word {@code word} in the input, in white or black text, depending on {@code white}.
     * This method assumes the shader and its corresponding transform and camera are prepared already
     *
     * @param toRender the word to render
     * @param white whether the text is to be white or black
     */
    private void renderWord(String toRender, boolean white) {
        // we check if toRender has an even length as this would impact where the center of the text is
        boolean evenLength = toRender.length() / 2f % 2 == 0;
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
}