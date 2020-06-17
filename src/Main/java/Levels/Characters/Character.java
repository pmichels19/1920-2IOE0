package Levels.Characters;

import Graphics.OBJModel;
import Graphics.OpenGL.Shader;
import Levels.Framework.Point;
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

    // Holds maze position
    private Point mazePosition;

    // Holds the scale
    private float scale;

    // holds the rotation
    private Vector3f rotation;
    private float rotationAngle;
    protected int direction;

    private float gamePositionX;
    private float gamePositionY;

    // the values for max health and mana
    int max_health;
    int max_mana;

    // the values for current health and mana
    int cur_health;
    int cur_mana;

    // the amount of damage dealt by normal attack
    int damage = 10;

    // Speed of the character
    private float speed;

    //Time related objects to link animation to
    private double tVal = 0.0;
    Timer timer;

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_DOWN = 3;

    public Character(int max_health, int max_mana, int speed, OBJModel model) {
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

        this.speed = speed;

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
        float startingPoint = 1.5f;
        float floatingSpeed = 150f;
        position.z = (float) (startingPoint + Math.pow(Math.cos(Math.toRadians(tVal * floatingSpeed)),2)/2);

        modelTransform.translate(position).rotate(rotationAngle, rotation).scale(scale);
        shader.setUniform("modelTransform", modelTransform);
        model.render(shader);
        shader.setUniform("transform", 0);
    }

    public OBJModel getModel() {
        return model;
    }

    public void setModel(OBJModel model) {
        this.model = model;
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
        if (cur_health > max_health) {
            cur_health = max_health;
        }
    }

    public void setMana(int mana) {
        cur_mana = mana;
        if (cur_mana > max_mana) {
            cur_mana = max_mana;
        }
    }

    public float getGamePositionX() {
        return gamePositionX;
    }

    public float getGamePositionY() {
        return gamePositionY;
    }

//    public int getSpeed() {
//        return speed;
//    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setGamePositionX(float gamePositionX) {
        this.gamePositionX = gamePositionX;

        this.position.x = gamePositionX * 2f;
    }

    public void setGamePositionY(float gamePositionY, float gridLength) {
        this.gamePositionY = gamePositionY;
        this.position.y = (gridLength - 0.5f - gamePositionY) * 2f;
    }

    /**
     * turns the character so it faces to the right
     */
    public void turnRight() {
        this.rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
        this.rotation = new Vector3f(0f, 0f, 1f);
        this.direction = DIRECTION_RIGHT;
    }

    /**
     * turns the character so it faces to the left
     */
    public void turnLeft() {
        this.rotationAngle = (float) ((90f * Math.PI) / 180.0f);
        this.rotation = new Vector3f(0f, 0f, 1f);
        this.direction = DIRECTION_LEFT;
    }

    /**
     * turns the character so it faces downwards
     */
    public void turnDown() {
        this.rotationAngle = (float) ((-180f * Math.PI) / 180.0f);
        this.rotation = new Vector3f(0f, 0f, 1f);
        this.direction = DIRECTION_DOWN;
    }

    /**
     * turns the character so it faces upwards
     */
    public void turnUp() {
        this.rotationAngle = (float) ((0f * Math.PI) / 180.0f);
        this.rotation = new Vector3f(0f, 0f, 1f);
        this.direction = DIRECTION_UP;
    }

    public void setGamePositionAndRotate(float gamePositionX, float gamePositionY, float gridLength) {
        if (gamePositionX > this.gamePositionX) {
            turnRight();
        } else if (gamePositionX < this.gamePositionX) {
            turnLeft();
        }
        if (gamePositionY > this.gamePositionY) {
            turnDown();
        } else if (gamePositionY < this.gamePositionY) {
            turnUp();
        }
        setGamePositionX(gamePositionX);
        setGamePositionY(gamePositionY, gridLength);
    }

    public Point getMazePosition() {
        return mazePosition;
    }

    public void setMazePosition(Point mazePosition) {
        this.mazePosition = mazePosition;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Initializes the starting position of the enemy
     *
     * @param x          x location
     * @param y          y location
     * @param gridLength length of the grid
     */
    public void initializePosition(int x, int y, int gridLength) {
        setMazePosition(new Point(x, y));
        setGamePositionAndRotate((float) y, (float) x, gridLength);
    }

    /**
     * Damages a character by given amount
     * @param damage amount of damage to be dealt to this player
     */
    public void damage(int damage){
        setHealth(cur_health - damage);
    }
}
