package Graphics.Rendering;

import Levels.Framework.joml.Vector3f;

public class NewGameMenu extends FlatRender {
    @Override
    public void render() {
        transform.setPosition( new Vector3f(-1, 0.75f, 0) );
        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ) );
        renderer.setTransform(transform);
        renderString("Pick a save slot", true, 8.5f, 0);

        renderSlots();
    }

    private void renderSlots() {
        transform.setPosition( new Vector3f( -1, 0.5f, 0 ) );
        transform.setScale( new Vector3f( 0.03f * 1080f / 1920f, 0.03f, 1 ) );
        renderer.setTransform(transform);
        renderString("slot one", true, 5f, 0);

        transform.setPosition( new Vector3f( -1, 0, 0 ) );
        transform.setScale( new Vector3f( 0.03f * 1080f / 1920f, 0.03f, 1 ) );
        renderer.setTransform(transform);
        renderString("slot two", true, 5f, 0);

        transform.setPosition( new Vector3f( -1, -0.5f, 0 ) );
        transform.setScale( new Vector3f( 0.03f * 1080f / 1920f, 0.03f, 1 ) );
        renderer.setTransform(transform);
        renderString("slot three", true, 6f, 0);
    }
}
