package SpellCasting;

import Graphics.IO.Timer;
import Levels.Characters.Player;

public class SpellAgility extends Spell {

    private int prevMana;
    private int manaCost = 10;

    public SpellAgility() {
        super(5);
    }

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana");
        } else {
            player.setMana(prevMana - manaCost);
            castMoment = Timer.getTime();
            Player.getInstance().setAgilityPower(1);
        }
    }

    @Override
    public void renderSpell() {

    }
}
