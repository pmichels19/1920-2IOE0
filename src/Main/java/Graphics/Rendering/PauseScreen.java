package Graphics.Rendering;

import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

public class PauseScreen extends FlatRender {
    // whether the game has been paused by the player
    private static boolean paused = false;

    /*
    the option that is selected by the player in the pause menu, mapped as:
    0: Save
    1: Save and quit
    2: quit, without saving
    */
    private static int selectedOption = 0;

    @Override
    public void render() {
        if ( paused ) {
            prepareRender();

            // firstly we render the bar to show what option was selected in the pause screen
            transform.setScale( new Vector3f( 0.65f, 0.1f, 1 ) );
            transform.setPosition( new Vector3f( 0, 0.25f - (0.25f * selectedOption), 0 ) );
            renderer.setTransform(transform);
            renderer.renderTile(GUIElements.BACKGROUND.getTexture(), 0, 0, TileRenderer.FLOOR);

            // render the text of the pause screen
            transform.setScale(new Vector3f(0.1f * 1080f / 1920f, 0.1f, 1));
            transform.setPosition( new Vector3f( 0, 0.75f, 0 ) );
            renderer.setTransform(transform);
            renderWord("game paused", true);

            // scale the letters down a bit for the option selection
            transform.setScale( new Vector3f( 0.06f * 1080f / 1920f, 0.06f, 1 ) );

            // render our first option, which is to save the game
            transform.setPosition( new Vector3f( 0, 0.25f, 0 ) );
            renderer.setTransform(transform);
            renderWord("Save", selectedOption != 0);

            // render our second option, which is to save the game and then quit
            transform.setPosition( new Vector3f( 0, 0, 0 ) );
            renderer.setTransform(transform);
            renderWord("Save and quit", selectedOption != 1);

            // and finally the third option, which is to just quit, without saving
            transform.setPosition( new Vector3f( 0, -0.25f, 0 ) );
            renderer.setTransform(transform);
            renderWord("quit", selectedOption != 2);
        }
    }

    /**
     * toggles the pause screen on and off and resets the selected option to 0
     */
    public static void togglePause() {
        paused = !paused;
        selectedOption = 0;
    }

    /**
     * returns whether the game has been paused or not
     *
     * @return {@code paused}
     */
    public static boolean isPaused() {
        return paused;
    }

    /**
     * changes the selected option by one and than takes it modulo all the available options, so we can wrap around
     *
     * @param up whether {@code selectedOption} needs to be in- or decremented
     */
    public static void changeSelected( boolean up ) {
        if (up) {
            selectedOption--;
        } else {
            selectedOption++;
        }
        selectedOption = (selectedOption + 3) % 3;
    }

    public static int getSelectedOption() {
        return selectedOption;
    }
}