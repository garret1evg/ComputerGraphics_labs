import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.image.TextureLoader;
import javax.vecmath.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;
import javax.media.j3d.*;
import javax.swing.*;

public class Gull extends JFrame {

    private TransformGroup air = new TransformGroup();
    private Canvas3D canvas;
    private SimpleUniverse universe;
    private BranchGroup root;
    private TransformGroup gull;
    private Map<String, Shape3D> shapeMap;

    public Gull() throws IOException {

        configureWindow();
        configureCanvas();
        configureUniverse();

        root = new BranchGroup();
        root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        addImageBackground("resource/image/0017621_wade-sea-sea-gulls-beach-birds-in-the-sky-seashore-cotton-fabric_500.jpeg");
        createLight();

        ModifyViewAngle();

        gull = getGullGroup();
        air.addChild(gull);

        root.addChild(air);

        addAppearance();

        AnimateGull animateGull = new AnimateGull(this);
        canvas.addKeyListener(animateGull);

        root.compile();
        universe.addBranchGraph(root);
    }

    private void addImageBackground(String imagePath) {
        TextureLoader t = new TextureLoader(imagePath, canvas);
        Background background = new Background(t.getImage());
        background.setImageScaleMode(Background.SCALE_FIT_ALL);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        background.setApplicationBounds(bounds);
        root.addChild(background);
    }

    private void configureWindow() {
        setTitle("GULL");
        setSize(1920, 1050);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void configureCanvas() {
        canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        canvas.setDoubleBufferEnable(true);
        canvas.setFocusable(true);
        add(canvas, BorderLayout.CENTER);
    }

    private void configureUniverse() {
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
    }

    private void createLight() {
        BoundingSphere bounds = new BoundingSphere();
        bounds.setRadius(1000);

        DirectionalLight directionalLight = new DirectionalLight(
                new Color3f(new Color(255, 255, 255)),
                new Vector3f(0, -0.5f, -0.5f));
        directionalLight.setInfluencingBounds(bounds);

        AmbientLight ambientLight = new AmbientLight(
                new Color3f(new Color(255, 255, 245)));
        ambientLight.setInfluencingBounds(bounds);

        root.addChild(directionalLight);
        root.addChild(ambientLight);
    }

    private TransformGroup getGullGroup() throws IOException {
        Transform3D transform3D = new Transform3D();

        transform3D.setTranslation(new Vector3d(1, 0, 0));

        TransformGroup group = getModelGroup("resource/GULL.OBJ");
        group.setTransform(transform3D);

        return group;
    }

    private TransformGroup getModelGroup(String path) throws IOException {
        Scene scene = loadSceneFromFile(path);
        shapeMap = scene.getNamedObjects();
        TransformGroup group = new TransformGroup();

        for (String shapeName : shapeMap.keySet()) {
            Shape3D shape = shapeMap.get(shapeName);

            scene.getSceneGroup().removeChild(shape);
            group.addChild(shape);
        }

        group.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        return group;
    }


    private void addAppearance() {

        Appearance gullEye = new Appearance();
        gullEye.setMaterial(getMaterial(
                Color.BLACK,
                new Color(0x001836)));
        shapeMap.get("gull_eye").setAppearance(gullEye);

        Appearance body = new Appearance();
        body.setMaterial(getMaterial(
                Color.BLACK,
                new Color(0x939393)));
        shapeMap.get("gull_body").setAppearance(body);
    }

    public static void main(String[] args) {
        try {
            Gull window = new Gull();
            window.setVisible(true);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    Material getMaterial(
            Color emissiveColor,
            Color defaultColor) {
        Material material = new Material();
        material.setEmissiveColor(new Color3f(emissiveColor));
        material.setAmbientColor(new Color3f(defaultColor));
        material.setDiffuseColor(new Color3f(defaultColor));
        material.setSpecularColor(new Color3f(defaultColor));
        material.setShininess(64);
        material.setLightingEnable(true);
        return material;
    }

    private void ModifyViewAngle() {
        ViewingPlatform vp = universe.getViewingPlatform();
        TransformGroup vpGroup = vp.getMultiTransformGroup().getTransformGroup(0);
        Transform3D vpTranslation = new Transform3D();
        vpTranslation.setTranslation(new Vector3f(0, 0, 6));
        vpGroup.setTransform(vpTranslation);

    }

    public TransformGroup getGullTransformGroup() {
        return gull;
    }

    public static Scene loadSceneFromFile(String location) throws IOException {
        ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
        file.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
        return file.load(new FileReader(location));
    }

}
