package SpellCasting;

import Levels.Characters.Player;

public class SpellCloak extends Spell {

    private int prevMana;
    private int manaCost = 10;


    @Override
    public void castSpell(Object[] args) {
        prevMana = Player.getInstance().getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            Player.getInstance().setMana(prevMana - manaCost);

        }


    }

    @Override
    public void renderSpell() {

    }
}
