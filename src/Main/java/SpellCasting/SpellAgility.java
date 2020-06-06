package SpellCasting;

import Levels.Characters.Player;

import java.util.concurrent.TimeUnit;

public class SpellAgility extends Spell {

    private int castTime = 0;

    @Override
    public void castSpell(Object[] args) {
        System.out.println("cast");
        // 5 seconds
        castTime = 150;
        Player.getInstance().setAgilityPower(1);
    }

    @Override
    public void renderSpell() {

    }

    public void countDown() {
        if (castTime > 0) {
            castTime--;
        } else {
            Player.getInstance().setAgilityPower(0);
        }
    }
}
