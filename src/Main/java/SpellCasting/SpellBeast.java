package SpellCasting;

import Graphics.OpenGL.Light;
import Graphics.Rendering.World;
import Levels.Characters.Enemy;
import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;
import Main.Main;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class SpellBeast extends Spell {

    Player player = Player.getInstance();
    Timer timer = new Timer();

    private int prevMana;
    private int manaCost = 10;

    World world = Main.getWorld();
    Light[] lights = world.getLights();

    public SpellBeast() {
        super(10);
    }

    @Override
    public void castSpell(Object[] args) {
        int duration = 2;
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





    }

    @Override
    public void renderSpell() {

    }
}
