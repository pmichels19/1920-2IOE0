package Graphics.Rendering;

import Levels.Assets.Items.Item;
import Levels.Assets.Tiles.GUIElements;
import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

public class GUI extends FlatRender {

    // the player to draw the GUI for
    private Player player;
    public GUI() {
        super(new String[]{});
    }

    @Override
    public void render() {
        player = Player.getInstance();
        prepareRender();

        renderResourceBars();
        renderInventory();
    }

    private void renderResourceBars() {
        // make sure the camera is set correctly
        renderer.setCamera(camera);

        // we want to draw the resourcebars in the top left of the screen
        transform.setPosition( new Vector3f(-1, 1, 0) );

        // calculate the length of the resource bars
        float maxHealthPercent = ( (float) player.getMaxHealth() ) / 100f;
        float maxManaPercent = ( (float) player.getMaxMana() ) / 100f;
        float healthPercent = ( (float) player.getHealth() ) / 100f;
        float manaPercent = ( (float) player.getMana() ) / 100f;

        // first we render the max health bar
        transform.setScale( new Vector3f( 0.19f * maxHealthPercent, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.MISSING_HEALTH.getTexture(), 0.5f, -1.75f, TileRenderer.FLAT );

        // then the current health bar
        transform.setScale( new Vector3f( 0.19f * healthPercent, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.HEALTH.getTexture(), 0.5f, -1.75f, TileRenderer.FLAT );

        // then the max mana bar
        transform.setScale( new Vector3f( 0.19f * maxManaPercent, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.MISSING_MANA.getTexture(), 0.5f, -0.5f, TileRenderer.FLAT );

        // and finally the current mana bar
        transform.setScale( new Vector3f( 0.19f * manaPercent, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.MANA.getTexture(), 0.5f, -0.5f, TileRenderer.FLAT );
    }

    private void renderInventory() {
        // retrieve the inventory
        Item[] inventory = player.getInventory();

        // render the white banner to highlight the selected inventory slot
        transform.setScale( new Vector3f(0.17f, 0.1f, 1) );
        transform.setPosition( new Vector3f( 1, (2f / 3f) - ((1f / 3f) * player.getSelectedItem()), 0 ) );
        renderer.setTransform( transform );
        renderer.renderTile( GUIElements.BACKGROUND.getTexture(), 0, 0, TileRenderer.FLAT );

        // loop over all the inventory slots and render them
        for (int i = 0; i < inventory.length; i++) {
            transform.setScale( new Vector3f(0.09f * (1080f/1920f), 0.09f, 1 ) );
            transform.setPosition(new Vector3f(1, (2f - i) / 3f, 0));
            renderer.setTransform(transform);
            renderer.renderTile(GUIElements.ITEM_BACKGROUND.getTexture(), -1, 0, TileRenderer.FLAT);

            // draw the items
            transform.setScale( new Vector3f(0.08f * (1080f/1920f), 0.08f, 1 ) );
            renderer.setTransform(transform);
            renderer.renderTile(
                    inventory[i] == null ? GUIElements.ITEM_BACKGROUND.getTexture() : inventory[i].getTexture(),
                    -(0.09f/0.08f),
                    0,
                    TileRenderer.FLAT
            );
        }
    }
}
