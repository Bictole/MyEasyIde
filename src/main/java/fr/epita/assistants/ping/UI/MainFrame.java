package fr.epita.assistants.ping.UI;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectService;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.AbstractAction;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    private JFrame jFrame;

    private JTree jTree;
    private JTextArea jTextArea;
    private JToolBar jToolBar;
    private JMenuBar jMenuBar;

    private Integer iconWidth = 24;
    private Integer iconHeight = 24;

    private Project project;

    // Frame constructor
    public MainFrame(String title, ProjectService projectService, String path) {
        super(title);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

        jFrame = this;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel contentPane = (JPanel) getContentPane();

        // Text component
        jTextArea = new JTextArea();

        createMenuBar();
        createToolBar();

        project = projectService.load(Path.of(path));
        JScrollPane treeView = initTree(project.getRootNode());

        //jMenuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        //jToolBar.setBorder(new EtchedBorder());

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, jTextArea);
        mainSplitPane.setResizeWeight(0.10);
        this.setJMenuBar(jMenuBar);
        contentPane.add(jToolBar, BorderLayout.NORTH);
        contentPane.add(mainSplitPane, BorderLayout.CENTER);
        this.pack();
    }


    private void createMenuBar() {
        // Create a menubar
        jMenuBar = new JMenuBar();

        // Create a menu
        JMenu m1 = new JMenu("File");
        m1.setMnemonic('F');

        m1.add(actNew);
        m1.add(actOpenProject);
        m1.add(actSave);
        m1.add(actExit);

        // Create a menu
        JMenu m2 = new JMenu("Edit");
        m2.setMnemonic('E');

        m2.add(actCut);
        m2.add(actCopy);
        m2.add(actPaste);

        jMenuBar.add(m1);
        jMenuBar.add(m2);
    }

    private static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void createToolBar() {
        // Create a toolbar
        jToolBar = new JToolBar();

        jToolBar.add(actNew).setHideActionText(true);
        jToolBar.add(actOpenProject).setHideActionText(true);
        jToolBar.add(actSave).setHideActionText(true);
        jToolBar.addSeparator();
        jToolBar.add(actCopy).setHideActionText(true);
        jToolBar.add(actCut).setHideActionText(true);
        jToolBar.add(actPaste).setHideActionText(true);

        jToolBar.setFloatable(false);
    }

    private static String createFilePath(TreePath treePath) {
        StringBuilder sb = new StringBuilder();
        Object[] nodes = treePath.getPath();
        if (nodes.length == 0)
            return sb.toString();
        sb.append(nodes[0].toString());
        for (int i = 1; i < nodes.length; i++) {
            sb.append(File.separatorChar).append(nodes[i].toString());
        }
        return sb.toString();
    }

    private void mouseOpenFile(MouseEvent e) {
        TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());
        File file = new File(createFilePath(tp));
        if (file.isDirectory())
            return;
        // Set the label to the path of the selected directory
        try {
            String text = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
            // Set the text
            jTextArea.setText(text);
        } catch (Exception evt) {
            JOptionPane.showMessageDialog(jFrame, evt.getMessage());
        }

    }

    private JScrollPane initTree(Node root) {
        DefaultMutableTreeNode top = createTree(root);
        jTree = new JTree(top);
        jTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mouseOpenFile(e);
                }
            }
        });
        JScrollPane treeView = new JScrollPane(jTree);
        return treeView;
    }

    DefaultMutableTreeNode createTree(Node root) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(root.getPath());
        File fRoot = root.getPath().toFile();
        if (!(fRoot.exists() && fRoot.isDirectory()))
            return top;

        fillTree(top, fRoot);

        return top;
    }

    //////////////////////////////
    void fillTree(DefaultMutableTreeNode root, File dir) {

        File[] tmp = dir.listFiles();
        List<File> ol = new ArrayList<File>(Arrays.asList(tmp));
        Collections.sort(ol, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isDirectory()) {
                    return o1.compareTo(o2);
                } else if (o1.isDirectory()) {
                    return -1;
                } else if (o2.isDirectory()) {
                    return 1;
                }
                return o1.compareTo(o2);
            }
        });

        for (int fnum = 0; fnum < ol.size(); fnum++) {

            File file = ol.get(fnum);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
            if (file.isDirectory()) {
                fillTree(node, file);
            }
            root.add(node);
        }
    }

    ////////////////////////////////////////////
    //ACTIONS
    ////////////////////////////////////////////

    private AbstractAction actNew = new AbstractAction() {
        {
            putValue(Action.NAME, "New...");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/newFile.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
            putValue(Action.SHORT_DESCRIPTION, "New (CTRL+N)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("New");
        }
    };

    private AbstractAction actOpenFile = new AbstractAction() {
        {
            putValue(Action.NAME, "Open File...");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/open.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
            putValue(Action.SHORT_DESCRIPTION, "Open file (CTRL+O)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("OpenFile");

            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser(project.getRootNode().getPath().toFile());

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
                    JOptionPane.showMessageDialog(jFrame, evt.getMessage());
                }
            } else
                JOptionPane.showMessageDialog(jFrame, "User cancelled operation");
        }
    };

    private AbstractAction actOpenProject = new AbstractAction() {
        {
            putValue(Action.NAME, "Open File...");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/open.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
            putValue(Action.SHORT_DESCRIPTION, "Open file (CTRL+O)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("OpenProject");

            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser(project.getRootNode().getPath().toFile());

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
                    JOptionPane.showMessageDialog(jFrame, evt.getMessage());
                }
            } else
                JOptionPane.showMessageDialog(jFrame, "User cancelled operation");
        }
    };

    private AbstractAction actSave = new AbstractAction() {
        {
            putValue(Action.NAME, "Save File");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/save.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(Action.SHORT_DESCRIPTION, "Save file (CTRL+S)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Save");
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
                    JOptionPane.showMessageDialog(jFrame, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(jFrame, "the user cancelled the operation");
        }
    };

    private AbstractAction actSaveAs = new AbstractAction() {
        {
            putValue(Action.NAME, "Save As...");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/save_as.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
            putValue(Action.SHORT_DESCRIPTION, "Save file as");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Save as");
        }
    };

    private AbstractAction actCopy = new AbstractAction() {
        {
            putValue(Action.NAME, "Copy");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/copy.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
            putValue(Action.SHORT_DESCRIPTION, "Copy (CTRL+C)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jTextArea.copy();
            System.out.println("Copy");
        }
    };

    private AbstractAction actCut = new AbstractAction() {
        {
            putValue(Action.NAME, "Cut");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/cut.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
            putValue(Action.SHORT_DESCRIPTION, "Cut (CTRL+X)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jTextArea.cut();
            System.out.println("Cut");
        }
    };

    private AbstractAction actPaste = new AbstractAction() {
        {
            putValue(Action.NAME, "Paste");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/paste.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
            putValue(Action.SHORT_DESCRIPTION, "Paste (CTRL+V)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jTextArea.paste();
            System.out.println("Paste");
        }
    };

    private AbstractAction actExit = new AbstractAction() {
        {
            putValue(Action.NAME, "Exit");
            putValue(Action.SMALL_ICON, resizeIcon(new ImageIcon("src/main/resources/icons/exit.png"), iconWidth, iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
            putValue(Action.SHORT_DESCRIPTION, "Exit (ALT+F4)");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Exit");
        }
    };

    public static void main(String[] args) {

        ProjectService projectService = MyIde.init(null);
        String path = ".";
        JFrame frame = new MainFrame("MyIDE", projectService, path);
        frame.setVisible(true);
    }

}
