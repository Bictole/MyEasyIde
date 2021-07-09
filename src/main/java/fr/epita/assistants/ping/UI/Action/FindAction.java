package fr.epita.assistants.ping.UI.Action;

import ch.qos.logback.core.joran.action.Action;
import fr.epita.assistants.ping.UI.MainFrame;
import fr.epita.assistants.ping.UI.UITools;
import org.fife.rsta.ui.GoToDialog;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.nio.charset.MalformedInputException;

public class FindAction {

    /**
     * Shows the Find dialog.
     */
    public static class ShowFindDialogAction extends UITools.ActionTemplate {

        MainFrame frame;

        public ShowFindDialogAction(MainFrame frame) {
            super(
                    "Find",
                    null,
                    KeyEvent.VK_F,
                    "Find specified words",
                    KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));

            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (frame.replaceDialog.isVisible()) {
                frame.replaceDialog.setVisible(false);
            }
            frame.findDialog.setVisible(true);
        }

    }


    /**
     * Shows the Replace dialog.
     */
    public static class ShowReplaceDialogAction extends UITools.ActionTemplate {

        MainFrame frame;

        public ShowReplaceDialogAction(MainFrame frame) {
            super(
                    "Replace",
                    null,
                    KeyEvent.VK_H,
                    "Replace specified words",
                    KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));

            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (frame.findDialog.isVisible()) {
                frame.findDialog.setVisible(false);
            }
            frame.replaceDialog.setVisible(true);
        }
    }

    /**
     * Opens the "Go to Line" dialog.
     */
    public static class GoToLineAction extends UITools.ActionTemplate {

        MainFrame frame;

        public GoToLineAction(MainFrame frame) {
            super(
                    "Go To Line",
                    null,
                    KeyEvent.VK_L,
                    "Replace specified words",
                    KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (frame.findDialog.isVisible()) {
                frame.findDialog.setVisible(false);
            }
            if (frame.replaceDialog.isVisible()) {
                frame.replaceDialog.setVisible(false);
            }

            RSyntaxTextArea textArea = frame.getrSyntaxTextArea();

            GoToDialog dialog = new GoToDialog(frame);
            dialog.setMaxLineNumberAllowed(textArea.getLineCount());
            dialog.setVisible(true);
            int line = dialog.getLineNumber();
            if (line > 0) {
                try {
                    textArea.setCaretPosition(textArea.getLineStartOffset(line - 1));
                } catch (BadLocationException ble) { // Never happens
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                    ble.printStackTrace();
                }
            }
        }

    }
}
