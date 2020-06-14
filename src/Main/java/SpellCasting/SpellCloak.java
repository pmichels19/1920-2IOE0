package SpellCasting;

import Levels.Characters.Player;

public class SpellCloak extends Spell {

    Player player = Player.getInstance();

    private int prevMana;
    private int manaCost = 10;


    @Override
    public void castSpell(Object[] args) {
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);

        }


    }

    @Override
    public void renderSpell() {

    }
}
