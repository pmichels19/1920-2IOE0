package Graphics.ColladaParser.ColladaLoader;

import Graphics.ColladaParser.DataStructures.*;
import Graphics.ColladaParser.Utils.*;
import Graphics.ColladaParser.XMLParser.*;

public class ColladaLoader {

    public static AnimatedModelData loadColladaModel(XMLFile colladaFile, int maxWeights) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);

        SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
        SkinningData skinningData = skinLoader.extractSkinData();

        SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
        SkeletonData jointsData = jointsLoader.extractBoneData();

        GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
        MeshData meshData = g.extractModelData();

        return new AnimatedModelData(meshData, jointsData);
    }

    //FIXME: Deprecated
    /*
    public static AnimationData loadColladaAnimation(MyFile colladaFile) {
        XmlNode node = XmlParser.loadXmlFile(colladaFile);
        XmlNode animNode = node.getChild("library_animations");
        XmlNode jointsNode = node.getChild("library_visual_scenes");
        AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
        AnimationData animData = loader.extractAnimation();
        return animData;
    } */

}