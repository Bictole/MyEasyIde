package fr.epita.assistants.ping.UI;

import javax.swing.*;

public class UITools {

    public static void errorDialog(MainFrame frame, String message) {
        JOptionPane.showMessageDialog(frame, message, "", JOptionPane.ERROR_MESSAGE);
    }

    public static Icon getResizedIcon(MainFrame frame, Icons icon) {
        return MainFrame.resizeIcon(new ImageIcon(icon.path), frame.iconWidth, frame.iconHeight);
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
            putValue(Action.SMALL_ICON, icon);
            putValue(Action.MNEMONIC_KEY, mnemonic);
            putValue(Action.SHORT_DESCRIPTION, description);
            putValue(Action.ACCELERATOR_KEY, keyStroke);
        }
    }

}
