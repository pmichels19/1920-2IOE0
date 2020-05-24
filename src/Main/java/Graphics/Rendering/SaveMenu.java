package Graphics.Rendering;

import Levels.Framework.joml.Vector3f;

public class SaveMenu extends SlotPickMenu {
    @Override
    public void render() {
        transform.setPosition( new Vector3f(-1, 0.75f, 0) );
        transform.setScale( new Vector3f( 0.06f * 1080f/1920f, 0.06f, 1 ) );
        renderer.setTransform(transform);
        renderString("Pick a slot to save to", true, 12, 0);

        super.render();
    }
}
