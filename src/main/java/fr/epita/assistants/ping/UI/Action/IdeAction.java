package fr.epita.assistants.ping.UI.Action;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.UI.Icons;
import fr.epita.assistants.ping.UI.MainFrame;
import fr.epita.assistants.ping.UI.Panel.NewProject;
import fr.epita.assistants.ping.UI.UITools;
import fr.epita.assistants.ping.service.NodeManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static fr.epita.assistants.ping.UI.UITools.*;

public class IdeAction {

    public static class actNewProject extends ActionTemplate {

        private final MainFrame mainFrame;

        public actNewProject(MainFrame frame) {
            super(
                    "New Project",
                    getResizedIcon(frame, Icons.NEW_PROJECT),
                    KeyEvent.VK_N,
                    "New Project",
                    null);
            this.mainFrame = frame;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            new NewProject(mainFrame);
        }
    }

    private static void abstractNewFile(MainFrame mainFrame, Node.Types type, String name) {
        if (name == null)
            return;
        var selectionPath = mainFrame.getjTree().getSelectionPath();
        Node node;
        if (selectionPath == null)
            node = mainFrame.project.getRootNode();
        else
            node = (Node) selectionPath.getLastPathComponent();
        File file = node.getPath().toFile();
        Path path;
        if (file.isFile())
            path = file.toPath().getParent();
        else
            path = file.toPath();

        mainFrame.getProjectExplorer().setEditing(true);
        ProjectService projectService = mainFrame.getProjectService();
        NodeManager nodeService = (NodeManager)projectService.getNodeService();
        Node root = mainFrame.project.getRootNode();
        if (nodeService.create(nodeService.getFromSource(root, path), name, type) != null){
            mainFrame.getjTree().updateUI();
        }
        else
            UITools.errorDialog(mainFrame, "File already exist.", "New file status");

        mainFrame.getProjectExplorer().setEditing(false);
    }

    private static String getNameDialog(MainFrame frame, String message) {
        String result = (String) JOptionPane.showInputDialog(
                frame,
                message,
                "",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "Name"
        );
        if (result != null) {
            if (result.length() > 0) {
                return result;
            } else {
                errorDialog(frame, "Name is : " + result, "File name error");
            }
        }
        return null;
    }

    public static class actNewFolder extends ActionTemplate {

        private final MainFrame mainFrame;

        public actNewFolder(MainFrame frame) {
            super(
                    "Folder",
                    getResizedIcon(frame, Icons.NEW_FOLDER),
                    KeyEvent.VK_N,
                    "New Folder",
                    null);
            this.mainFrame = frame;
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = getNameDialog(mainFrame, "Enter folder name");
            abstractNewFile(mainFrame, Node.Types.FOLDER, name);
        }
    }

    public static class actNewFile extends ActionTemplate {
        private final MainFrame mainFrame;

        public actNewFile(MainFrame frame) {
            super(
                    "File",
                    getResizedIcon(frame, Icons.NEW_FILE),
                    KeyEvent.VK_N,
                    "Create new file",
                    KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = getNameDialog(mainFrame, "Enter file name");
            abstractNewFile(mainFrame, Node.Types.FILE, name);
        }
    }


    public static class actOpenFile extends ActionTemplate {
        private final MainFrame mainFrame;
        private JTextArea jTextArea;

        public actOpenFile(MainFrame frame, JTextArea jTextArea) {
            super(
                    "Open File",
                    getResizedIcon(frame, Icons.OPEN),
                    KeyEvent.VK_O,
                    "Open file (CTRL+O)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("OpenFile");

            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser(mainFrame.project.getRootNode().getPath().toFile());

            // Invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);

            // If the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {
                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    // String
                    String s1 = "", sl = "";

                    // File reader
                    FileReader fr = new FileReader(fi);

                    // Buffered reader
                    BufferedReader br = new BufferedReader(fr);

                    // Initialize sl
                    sl = br.readLine();

                    // Take the input from the file
                    while ((s1 = br.readLine()) != null) {
                        sl = sl + "\n" + s1;
                    }

                    // Set the text
                    jTextArea.setText(sl);
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(mainFrame, evt.getMessage());
                }
            } else
                JOptionPane.showMessageDialog(mainFrame, "User cancelled operation");
        }
    }

    ;

    public static class actOpenProject extends ActionTemplate {
        private final MainFrame mainFrame;

        public actOpenProject(MainFrame frame) {
            super(
                    "Open Project",
                    getResizedIcon(frame, Icons.OPEN),
                    KeyEvent.VK_O,
                    "Open Project (CTRL+O)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            String home = System.getProperty("user.home");

            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser(Path.of(home).toFile());

            j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            j.setAcceptAllFileFilterUsed(false);

            // Invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);

            // If the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {

                File fi = new File(j.getSelectedFile().getAbsolutePath());
                mainFrame.loadProjectFrame(fi.toPath());
            }
        }
    }

    private static void saveFile(File file, JTextArea jTextArea, MainFrame frame) {
        if (file == null)
            return;

        try {
            // Create a file writer
            FileWriter wr = new FileWriter(file, false);

            // Create buffered writer to write
            BufferedWriter w = new BufferedWriter(wr);

            // Write
            w.write(jTextArea.getText());

            w.flush();
            w.close();
        } catch (Exception evt) {
            JOptionPane.showMessageDialog(frame, evt.getMessage());
        }
    }

    public static class actSave extends ActionTemplate {
        private final MainFrame mainFrame;
        private JTextArea jTextArea;

        public actSave(MainFrame frame) {
            super(
                    "Save File",
                    getResizedIcon(frame, Icons.SAVE),
                    KeyEvent.VK_S,
                    "Save file (CTRL+S)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_S,
                            KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            // Set the label to the path of the selected directory
            if (mainFrame.tabManager.getCurrentFile() != null) {
                File file = mainFrame.tabManager.getCurrentFile().getFile();
                if (file == null)
                    file = fileSelector(mainFrame);
                if (mainFrame.tabManager.getCurrentTextArea() != null)
                    saveFile(file, mainFrame.tabManager.getCurrentTextArea(), mainFrame);
            }
        }


    }

    public static class actSaveAs extends ActionTemplate {
        private final MainFrame mainFrame;
        private JTextArea jTextArea;

        public actSaveAs(MainFrame frame, JTextArea jTextArea) {
            super(
                    "Save As...",
                    getResizedIcon(frame, Icons.SAVE_AS),
                    KeyEvent.VK_A,
                    "Save file as",
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK)
                    );
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            File file = fileSelector(mainFrame);
            saveFile(file, mainFrame.tabManager.getCurrentTextArea(), mainFrame);
        }
    }

    public static class actCopy extends ActionTemplate {
        private final MainFrame mainFrame;
        private JTextArea jTextArea;

        public actCopy(MainFrame frame, JTextArea jTextArea) {
            super(
                    "Copy",
                    getResizedIcon(frame, Icons.COPY),
                    KeyEvent.VK_C,
                    "Copy (CTRL+C)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (jTextArea != null)
                jTextArea.copy();
        }
    }

    public static class actCut extends ActionTemplate {
        private final MainFrame mainFrame;
        private JTextArea jTextArea;

        public actCut(MainFrame frame, JTextArea jTextArea) {
            super(
                    "Cut",
                    getResizedIcon(frame, Icons.CUT),
                    KeyEvent.VK_T,
                    "Cut (CTRL+X)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (jTextArea != null)
                jTextArea.cut();
        }
    }

    public static class actPaste extends ActionTemplate {
        private final MainFrame mainFrame;
        private JTextArea jTextArea;

        public actPaste(MainFrame frame, JTextArea jTextArea) {
            super(
                    "Paste",
                    getResizedIcon(frame, Icons.PASTE),
                    KeyEvent.VK_P,
                    "Paste (CTRL+V)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (jTextArea != null)
                jTextArea.paste();
        }
    }

    public static class actUndo extends ActionTemplate {
        private final MainFrame mainFrame;


        public actUndo(MainFrame frame ) {
            super(
                    "Undo",
                    getResizedIcon(frame, Icons.UNDO),
                    KeyEvent.VK_U,
                    "Undo (CTRL+Z)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (mainFrame.getUndoManager() != null && mainFrame.getUndoManager().canUndo())
                mainFrame.getUndoManager().undo();
        }
    }

    public static class actRedo extends ActionTemplate {
        private final MainFrame mainFrame;

        public actRedo(MainFrame frame) {
            super(
                    "Redo",
                    getResizedIcon(frame, Icons.REDO),
                    KeyEvent.VK_R,
                    "Redo (CTRL+Y)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (mainFrame.getUndoManager() != null && mainFrame.getUndoManager().canRedo())
                mainFrame.getUndoManager().redo();
        }
    }

    public static class actExit extends ActionTemplate {
        private final MainFrame mainFrame;

        public actExit(MainFrame frame) {
            super(
                    "Exit",
                    getResizedIcon(frame, Icons.EXIT),
                    KeyEvent.VK_X,
                    "Exit (ALT+F4)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
        }
    }
}
