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
        renderWord("Mazes", true);

        transform.setPosition( new Vector3f( 0.5f, 0.25f, 0 ) );
        renderer.setTransform(transform);
        renderWord("Magic", true);

        transform.setPosition( new Vector3f( 0.5f, 0.5f, 0 ) );
        transform.setScale( new Vector3f( 0.18f, 0.1f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0, 0, TileRenderer.FLOOR );

        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ) );
        renderer.setTransform(transform);
        renderWord("Of", false);
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

        // the option to render
        String option;
        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ) );

        option = "Continue";
        transform.setPosition( new Vector3f( -1, 0.5f, 0 ) );
        renderer.setTransform(transform);
        renderWord( option, selectedOption != 0, (1f + option.length() / 2f), 0 );

        option = "New Game";
        transform.setPosition( new Vector3f( -1, 0.25f, 0 ) );
        renderer.setTransform(transform);
        renderWord( option, selectedOption != 1, (1f + option.length() / 2f), 0 );

        option = "Load Game";
        transform.setPosition( new Vector3f( -1, 0, 0 ) );
        renderer.setTransform(transform);
        renderWord( option, selectedOption != 2, (1f + option.length() / 2f), 0 );

        option = "Exit Game";
        transform.setPosition( new Vector3f( -1, -0.25f, 0 ) );
        renderer.setTransform(transform);
        renderWord( option, selectedOption != 3, (1f + option.length() / 2f), 0 );
    }
}
