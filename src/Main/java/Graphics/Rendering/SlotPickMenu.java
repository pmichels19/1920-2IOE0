package Graphics.Rendering;

import Graphics.OpenGL.Texture;
import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

import java.io.File;

public abstract class SlotPickMenu extends FlatRender {
    // the save slot the player is currently selecting
    private static int selectedSlot = 0;

    // the text for the three save slots
    private static final String[] slots = new String[] { "slot one", "slot two", "slot three" };

    /**
     * changes the selected slot based on the provided {@code up} boolean in the parameter
     *
     * @param up whether the change is an increment or a decrement
     */
    public static void changeSelected(boolean up) {
        if (up) {
            selectedSlot--;
        } else {
            selectedSlot++;
        }
        selectedSlot = (selectedSlot + slots.length) % slots.length;
    }

    /**
     * resets the selected slot to 0
     */
    public static void resetSelected() {
        selectedSlot = 0;
    }

    @Override
    public void render() {
        transform.setScale( new Vector3f( 0.8f, 0.25f, 1 ) );
        transform.setPosition( new Vector3f( -1, 0.35f - 0.5f * selectedSlot, 0 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0.2f, 0, TileRenderer.FLOOR );

        // set the scale for rendering the text
        transform.setScale( new Vector3f( 0.03f * 1080f / 1920f, 0.03f, 1 ) );
        // we can then render them in a loop
        for (int i = 0; i < slots.length; i++) {
            transform.setPosition( new Vector3f( -1, 0.5f - 0.5f * i, 0 ) );
            renderer.setTransform(transform);
            renderString(slots[i], selectedSlot != i, 1 + slots[i].length() / 2f, 0);
        }

        // if the selected slot already contains a save file, show a screenshot of that file for reference
        if ( (new File( "src/Main/Java/Saves/Slot_" + selectedSlot + "/test.png" )).isFile() ) {
            // FIXME: we can not make a new texture every time, that will slow down openGL
            Texture texture = new Texture( "src/Main/Java/Saves/Slot_" + selectedSlot + "/test.png" );

            transform.setScale( new Vector3f( 0.2f, 0.2f, 1 ) );
            transform.setPosition( new Vector3f( -0.2f, 0.35f - 0.5f * selectedSlot, 0 ) );
            renderer.setTransform(transform);
            renderer.renderTile( texture, 0, 0, TileRenderer.FLOOR );
        }

        // TODO: we now only show a screenshot, maybe we also want to show what level the player was on
    }
}
