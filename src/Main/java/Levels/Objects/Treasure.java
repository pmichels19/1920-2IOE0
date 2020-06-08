package Levels.Objects;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Texture;
import Levels.Characters.Player;
import Levels.Framework.joml.Vector3f;

import java.io.IOException;


public class Treasure extends Object3D {
    // Name of the obj file in the res folder corresponding to the player model
    private static String objModelFile = "ball";

    // Path to the texture of the model
    private static String textureFile = "res/Models/nothing.png";

    // Path to the normal mapping of the model
//    private static String normalMapFile = "res/Models/eyeball_normal.jpg";
    private static String normalMapFile = null;

    private static Treasure object;

    public Treasure(OBJModel model) {
        super(model);
        position = new Vector3f(0, 0f, 0f);
        scale = 2f;
        rotationAngle = (float) ((-90f * Math.PI) / 180.0f);
        rotation = new Vector3f(0f, 0f, 1f);
    }

    public static Treasure getInstance() {
        if (object == null) {
            try {
                // Create the model
                OBJModel model =  OBJLoader.loadObjModel(objModelFile);

                if (textureFile != null) {
                    // Set the texture
                    model.setTexture(new Texture(textureFile));
                }

                // Add a normal map if there is one
                if (normalMapFile != null) {
                    model.setNormalMap(new Texture(normalMapFile));
                }

                // Instantiate the player
                object = new Treasure(model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

}
