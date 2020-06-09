package Graphics.Rendering;

import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

public class DeathScreen extends FlatRender {

    private  static int selected = 0;
    private static final String[] options = new String[] {
            "Continue from last save",
            "Load save",
            "Exit to main menu",
            "Exit to desktop"
    };

    @Override
    public void render() {
        prepareRender();

        // render the title of the death screen : YOU DIED
        transform.setScale( new Vector3f( 0.1f * 1080f / 1920f, 0.1f, 1 ));
        transform.setPosition( new Vector3f( 0, 0.75f, 0 ) );
        renderer.setTransform(transform);
        renderString("You died", true);

        // render the background to highlight the selected option
        transform.setScale( new Vector3f( 0.85f, 0.125f, 1  ) );
        transform.setPosition( new Vector3f( 0, 0.25f - selected * 0.25f, 0 ) );
        renderer.setTransform( transform );
        renderer.renderTile(GUIElements.BACKGROUND.getTexture(), 0, 0, TileRenderer.FLOOR);

        // render the options in the death screen
        transform.setScale( new Vector3f( 0.06f * 1080f / 1920f, 0.06f, 1  ) );
        for (int i = 0; i < options.length; i++) {
            String option = options[i];

            transform.setPosition( new Vector3f( 0, 0.25f - i * 0.25f, 0 ) );
            renderer.setTransform( transform );

            renderString(option, i != selected);
        }
    }

    /**
     * changes the selected option by one and than takes it modulo all the available options, so we can wrap around
     *
     * @param up whether {@code selectedOption} needs to be in- or decremented
     */
    public static void changeSelected(boolean up) {
        if (up) {
            selected--;
        } else {
            selected++;
        }
        selected = (selected + options.length) % options.length;
    }

    public static void resetSelected() {
        selected = 0;
    }

    public static int getSelected() {
        return selected;
    }
}
