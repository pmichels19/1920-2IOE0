package SpellCasting;

import Graphics.Rendering.World;
import Levels.Characters.Enemy;
import Levels.Characters.Player;
import Levels.Framework.Maze;
import Main.Main;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SpellCloak extends Spell {
    private int prevMana;
    private int manaCost = 10;
  
    Timer timer = new Timer();

    public SpellCloak() {
        super(10);
    }

    World world = Main.getWorld();
    List<Enemy> enemies = world.getEnemyList();

    @Override
    public void castSpell(Object[] args) {
        prevMana = Player.getInstance().getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            Player.getInstance().setMana(prevMana - manaCost);

        }

        Player.getInstance().setInvisibility(1);
        int detectionDistance = 4;
        for (Enemy enemy : enemies) {
            detectionDistance = enemy.getDetectionDistance();
            enemy.setDetectionDistance(0);
        }

        int finalDetectionDistance = detectionDistance;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Player.getInstance().setInvisibility(0);
                for (Enemy enemy : enemies) {
                    enemy.setDetectionDistance(finalDetectionDistance);
                }
            }
        }, (duration * 1000));

    }

    @Override
    public void renderSpell() {

    }
}
