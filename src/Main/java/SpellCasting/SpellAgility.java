package SpellCasting;

import Levels.Characters.Player;

public class SpellAgility extends Spell {

    private long duration;

    @Override
    public void castSpell(Object[] args) {
        duration = System.currentTimeMillis();
        Player.getInstance().setAgilityPower(1);
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
