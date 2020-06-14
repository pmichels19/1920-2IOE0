package SpellCasting;

import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

public class SpellGuide extends Spell {

    Player player = Player.getInstance();

    private int prevMana;
    private int manaCost = 10;

    Vector3f loc;

    @Override
    public void castSpell(Object[] args) {
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);
            loc = player.getPosition();

        }
    }

    @Override
    public void renderSpell() {

    }
}
