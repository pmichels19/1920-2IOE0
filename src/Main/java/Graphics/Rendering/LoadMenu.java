package Graphics.Rendering;

import Levels.Framework.joml.Vector3f;

public class LoadMenu extends SlotPickMenu {
    @Override
    public void render() {
        super.render();
        renderTitle("Pick a save to load");
    }
}
