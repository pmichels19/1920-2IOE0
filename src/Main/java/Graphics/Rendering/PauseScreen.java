package Graphics.Rendering;

import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

public class PauseScreen extends FlatRender {
    /*
    the option that is selected by the player in the pause menu, mapped to the options array
    */
    private static int selectedOption = 0;
    private static final String[] options = new String[] { "Return to Game","save", "exit to main menu", "exit to desktop" };

    @Override
    public void render() {
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
        renderString("game paused", true);

        // scale the letters down a bit for the option selection
        transform.setScale( new Vector3f( 0.06f * 1080f / 1920f, 0.06f, 1 ) );
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            transform.setPosition(new Vector3f(0, 0.25f - i * 0.25f, 0));
            renderer.setTransform(transform);
            renderString(option, selectedOption != i);
        }
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
        selectedOption = (selectedOption + options.length) % options.length;
    }

    public static int getSelectedOption() {
        return selectedOption;
    }
}