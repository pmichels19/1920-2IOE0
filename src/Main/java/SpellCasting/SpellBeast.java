package SpellCasting;

import Graphics.OpenGL.Light;
import Graphics.Rendering.World;
import Levels.Characters.Enemy;
import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;
import Main.Main;

import java.util.*;

public class SpellBeast extends Spell {

    private int prevMana;
    private int manaCost = 10;

    public SpellBeast() {
        super(10);
    }

    @Override
    public void castSpell(Object[] args) {
        Player player = Player.getInstance();
        World world = Main.getWorld();
        Light[] lights = world.getLights();

        Timer timer = new Timer();

        int duration = 5;
        prevMana = player.getMana();
        if (prevMana < manaCost) {
            System.out.println("Not enough mana!");
        } else {
            player.setMana(prevMana - manaCost);

        }

        Light[] newLights = new Light[lights.length];

        for (int i = 0; i < newLights.length; i++) {
            Light newLight = new Light(lights[i].getPosition(), new Vector3f(1f, .2f, .2f), lights[i].getObject(), lights[i].getAttenuation());
            newLights[i] = newLight;
        }

        world.setLights(newLights);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                world.setLights(lights);
            }
        }, (duration * 1000));

        List<Enemy> enemies = world.getEnemyList();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                world.setLights(lights);
            }
        }, (duration * 1000));

        for (int i = 0; i < duration; i++) {
            float healthDecrease = .5f/duration;
            for (Enemy enemy : enemies) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        int curHealth = enemy.getHealth();
                        enemy.setHealth((int) (curHealth - curHealth*healthDecrease));
                    }
                }, (i * 1000));
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int curHealth = player.getHealth();
                    player.setHealth((int) (curHealth - curHealth*healthDecrease));
                }
            }, (i * 1000));
        }





    }

    @Override
    public void renderSpell() {

    }
}
