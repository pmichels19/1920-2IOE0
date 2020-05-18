package Main;

import Graphics.OpenGL.Shader;
import Graphics.TileRenderer;
import Graphics.Transforming.Camera;
import Graphics.Transforming.Transform;
import Levels.Assets.Tiles.GUIElements;
import Levels.Assets.Tiles.Wall;
import Levels.Characters.Player;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector3f;

import static java.lang.Math.toRadians;

public class GUI {

    // the player to draw the GUI for
    private Player player;
    private TileRenderer renderer;

    // stuff needed for rendering
    private Camera camera;
    private Transform transform;

    /**
     * initializes a new GUI for the singleton player
     */
    public GUI() {
        // we need the player for data such as health, mana inventory etc.
        player = Player.getInstance();
        // and a tilerenderer to render the HUD piece by piece
        renderer = TileRenderer.getInstance();

        // we use an unchanged camera with an empty transform to easily render the layout
        camera = new Camera();
        transform = new Transform();
    }

    /**
     * renders the GUI
     */
    public void render() {
        renderResourceBars();
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
        float health = ( (float) player.getCurrentHealth() ) / ( (float) player.getMaxHealth() );
        float mana = ( (float) player.getCurrentMana() ) / ( (float) player.getMaxMana() );

        // render the health bar and mana bar
        transform.setScale( new Vector3f( 0.19f * health, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.HEALTH.getTexture(), 0.5f, 1.75f, TileRenderer.FLOOR );

        transform.setScale( new Vector3f( 0.19f * mana, 0.04f, 1 ) );
        renderer.setTransform(transform);
        renderer.renderTile( GUIElements.MANA.getTexture(), 0.5f, 0.5f, TileRenderer.FLOOR );
    }
}
