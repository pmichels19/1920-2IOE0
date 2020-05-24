package Graphics.Rendering;

import Graphics.OpenGL.Texture;
import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

import java.io.File;

public class NewGameMenu extends FlatRender {
    // the save slot the player is currently selecting
    private static int selectedSlot = 0;

    @Override
    public void render() {
        transform.setPosition( new Vector3f(-1, 0.75f, 0) );
        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ) );
        renderer.setTransform(transform);
        renderString("Pick a save slot", true, 8.5f, 0);

        renderSlots();
    }

    private void renderSlots() {
        transform.setScale( new Vector3f( 0.8f, 0.25f, 1 ) );
        transform.setPosition( new Vector3f( -1, 0.35f - 0.5f * selectedSlot, 0 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0.2f, 0, TileRenderer.FLOOR );

        // set the scale for rendering the text
        transform.setScale( new Vector3f( 0.03f * 1080f / 1920f, 0.03f, 1 ) );
        // the text for the three save slots
        String[] slots = new String[] { "slot one", "slot two", "slot three" };
        // we can then render them in a loop
        for (int i = 0; i < slots.length; i++) {
            transform.setPosition( new Vector3f( -1, 0.5f - 0.5f * i, 0 ) );
            renderer.setTransform(transform);
            renderString(slots[i], selectedSlot != i, 1 + slots[i].length() / 2f, 0);
        }

        // if the selected slot already contains a save file, show a screenshot of that file
        if ( (new File( "src/Main/Java/Saves/Slot_" + selectedSlot + "/test.png" )).isFile() ) {
            Texture texture = new Texture( "src/Main/Java/Saves/Slot_" + selectedSlot + "/test.png" );
            transform.setScale( new Vector3f( 0.2f, 0.2f, 1 ) );
            transform.setPosition( new Vector3f( 0, 0.35f - 0.5f * selectedSlot, 0 ) );
            renderer.setTransform(transform);
            renderer.renderTile( texture, 0, 0, TileRenderer.FLOOR );
        }
    }
}
