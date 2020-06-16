package SpellCasting;

import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

import java.util.Timer;
import java.util.TimerTask;

public class SpellIlluminate extends Spell {
    float LIGHT_ATTENUATION = .1f;
    float STANDARD_ATTENUATION = .5f;

    private int prevMana;
    private int manaCost = 10;

    float seconds = 10f;
    int steps = 50;

    public SpellIlluminate() {
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
            illuminate(1, seconds/steps, steps);
        }
    }

    @Override
    public void renderSpell() {

    }

    private void illuminate(float brightness, float duration, int n) {
        Timer timer = new Timer();
        float t = duration;
        if (n >= steps/2) {
             t *= 2;
        } else {
            t /= 2;
        }

        if (n >= 0) {
            float attenuation = STANDARD_ATTENUATION - (STANDARD_ATTENUATION-LIGHT_ATTENUATION) * brightness;
            Player.getInstance().setCurrentAttenuation(new Vector3f(.5f, .2f, attenuation));

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    illuminate(brightness - (1f / steps), duration, n - 1);
                }
            }, (long) (t * 1000));
        }
    }

}
