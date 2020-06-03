package Graphics.Rendering;

import Levels.Assets.Items.Item;
import Levels.Assets.Tiles.GUIElements;
import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

public class GUI extends FlatRender {

    // the player to draw the GUI for
    private final Player player = Player.getInstance();

    @Override
    public void render() {
        prepareRender();

        renderResourceBars();
        renderInventory();
    }

    private void renderResourceBars() {
        // make sure the camera is set correctly
        renderer.setCamera(camera);

        // we want to draw the resourcebars in the bottom left of the screen
        transform.setPosition( new Vector3f(-1, -1, 0) );

        // render the background for the resource bars
        transform.setScale( new Vector3f( 0.2f, 0.1f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0.5f, 0.5f, TileRenderer.FLOOR );

        // calculate the percentage of health and mana left
        float healthPercent = ( (float) player.getHealth() ) / ( (float) player.getMaxHealth() );
        float manaPercent = ( (float) player.getMana() ) / ( (float) player.getMaxMana() );

        // first we render the missing health bar
        transform.setScale( new Vector3f( 0.19f, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.MISSING_HEALTH.getTexture(), 0.5f, 1.75f, TileRenderer.FLOOR );

        // then the current health bar
        transform.setScale( new Vector3f( 0.19f * healthPercent, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.HEALTH.getTexture(), 0.5f, 1.75f, TileRenderer.FLOOR );

        // then the missing mana bar
        transform.setScale( new Vector3f( 0.19f, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.MISSING_MANA.getTexture(), 0.5f, 0.5f, TileRenderer.FLOOR );

        // and finally the current mana bar
        transform.setScale( new Vector3f( 0.19f * manaPercent, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.MANA.getTexture(), 0.5f, 0.5f, TileRenderer.FLOOR );
    }

    private void renderInventory() {
        // retrieve the inventory
        Item[] inventory = player.getInventory();

        // render the white banner to highlight the selected inventory slot
        transform.setScale( new Vector3f(0.17f, 0.1f, 1) );
        transform.setPosition( new Vector3f( 1, (2f / 3f) - ((1f / 3f) * player.getSelectedItem()), 0 ) );
        renderer.setTransform( transform );
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0, 0, TileRenderer.FLOOR );

        // set the scale for the inventory slots
        transform.setScale( new Vector3f(0.09f * (1080f/1920f), 0.09f, 1 ) );

        // loop over all the inventory slots and render them
        for (int i = 0; i < inventory.length; i++) {
            transform.setPosition(new Vector3f(1, (2f / 3f) - ((1f / 3f) * i), 0));
            renderer.setTransform(transform);
            renderer.renderTile(GUIElements.ITEM_BACKGROUND.getTexture(), -1, 0, TileRenderer.FLOOR);

            // draw the items
            renderer.renderTile(
                    inventory[i] == null ? GUIElements.ITEM_BACKGROUND.getTexture() : inventory[i].getTexture(),
                    -1,
                    0,
                    TileRenderer.FLOOR
            );
        }
    }
}
