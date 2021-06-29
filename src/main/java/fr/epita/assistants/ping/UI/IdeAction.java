package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.service.ProjectService;
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
            // putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame f = new JFrame("New Project");
            JLabel nameLabel = new JLabel("Name:");

            JTextField nameTextField = new JTextField("undefined");

            JLabel locationLabel = new JLabel("Location:");
            JTextField locationTextField = new JTextField();
            JButton locationButton = new JButton(MainFrame.resizeIcon(new ImageIcon(Icons.OPEN_PROJECT.path), 16, 16));
            locationButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Create an object of JFileChooser class
                    JFileChooser j = new JFileChooser(mainFrame.project.getRootNode().getPath().toFile());
                    j.setDialogTitle("Choose project location");
                    j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    j.setAcceptAllFileFilterUsed(false);
                    // Invoke the showsOpenDialog function to show the save dialog
                    int r = j.showOpenDialog(null);

                    // If the user selects a file
                    if (r == JFileChooser.APPROVE_OPTION) {

                        File fi = new File(j.getSelectedFile().getAbsolutePath());
                        locationTextField.setText(fi.getAbsolutePath());
                    }
                }
            });

            JButton btnOK = new JButton("OK");
            btnOK.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (nameTextField.getText().isEmpty())
                    {
                        JOptionPane.showMessageDialog(f, "Enter a project name", "Error name", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    else if (locationTextField.getText().isEmpty())
                    {
                        JOptionPane.showMessageDialog(f, "Enter a project location", "Error location", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        File tmp = Path.of(locationTextField.getText()).resolve(nameTextField.getText()).toFile();
                        if (tmp.exists())
                        {
                            if (JOptionPane.showConfirmDialog(f,
                                "Project already exists at this location.\nDo you want to open it ?",
                                "Already exist project", JOptionPane.YES_NO_OPTION)  == JOptionPane.YES_OPTION)
                            {
                                mainFrame.loadProjectFrame(tmp.toPath());
                                f.dispose();
                            }
                            return;
                        }
                        if (JOptionPane.showConfirmDialog(f,
                                "No project exists at this location.\nDo you want to create it ?",
                                "Confirm project creation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                        {
                            Files.createDirectories(tmp.toPath());
                            mainFrame.loadProjectFrame(tmp.toPath());
                            f.dispose();
                        }
                        return;

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            });
            JButton btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    f.dispose();
                    return;
                }
            });

            GroupLayout layout = new GroupLayout(f.getContentPane());

            f.getContentPane().setLayout(layout);

            layout.setAutoCreateGaps(true);

            layout.setAutoCreateContainerGaps(true);

            layout.setHorizontalGroup(layout.createSequentialGroup()

                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(nameLabel)
                            .addComponent(locationLabel))

                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField)
                            .addComponent(locationTextField)
                            .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addComponent(btnOK))
                                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                            .addComponent(btnCancel))))

                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(locationButton))
            );

            //lier la taille des composants quel que soit leur emplacement
            layout.linkSize(SwingConstants.HORIZONTAL, btnOK, btnCancel);

            layout.setVerticalGroup(layout.createSequentialGroup()

                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(nameLabel)
                            .addComponent(nameTextField))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(locationLabel)
                            .addComponent(locationTextField)
                            .addComponent(locationButton))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(btnOK)
                            .addComponent(btnCancel))
            );

            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            f.setSize(dim.width / 4, dim.height / 8);
            f.pack();
            f.setLocation(dim.width / 2 - f.getSize().width / 2, dim.height / 2 - f.getSize().height / 2);
            f.setVisible(true);
            f.setDefaultCloseOperation(f.HIDE_ON_CLOSE);
        }
    }

    private static void abstractNewFile(MainFrame mainFrame, Node.Types type, String name) {
        if (name == null)
            return;
        File file = mainFrame.getSelectedFile();
        Path path;
        if (file == null)
            path = mainFrame.project.getRootNode().getPath();
        else if (file.isFile())
            path = file.toPath().getParent();
        else
            path = file.toPath();

        ProjectService projectService = mainFrame.getProjectService();
        NodeManager nodeService = (NodeManager)projectService.getNodeService();
        Node root = mainFrame.project.getRootNode();
        nodeService.create(nodeService.getFromSource(root, path), name, type);
        mainFrame.updateTree(root);
        System.out.println("New " + type.toString() + ": " + name);
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
                // test if valid filename
                errorDialog(frame, "Invalid name: " + result);
            }
        }
        return null;
    }

    public static class actNewFolder extends ActionTemplate {

        private final MainFrame mainFrame;

        public actNewFolder(MainFrame frame) {
            super(
                    "Fodler",
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

            // Invoke the showsOpenDialog function to show the save dialog
            int r = j.showOpenDialog(null);

            // If the user selects a file
            if (r == JFileChooser.APPROVE_OPTION) {

                File fi = new File(j.getSelectedFile().getAbsolutePath());
                mainFrame.loadProjectFrame(fi.toPath());
            }
        }
    }



    public static class actSave extends ActionTemplate {
        private final MainFrame mainFrame;
        private RSyntaxTextArea rSyntaxTextArea;

        public actSave(MainFrame frame, RSyntaxTextArea rSyntaxTextArea) {
            super(
                    "Save File",
                    getResizedIcon(frame, Icons.SAVE),
                    KeyEvent.VK_S,
                    "Save file (CTRL+S)",
                    KeyStroke.getKeyStroke(KeyEvent.VK_S,
                            KeyEvent.CTRL_DOWN_MASK));
            this.mainFrame = frame;
            this.rSyntaxTextArea = rSyntaxTextArea;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            // Set the label to the path of the selected directory
            File file = mainFrame.getOpenedFile();
            if (file == null)
                file = fileSelector(mainFrame);
            if (file == null)
                return;

            try {
                // Create a file writer
                FileWriter wr = new FileWriter(file, false);

                // Create buffered writer to write
                BufferedWriter w = new BufferedWriter(wr);

                // Write
                w.write(rSyntaxTextArea.getText());

                w.flush();
                w.close();
            } catch (Exception evt) {
                JOptionPane.showMessageDialog(mainFrame, evt.getMessage());
            }
        }
    }

    public static class actSaveAs extends ActionTemplate {
        private final MainFrame mainFrame;
        private JTextArea jTextArea;

        public actSaveAs(MainFrame frame) {
            super(
                    "Save As...",
                    getResizedIcon(frame, Icons.SAVE_AS),
                    KeyEvent.VK_A,
                    "Save file as",
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK)
                    );
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

            // Invoke the showsSaveDialog function to show the save dialog
            int r = j.showSaveDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {

                // Set the label to the path of the selected directory
                File fi = new File(j.getSelectedFile().getAbsolutePath());

                try {
                    // Create a file writer
                    FileWriter wr = new FileWriter(fi, false);

                    // Create buffered writer to write
                    BufferedWriter w = new BufferedWriter(wr);

                    // Write
                    w.write(jTextArea.getText());

                    w.flush();
                    w.close();
                } catch (Exception evt) {
                    JOptionPane.showMessageDialog(mainFrame, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(mainFrame, "the user cancelled the operation");
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
