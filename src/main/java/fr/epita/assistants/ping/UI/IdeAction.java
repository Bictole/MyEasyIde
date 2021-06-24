package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.service.NodeManager;
import fr.epita.assistants.ping.service.ProjectManager;
import org.eclipse.sisu.launch.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IdeAction {

    enum Icons {
        NEW_PROJECT("newProject.png"),
        OPEN_PROJECT("open.png"),
        NEW_FOLDER("newFolder.png"),
        NEW_FILE("newFile.png"),
        OPEN("open.png"),
        SAVE("save.png"),
        SAVE_AS("save_as.png"),
        COPY("copy.png"),
        CUT("cut.png"),
        PASTE("paste.png"),
        EXIT("exit.png");

        String path;

        Icons(String s) {
            String mainPath = "src/main/resources/icons/";
            path = mainPath + s;
        }
    }

    public static class actNewProject extends AbstractAction {

        private MainFrame mainFrame;

        public actNewProject(MainFrame frame) {
            this.mainFrame = frame;
            putValue(Action.NAME, "Project");
            putValue(Action.SMALL_ICON, mainFrame.resizeIcon(new ImageIcon(Icons.NEW_PROJECT.path), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(Action.SHORT_DESCRIPTION, "New Project");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame f = new JFrame("New Project");
            JLabel nameLabel = new JLabel("Name:");

            JTextField nameTextField = new JTextField("undefined");

            JLabel locationLabel = new JLabel("Location:");
            JTextField locationTextField = new JTextField();
            JButton locationButton = new JButton(mainFrame.resizeIcon(new ImageIcon(Icons.OPEN_PROJECT.path), 16, 16));
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
            JButton btnCancel = new JButton("Cancel");

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

            f.pack();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            f.setLocation(dim.width / 2 - f.getSize().width / 2, dim.height / 2 - f.getSize().height / 2);
            f.setSize(dim.width / 4, dim.height / 8);
            f.setVisible(true);
        }
/*
        JFrame f = new JFrame();
            JPanel panel = new JPanel(new BorderLayout());

            JPanel nameP = new JPanel(new BorderLayout());

            JLabel nameL = new JLabel("Project Name");
            JTextField nameTF = new JTextField("undefined");

            nameP.add(nameL, BorderLayout.WEST);
            nameP.add(nameTF, BorderLayout.CENTER);

            JPanel locationP = new JPanel(new BorderLayout());
            JLabel locationL = new JLabel("Project Location");
            JTextField locationTF = new JTextField();
            JButton locationB = new JButton(mainFrame.resizeIcon(new ImageIcon("src/main/resources/icons/open.png"), mainFrame.iconWidth, mainFrame.iconWidth));
            locationB.addActionListener(new AbstractAction() {
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
                        locationTF.setText(fi.getAbsolutePath());
                    }
                }
            });

            locationP.add(locationL, BorderLayout.WEST);
            locationP.add(locationTF, BorderLayout.CENTER);
            locationP.add(locationB, BorderLayout.EAST);

            panel.add(nameP, BorderLayout.CENTER);
            panel.add(locationP, BorderLayout.CENTER);
            f.add(panel);
            f.setSize(300, 300);
            f.setVisible(true);
            /*JOptionPane optionPane = new JOptionPane();
            if (optionPane.showConfirmDialog(mainFrame, box, "Project information", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            {
                Path p = Path.of(locationTF.getText());

            }*/
    }

    public static class actNewFolder extends AbstractAction {

        private MainFrame mainFrame;

        public actNewFolder(MainFrame frame) {
            this.mainFrame = frame;
            putValue(Action.NAME, "Folder");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon(Icons.NEW_FOLDER.path), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(Action.SHORT_DESCRIPTION, "New Folder");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
            // dialog to get the name
            nodeService.create(nodeService.getFromSource(root, path), "New Folder", Node.Types.FOLDER);

            System.out.println("New folder");
        }
    }

    public static class actNewFile extends AbstractAction {
        private MainFrame mainFrame;

        public actNewFile(MainFrame frame) {
            this.mainFrame = frame;
            putValue(Action.NAME, "File");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon(Icons.NEW_FILE.path), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(Action.SHORT_DESCRIPTION, "New File");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
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
            // dialog to get the name
            nodeService.create(nodeService.getFromSource(root, path), "New file", Node.Types.FILE);

            System.out.println("New file");
        }
    }


    public static class actOpenFile extends AbstractAction {
        private MainFrame mainFrame;
        private JTextArea jTextArea;

        public actOpenFile(MainFrame frame, JTextArea jTextArea) {
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
            putValue(Action.NAME, "Open File");
            putValue(Action.SMALL_ICON, mainFrame.resizeIcon(new ImageIcon(Icons.OPEN.path), mainFrame.iconWidth, mainFrame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
            putValue(Action.SHORT_DESCRIPTION, "Open file (CTRL+O)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
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

    public static class actOpenProject extends AbstractAction {
        private MainFrame mainFrame;

        public actOpenProject(MainFrame frame) {
            this.mainFrame = frame;
            putValue(Action.NAME, "Open Project");
            putValue(Action.SMALL_ICON, mainFrame.resizeIcon(new ImageIcon(Icons.OPEN.path), mainFrame.iconWidth, mainFrame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
            putValue(Action.SHORT_DESCRIPTION, "Open Project (CTRL+O)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser(mainFrame.project.getRootNode().getPath().toFile());

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

    /*
    public static abstract class FileAction extends AbstractAction {
        private JTextArea jTextArea;

        public FileAction(String name, Icon icon, KeyEvent mnemonic, String description, KeyStroke keyStroke) {
            putValue(Action.NAME, name);
            putValue(Action.SMALL_ICON, icon);
            putValue(Action.MNEMONIC_KEY, mnemonic);
            putValue(Action.SHORT_DESCRIPTION, description);
            putValue(Action.ACCELERATOR_KEY, keyStroke);
        }
    }
    */

    public static class actSave extends AbstractAction {
        private MainFrame mainFrame;
        private JTextArea jTextArea;

        public actSave(MainFrame frame) {
            this.mainFrame = frame;
            putValue(Action.NAME, "Save File");
            putValue(Action.SMALL_ICON, mainFrame.resizeIcon(new ImageIcon(Icons.SAVE.path), mainFrame.iconWidth, mainFrame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(Action.SHORT_DESCRIPTION, "Save file (CTRL+S)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
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

    ;

    public static class actSaveAs extends AbstractAction {
        private MainFrame mainFrame;

        public actSaveAs(MainFrame frame) {
            this.mainFrame = frame;
            putValue(Action.NAME, "Save As...");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon(Icons.SAVE_AS.path), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
            putValue(Action.SHORT_DESCRIPTION, "Save file as");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

    public static class actCopy extends AbstractAction {
        private MainFrame mainFrame;
        private JTextArea jTextArea;

        public actCopy(MainFrame frame, JTextArea jTextArea) {
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
            putValue(Action.NAME, "Copy");
            putValue(Action.SMALL_ICON, mainFrame.resizeIcon(new ImageIcon(Icons.COPY.path), mainFrame.iconWidth, mainFrame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
            putValue(Action.SHORT_DESCRIPTION, "Copy (CTRL+C)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jTextArea.copy();
        }
    }

    public static class actCut extends AbstractAction {
        private MainFrame mainFrame;
        private JTextArea jTextArea;

        public actCut(MainFrame frame, JTextArea jTextArea) {
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
            putValue(Action.NAME, "Cut");
            putValue(Action.SMALL_ICON, mainFrame.resizeIcon(new ImageIcon(Icons.CUT.path), mainFrame.iconWidth, mainFrame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
            putValue(Action.SHORT_DESCRIPTION, "Cut (CTRL+X)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jTextArea.cut();
        }
    }

    public static class actPaste extends AbstractAction {
        private MainFrame mainFrame;

        private JTextArea jTextArea;

        public actPaste(MainFrame frame, JTextArea jTextArea) {
            this.mainFrame = frame;
            this.jTextArea = jTextArea;
            putValue(Action.NAME, "Paste");
            putValue(Action.SMALL_ICON, mainFrame.resizeIcon(new ImageIcon(Icons.PASTE.path), mainFrame.iconWidth, mainFrame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
            putValue(Action.SHORT_DESCRIPTION, "Paste (CTRL+V)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jTextArea.paste();
        }
    }

    public static class actExit extends AbstractAction {
        private MainFrame mainFrame;

        public actExit(MainFrame frame) {
            mainFrame = frame;
            putValue(Action.NAME, "Exit");
            putValue(Action.SMALL_ICON, mainFrame.resizeIcon(new ImageIcon(Icons.EXIT.path), mainFrame.iconWidth, mainFrame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
            putValue(Action.SHORT_DESCRIPTION, "Exit (ALT+F4)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }
    }

    ;
}
