package SpellCasting;

import Levels.Characters.Player;

import java.util.Timer;
import java.util.TimerTask;

public class SpellCloak extends Spell {

    Player player = Player.getInstance();
    Timer timer = new Timer();

    private int prevMana;
    private int manaCost = 10;

    public SpellCloak() {
        super(10);
    }


    @Override
    public void castSpell(Object[] args) {
        int duration = 10;
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);

        }

        player.setInvisibility(1);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                player.setInvisibility(0);
            }
        }, (duration * 1000));

    }

    @Override
    public void renderSpell() {

    }
}
