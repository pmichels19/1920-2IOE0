package Graphics.Rendering;

import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

public class MainMenu extends FlatRender {
    private static int selectedOption = 0;

    /**
     * returns the selected option in the main menu
     *
     * @return {@code selectedOption}
     */
    public static int getSelectedOption() {
        return selectedOption;
    }

    /**
     * changes the selected option, if going up selectedOption is decremented, and incremented otherwise
     *
     * @param up whether to go up or down in the menu
     */
    public static void changeOption(boolean up) {
        if (up) {
            selectedOption--;
        } else {
            selectedOption++;
        }
        selectedOption = (selectedOption + 4) % 4;
    }

    /**
     * resets the selected option to 0
     */
    public static void resetSelected() {
        selectedOption = 0;
    }

    @Override
    public void render() {
        prepareRender();

        renderLogo();
        renderOptions();
    }

    /**
     * Render the logo of the game in the top left of the screen
     */
    private void renderLogo() {
        transform.setScale( new Vector3f( 0.1f * 1080f/1920f, 0.1f, 1 ) );
        transform.setPosition( new Vector3f( 0.5f, 0.75f, 0 ) );
        renderer.setTransform(transform);
        renderString("Mazes", true);

        transform.setPosition( new Vector3f( 0.5f, 0.25f, 0 ) );
        renderer.setTransform(transform);
        renderString("Magic", true);

        transform.setPosition( new Vector3f( 0.5f, 0.5f, 0 ) );
        transform.setScale( new Vector3f( 0.18f, 0.1f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0, 0, TileRenderer.FLOOR );

        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ) );
        renderer.setTransform(transform);
        renderString("Of", false);
    }

    /**
     * renders the available options in the main menu and highlights the selected option
     */
    private void renderOptions() {
        // first we draw the bar to highlight the option the player is currently hovering over
        transform.setScale( new Vector3f( 0.5f, 0.1f, 1 ) );
        transform.setPosition( new Vector3f( -1, 0.5f - (selectedOption * 0.25f), 0 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0.3f, 0, TileRenderer.FLOOR );

        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ) );

        // render all available options
        String[] options = new String[] {"Continue", "New Game", "Load Game", "Exit Game"};
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            transform.setPosition(new Vector3f(-1, 0.5f - i * 0.25f, 0));
            renderer.setTransform(transform);
            renderString(option, selectedOption != i, (1f + option.length() / 2f), 0);
        }
    }
}
