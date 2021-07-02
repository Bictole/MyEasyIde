package fr.epita.assistants.ping.UI;

import javax.imageio.ImageIO;
import javax.imageio.ImageTranscoder;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import fr.epita.assistants.myide.domain.entity.Node;

import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


//import static sun.awt.image.BufferedImageGraphicsConfig.getConfig;

public class UITools {

    public static void errorDialog(MainFrame frame, String message, String title) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /*public static Icon getResizedIcon(MainFrame frame, Icons icon) {
        return MainFrame.resizeIcon(new ImageIcon(icon.path), frame.iconWidth, frame.iconHeight);
    }*/

    public static Icon getResizedIcon(MainFrame frame, Icons icon) {
        return new ImageIcon(ImageResize.ImageTest(icon, 24));
    }

    public static File fileSelector(MainFrame frame) {
        // Create an object of JFileChooser class
        JFileChooser j = new JFileChooser("f:");

        // Invoke the showsSaveDialog function to show the save dialog
        int r = j.showSaveDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            // Set the label to the path of the selected directory
            return j.getSelectedFile();
        } else {
            JOptionPane.showMessageDialog(frame, "the user cancelled the operation");
            return null;
        }
    }

    public static Node getSelectedNode(MainFrame mainFrame){
        var selectionPath = mainFrame.getjTree().getSelectionPath();
        Node node;
        if (selectionPath == null)
            node = mainFrame.project.getRootNode();
        else
            node = (Node) selectionPath.getLastPathComponent();
        return node;
    }

    /*
        Extend you action from this template, calling super() with right arguments
        in the constructor
        Using putValue() in inherited constructor after calling super() overrides
        null values can be passed if no arguments applies
     */
    public static abstract class ActionTemplate extends AbstractAction {

        public ActionTemplate(String name, Icon icon, int mnemonic, String description, KeyStroke keyStroke) {
            putValue(Action.NAME, name);
            putValue(Action.LARGE_ICON_KEY, icon);
            putValue(Action.MNEMONIC_KEY, mnemonic);
            putValue(Action.SHORT_DESCRIPTION, description);
            putValue(Action.ACCELERATOR_KEY, keyStroke);
        }
    }

    public static class ImageResize {


        public static Image ImageTest(Icons icon, int size) {
            try {
                BufferedImage image = ImageIO.read(new File(icon.path));
                return resizeImage(image, size, size);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static BufferedImage resize(BufferedImage image, int width, int height) {
            int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
            BufferedImage resizedImage = new BufferedImage(width, height, type);
            Graphics2D g = resizedImage.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();
            return resizedImage;
        }

        private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
            image = createCompatibleImage(image);
            image = resize(image, 100, 100);
            image = blurImage(image);
            return resize(image, width, height);
        }

        public static BufferedImage blurImage(BufferedImage image) {
            float ninth = 1.0f/9.0f;
            float[] blurKernel = {
                    ninth, ninth, ninth,
                    ninth, ninth, ninth,
                    ninth, ninth, ninth
            };

            Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
            map.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            map.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            map.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            RenderingHints hints = new RenderingHints(map);
            BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
            return op.filter(image, null);
        }

        private static BufferedImage createCompatibleImage(BufferedImage image) {
            GraphicsConfiguration gc = image.createGraphics().getDeviceConfiguration();
            int w = image.getWidth();
            int h = image.getHeight();
            BufferedImage result = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
            Graphics2D g2 = result.createGraphics();
            g2.drawRenderedImage(image, null);
            g2.dispose();
            return result;
        }

    }

}
