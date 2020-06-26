package SpellCasting;

import Levels.Characters.Player;

public class SpellFireball extends Spell {

    private int prevMana;
    private int manaCost = 10;

    public SpellFireball() {
        super(0);
    }

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);
            player.setFireball(true);
        }
    }

    @Override
    public void renderSpell() {

    }
}
