package Levels.Characters;

import Graphics.OBJModel;
import Graphics.OpenGL.Shader;
import Levels.Framework.joml.Matrix4f;
import Levels.Framework.joml.Vector3f;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class Character {
    // Holds the model
    private OBJModel model;

    // Holds the current position
    private Vector3f position;

    // Holds the scale
    private float scale;

    // holds the rotation
    private Vector3f rotation;
    private float rotationAngle;

    private float gamePositionX;
    private float gamePositionY;

    // the values for max health and mana
    int max_health;
    int max_mana;

    // the values for current health and mana
    int cur_health;
    int cur_mana;

    //Time related objects to link animation to
    private double tVal = 0.0;
    Timer timer;

    public Character(int max_health, int max_mana, OBJModel model) {
        this.model = model;
        position = new Vector3f(0, 0f, 1.5f);
        scale = 1.2f;
        rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
        rotation = new Vector3f(0f, 0f, 1f);

        gamePositionX = 1;
        gamePositionY = 1;

        // start the player off fresh, with full mana and health
        this.max_health = max_health;
        this.max_mana = max_mana;
        cur_health = max_health;
        cur_mana = max_mana;

        //Setting up the animation timer
        timer = new Timer( 10, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tVal += 0.005;
            } //increasing tVal
        }
        );

        timer.start();
    }


//    abstract void generateModel(float x_pos, float y_pos);

    /**
     * renders the model specified in the subclass once
     */
    public void render(Shader shader, float x_pos, float y_pos) {
//        generateModel(x_pos, y_pos);



        model.render(shader);
    }

    public void render(Shader shader) {
        shader.setUniform("transform", 1);
        Matrix4f modelTransform = new Matrix4f();

        //floating animation linked to Z-axis
        position.z = (float)Math.cos(Math.toRadians(tVal * 106f));

        modelTransform.translate(position).rotate(rotationAngle, rotation).scale(scale);
        shader.setUniform("modelTransform", modelTransform);
        model.render(shader);
        shader.setUniform("transform", 0);
    }

    public OBJModel getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setRotation(float angle, Vector3f vector) {
        this.rotationAngle = angle;
        this.rotation = vector;
    }

    public int getHealth() {
        return cur_health;
    }

    public int getMana() {
        return cur_mana;
    }

    public void setHealth(int health) {
        cur_health = health;
    }

    public void setMana(int mana) {
        cur_mana = mana;
    }

    public float getGamePositionX() {
        return gamePositionX;
    }

    public float getGamePositionY() {
        return gamePositionY;
    }

    public void setGamePositionX(float gamePositionX) {
        this.gamePositionX = gamePositionX;

        this.position.x = gamePositionX * 2f;
    }

    public void setGridPositionY(float gamePositionY, float gridLength) {
        this.gamePositionY = gamePositionY;
        this.position.y = (gridLength - 0.5f - gamePositionY) * 2f;
    }

    public void setGridPosition(float gamePositionX, float gamePositionY, float gridLength) {
        if (gamePositionX > this.gamePositionX) {
            this.rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
            this.rotation = new Vector3f(0f, 0f, 1f);
        } else if (gamePositionX < this.gamePositionX) {
            this.rotationAngle = (float) ((90f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
        }
        if (gamePositionY > this.gamePositionY) {
            this.rotationAngle = (float) ((-180f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
        } else if (gamePositionY < this.gamePositionY) {
            this.rotationAngle = (float) ((0f * Math.PI) / 180.0f);

            this.rotation = new Vector3f(0f, 0f, 1f);
        }
        setGamePositionX(gamePositionX);
        setGridPositionY(gamePositionY, gridLength);
    }
}
