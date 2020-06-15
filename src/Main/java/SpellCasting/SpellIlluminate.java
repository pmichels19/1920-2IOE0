package SpellCasting;

import Graphics.Rendering.World;
import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

import java.util.Timer;
import java.util.TimerTask;

public class SpellIlluminate extends Spell {

    Timer timer = new Timer();
    Player player = Player.getInstance();
    float LIGHT_ATTENUATION = .1f;
    float STANDARD_ATTENUATION = .5f;

    float seconds = 10f;
    int steps = 50;

    @Override
    public void castSpell(Object[] args) {
        illuminate(1, seconds/steps, steps);
    }

    @Override
    public void renderSpell() {

    }

    private void illuminate(float brightness, float duration, int n) {
        float t = duration;
        if (n >= steps/2) {
             t *= 2;
        } else {
            t /= 2;
        }

        if (n >= 0) {
            float attenuation = STANDARD_ATTENUATION - (STANDARD_ATTENUATION-LIGHT_ATTENUATION) * brightness;
            player.setCurrentAttenuation(new Vector3f(.5f, .2f, attenuation));

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    illuminate(brightness - (1f / steps), duration, n - 1);
                }
            }, (long) (t * 1000));
        }
    }

}
