package Graphics.Rendering;

import Levels.Framework.joml.Vector3f;

public class SaveMenu extends SlotPickMenu {
    @Override
    public void render() {
        super.render();
        renderTitle("Pick a slot to save to");
    }
}
