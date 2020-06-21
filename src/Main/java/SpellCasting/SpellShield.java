package SpellCasting;

import Levels.Characters.Player;

import java.util.Timer;
import java.util.TimerTask;

public class SpellShield extends Spell {
    public SpellShield() {
        super(60);
    }

    private int prevHealth;
    private int prevMana;
    private int manaCost = 15;
    private int shieldValue = 15;

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        Timer timer = new Timer();

        prevMana = player.getMana();
        prevHealth = player.getHealth();

        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);
            player.setShield(true);
            player.setHealth(prevHealth + shieldValue);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    player.setShield(false);
                    int newHealth = player.getHealth();
                    if (newHealth > prevHealth) {
                        player.setHealth(prevHealth);
                    }
                }
            }, 60 * 1000);
        }

    }

    @Override
    public void renderSpell() {

    }
}
