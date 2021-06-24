package fr.epita.assistants.ping.UI;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectService;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainFrame extends JFrame implements ActionListener {

    private JPanel mainPanel;

    private JTree jTree;
    private JTextArea jTextArea;
    private JToolBar jToolBar;
    private JMenuBar jMenuBar;

    private void createMenuBar() {
        // Create a menubar
        jMenuBar = new JMenuBar();

        // Create amenu for menu
        JMenu m1 = new JMenu("File");

        // Create menu items
        JMenuItem mi1 = new JMenuItem("New");
        JMenuItem mi2 = new JMenuItem("Open");
        JMenuItem mi3 = new JMenuItem("Save");

        // Add action listener
        mi1.addActionListener(this);
        mi2.addActionListener(this);
        mi3.addActionListener(this);

        m1.add(mi1);
        m1.add(mi2);
        m1.add(mi3);

        // Create amenu for menu
        JMenu m2 = new JMenu("Edit");

        // Create menu items
        JMenuItem mi4 = new JMenuItem("Cut");
        JMenuItem mi5 = new JMenuItem("Copy");
        JMenuItem mi6 = new JMenuItem("Paste");

        // Add action listener
        mi4.addActionListener(this);
        mi5.addActionListener(this);
        mi6.addActionListener(this);

        m2.add(mi4);
        m2.add(mi5);
        m2.add(mi6);

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

        JButton btnNew = new JButton(resizeIcon(new ImageIcon("src/main/resources/icons/newFile.png"), 24, 24));
        btnNew.setToolTipText("New File (CTRL+N)");
        btnNew.addActionListener(this);
        jToolBar.add(btnNew);

        JButton btnOpen = new JButton(resizeIcon(new ImageIcon("src/main/resources/icons/folder.png"), 24, 24));
        btnOpen.setToolTipText("Open (CTRL+O)");
        btnOpen.addActionListener(this);
        jToolBar.add(btnOpen);

        JButton btnSave = new JButton(resizeIcon(new ImageIcon("src/main/resources/icons/save.png"), 24, 24));
        btnSave.setToolTipText("Save (CTRL+S)");
        btnSave.addActionListener(this);
        jToolBar.add(btnSave);

        jToolBar.addSeparator();

        JButton btnCopy = new JButton(resizeIcon(new ImageIcon("src/main/resources/icons/copy.png"), 24,24));
        btnCopy.setToolTipText("Copy (CTRL+C)");
        btnCopy.addActionListener(this);
        jToolBar.add(btnCopy);

        JButton btnCut = new JButton(resizeIcon(new ImageIcon("src/main/resources/icons/cut.png"), 24, 24));
        btnCut.setToolTipText("Cut (CTRL+X)");
        btnCut.addActionListener(this);
        jToolBar.add(btnCut);

        JButton btnPaste = new JButton(resizeIcon(new ImageIcon("src/main/resources/icons/paste.png"), 24, 24));
        btnPaste.setToolTipText("Paste (CTRL+V)");
        btnPaste.addActionListener(this);
        jToolBar.add(btnPaste);

        jToolBar.setFloatable(false);

    }

    private JScrollPane initTree(Node root) {
        DefaultMutableTreeNode top = createTree(root);
        jTree = new JTree(top);
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
                if(o1.isDirectory() && o2.isDirectory()){
                    return o1.compareTo(o2);
                } else if(o1.isDirectory()){
                    return -1;
                } else if(o2.isDirectory()){
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


    MainFrame(String title, ProjectService projectService, String path) {
        super(title);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel contentPane = (JPanel) getContentPane();

        // Text component
        jTextArea = new JTextArea();

        createMenuBar();
        createToolBar();

        Project project = projectService.load(Path.of(path));
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

    @Override
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        System.out.println(s);
        if (s.equals("cut")) {
            jTextArea.cut();
        } else if (s.equals("copy")) {
            jTextArea.copy();
        } else if (s.equals("paste")) {
            jTextArea.paste();
        } else if (s.equals("Save")) {
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
                    JOptionPane.showMessageDialog(this, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(this, "the user cancelled the operation");
        } else if (s.equals("Open")) {
            // Create an object of JFileChooser class
            JFileChooser j = new JFileChooser("f:");

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
                    JOptionPane.showMessageDialog(this, evt.getMessage());
                }
            }
            // If the user cancelled the operation
            else
                JOptionPane.showMessageDialog(this, "User cancelled operation");
        } else if (s.equals("New")) {
            jTextArea.setText("");
        }
    }

    public static void main(String[] args) {

        ProjectService projectService = MyIde.init(null);
        String path = ".";
        JFrame frame = new MainFrame("MyIDE", projectService, path);
        frame.setVisible(true);
    }

}
