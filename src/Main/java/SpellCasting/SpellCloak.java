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

    public SpellCloak() {
        super(10);
    }

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();

        World world = Main.getWorld();
        List<Enemy> enemies = world.getEnemyList();

        Timer timer = new Timer();

        int duration = 10;
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);
        }

        player.setInvisibility(1);
        int detectionDistance = 4;
        for (Enemy enemy : enemies) {
            detectionDistance = enemy.getDetectionDistance();
            enemy.setDetectionDistance(0);
        }

        int finalDetectionDistance = detectionDistance;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                player.setInvisibility(0);
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
