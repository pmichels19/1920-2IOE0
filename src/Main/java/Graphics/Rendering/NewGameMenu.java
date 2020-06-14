package Graphics.Rendering;

import Levels.Framework.joml.Vector3f;

public class NewGameMenu extends SlotPickMenu {
    public NewGameMenu() {
        super();
    }

    @Override
    public void render() {
        super.render();
        renderTitle("Pick a slot to start");
    }
}
