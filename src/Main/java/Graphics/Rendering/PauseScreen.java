package Graphics.Rendering;

import Levels.Assets.Tiles.GUIElements;
import Levels.Framework.joml.Vector3f;

public class PauseScreen extends FlatRender {
    public PauseScreen() {
        super(new String[] { "Return to Game","save", "exit to main menu", "exit to desktop" });
    }

    @Override
    public void render() {
        prepareRender();

        // firstly we render the bar to show what option was selected in the pause screen
        transform.setScale( new Vector3f( 0.65f, 0.1f, 1 ) );
        transform.setPosition( new Vector3f( 0, 0.25f - (0.25f * selected), 0 ) );
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
            renderString(option, selected != i);
        }
    }
}