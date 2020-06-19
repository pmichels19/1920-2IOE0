package Levels.Objects;

import Graphics.OBJLoader;
import Graphics.OBJModel;
import Graphics.OpenGL.Texture;

import java.io.IOException;


public class MagicBall extends Object3D {
    // Name of the obj file in the res folder corresponding to the player model
    private static String objModelFile = "ball";

    // Path to the texture of the model
    private static String textureFile = "res/Models/nothing.png";

    // Path to the normal mapping of the model
//    private static String normalMapFile = "res/Models/eyeball_normal.jpg";
    private static String normalMapFile = null;

    private static MagicBall object;

    public MagicBall(OBJModel model) {
        super(model);
        scale = 2.5f;
    }

    public static MagicBall getInstance() {
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

                // Instantiate the object
                object = new MagicBall(model);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

}
