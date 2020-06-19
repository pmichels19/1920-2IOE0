package SpellCasting;

import Graphics.Rendering.World;
import Levels.Characters.Enemy;
import Levels.Characters.Player;
import Levels.Framework.Maze;
import Main.Main;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SpellScan extends Spell {

    Player player = Player.getInstance();
    Timer timer = new Timer();

    private int prevMana;
    private int manaCost = 10;

    public SpellScan() {
        super(10);
    }

    World world = Main.getWorld();
    List<Enemy> enemies = world.getEnemyList();

    @Override
    public void castSpell(Object[] args) {
        int duration = 5;
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);

        }

        for (Enemy enemy : enemies) {
            enemy.setHighAmbience(true);
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Enemy enemy : enemies) {
                    enemy.setHighAmbience(false);
                }
            }
        }, (duration * 1000));

    }

    @Override
    public void renderSpell() {

    }
}
