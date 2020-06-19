package Graphics.Rendering;

import Levels.Framework.joml.Vector3f;

public class VictoryScreen extends FlatRender {
    public VictoryScreen() {
        super(new String[]{});
    }

    @Override
    public void render() {
        prepareRender();

        transform.setScale( new Vector3f( 0.1f * 1080f/1920f, 0.1f, 1 ));
        transform.setPosition( new Vector3f( 0, 0.75f, 0) );
        renderer.setTransform( transform );
        renderString("Congratulations", true);

        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ));
        transform.setPosition( new Vector3f( 0, 0.5f, 0) );
        renderer.setTransform( transform );
        renderString("You beat the game", true);
    }
}
