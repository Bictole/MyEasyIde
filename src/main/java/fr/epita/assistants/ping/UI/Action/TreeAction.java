package fr.epita.assistants.ping.UI.Action;

import fr.epita.assistants.ping.UI.Icons;
import fr.epita.assistants.ping.UI.MainFrame;
import fr.epita.assistants.ping.UI.Panel.NewProject;
import fr.epita.assistants.ping.UI.UITools;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static fr.epita.assistants.ping.UI.UITools.getResizedIcon;

public class TreeAction {

    public static class actCopy extends UITools.ActionTemplate {

        private final MainFrame mainFrame;

        public actCopy(MainFrame frame) {
            super(
                    "Copy File",
                    getResizedIcon(frame, Icons.COPY),
                    KeyEvent.VK_N,
                    "Copy File or Folder",
                    null);
            this.mainFrame = frame;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO
        }
    }

    public static class actCut extends UITools.ActionTemplate {

        private final MainFrame mainFrame;

        public actCut(MainFrame frame) {
            super(
                    "Cut",
                    getResizedIcon(frame, Icons.CUT),
                    KeyEvent.VK_N,
                    "Cut File or Folder",
                    null);
            this.mainFrame = frame;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO
        }
    }

    public static class actPaste extends UITools.ActionTemplate {

        private final MainFrame mainFrame;

        public actPaste(MainFrame frame) {
            super(
                    "Paste",
                    getResizedIcon(frame, Icons.PASTE),
                    KeyEvent.VK_N,
                    "Paste File or Folder",
                    null);
            this.mainFrame = frame;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO
        }
    }

    public static class actDelete extends UITools.ActionTemplate {

        private final MainFrame mainFrame;

        public actDelete(MainFrame frame) {
            super(
                    "Delete",
                    null,
                    KeyEvent.VK_N,
                    "Delete File or Folder",
                    null);
            this.mainFrame = frame;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO
        }
    }

}
