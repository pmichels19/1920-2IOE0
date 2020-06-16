package SpellCasting;

import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

public class SpellGuide extends Spell {

    private int prevMana;
    private int manaCost = 10;

    Vector3f loc;

    public SpellGuide() {
        super(5);
    }

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
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
