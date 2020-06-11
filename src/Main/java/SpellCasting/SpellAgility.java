package SpellCasting;

import Levels.Characters.Player;

public class SpellAgility extends Spell {

    Player player = Player.getInstance();

    private int prevMana;
    private int manaCost = 10;

    private long duration;

    @Override
    public void castSpell(Object[] args) {
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana");
        } else {
            player.setMana(prevMana - manaCost);
            duration = System.currentTimeMillis();
            Player.getInstance().setAgilityPower(1);
        }
    }

    @Override
    public void renderSpell() {

    }

    public void checkDuration() {
        if (System.currentTimeMillis() - duration > 5000) {
            Player.getInstance().setAgilityPower(0);
        }
    }
}
