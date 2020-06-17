package SpellCasting;

import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

public class SpellGuide extends Spell {

    private int prevMana;
    private int manaCost = 10;

    Vector3f loc;

    @Override
    public void castSpell(Object[] args) {
        prevMana = Player.getInstance().getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            Player.getInstance().setMana(prevMana - manaCost);
            loc = Player.getInstance().getPosition();

        }
    }

    @Override
    public void renderSpell() {

    }
}
