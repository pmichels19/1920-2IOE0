package SpellCasting;

import AI.AStar.AStarSolver;
import Graphics.OpenGL.Light;
import Levels.Characters.Player;
import Levels.Framework.Point;
import Levels.Framework.joml.Vector3f;
import Levels.Objects.MagicBall;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SpellGuide extends Spell {

    Timer timer = new Timer();

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
            player.setGuide(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    player.setGuide(false);
                }
            }, 5 * 1000);
        }
    }

    @Override
    public void renderSpell() {

    }


}
