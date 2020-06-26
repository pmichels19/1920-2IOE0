package Graphics.Rendering;

import Graphics.OpenGL.Texture;
import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;
import Main.SaveManager;

public abstract class SlotPickMenu extends FlatRender {
    public SlotPickMenu() {
        super(new String[] { "slot one", "slot two", "slot three" });
    }

    @Override
    public void render() {
        transform.setScale( new Vector3f( 0.8f, 0.25f, 1 ) );
        transform.setPosition( new Vector3f( -1, 0.35f - 0.5f * selected, 0 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0.2f, 0, TileRenderer.FLAT );

        // set the scale for rendering the text
        transform.setScale( new Vector3f( 0.03f * 1080f / 1920f, 0.03f, 1 ) );
        // we can then render them in a loop
        for (int i = 0; i < options.length; i++) {
            transform.setPosition( new Vector3f( -1, 0.5f - 0.5f * i, 0 ) );
            renderer.setTransform(transform);
            renderString(options[i], selected != i, 1 + options[i].length() / 2f, 0);
        }

        // get the screenshot related to the save slot the player is hovering over
        Texture texture = SaveManager.getTexture( selected );

        transform.setScale( new Vector3f( 0.2f, 0.2f, 1 ) );
        transform.setPosition( new Vector3f( -0.2f, 0.35f - 0.5f * selected, 0 ) );
        renderer.setTransform(transform);
        renderer.renderTile( texture, 0, 0, TileRenderer.FLAT );
    }

    /**
     * renders a title to display above the slot picker
     *
     * @param title the title of the screen
     */
    void renderTitle(String title) {
        transform.setPosition( new Vector3f(-1, 0.75f, 0) );
        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ) );
        renderer.setTransform(transform);
        renderString(title, true, 1 + (title.length() / 2f), 0);
    }
}
