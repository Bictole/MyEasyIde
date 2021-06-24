package fr.epita.assistants.ping.UI;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.*;
import fr.epita.assistants.myide.domain.service.ProjectService;
//import fr.epita.assistants.ping.UI.examples.JCheckBoxes;
import fr.epita.assistants.ping.aspect.GitAspect;
import fr.epita.assistants.ping.aspect.MavenAspect;
import fr.epita.assistants.ping.project.AnyProject;
import org.eclipse.jgit.api.Status;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.AbstractAction;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {

    public JFrame jFrame;

    private JTree jTree;
    private JTextArea jTextArea;
    private JToolBar jToolBar;
    private JMenuBar jMenuBar;

    public Integer iconWidth = 24;
    public Integer iconHeight = 24;

    public Project project;
    private ProjectService projectService;

    private File selectedFile = null;

    // Frame constructor
    public MainFrame(String title, ProjectService projectService, Path path) {
        super(title);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

        jFrame = this;
        this.projectService = projectService;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        loadProjectFrame(path);
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public void loadProjectFrame(Path path) {
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.removeAll();

        // Text component
        jTextArea = new JTextArea();
        JScrollPane textView = new JScrollPane(jTextArea);
        project = projectService.load(path);

        createMenuBar();
        createToolBar();
        JScrollPane treeView = initTree(project.getRootNode());
        //jMenuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        //jToolBar.setBorder(new EtchedBorder());

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, textView);
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
        JMenu mFile = new JMenu("File");
        mFile.setMnemonic('F');
        JMenu mNew = new JMenu("New");
        mNew.add(new IdeAction.actNewProject(this));
        mNew.addSeparator();
        mNew.add(new IdeAction.actNewFile(this));
        mNew.add(new IdeAction.actNewFolder(this));

        mFile.add(mNew);
        mFile.add(new IdeAction.actOpenProject(this));
        mFile.add(new IdeAction.actSave(this));
        mFile.add(new IdeAction.actExit(this));

        jMenuBar.add(mFile);

        JMenu mEdit = new JMenu("Edit");
        mEdit.setMnemonic('E');

        mEdit.add(new IdeAction.actCut(this, jTextArea));
        mEdit.add(new IdeAction.actCopy(this, jTextArea));
        mEdit.add(new IdeAction.actPaste(this, jTextArea));
        jMenuBar.add(mEdit);

        if (project.getAspects().stream().anyMatch(a -> a.getType() == Mandatory.Aspects.GIT)) {
            JMenu mGit = new JMenu("Git");
            mGit.setMnemonic('G');
            mGit.add(new GitAction.actGitPull(this));
            mGit.add(new GitAction.actGitAdd(this));
            mGit.add(new GitAction.actGitCommit(this));
            mGit.add(new GitAction.actGitPush(this));

            jMenuBar.add(mGit);
        }

        if (project.getAspects().stream().anyMatch(a -> a.getType() == Mandatory.Aspects.MAVEN)) {
            JMenu mMaven = new JMenu("Maven");
            mMaven.setMnemonic('M');
            mMaven.add(new MavenAction.actMvnClean(this));
            mMaven.add(new MavenAction.actMvnCompile(this));
            mMaven.add(new MavenAction.actMvnExec(this));
            mMaven.add(new MavenAction.actMvnInstall(this));
            mMaven.add(new MavenAction.actMvnPackage(this));
            mMaven.add(new MavenAction.actMvnTest(this));
            mMaven.add(new MavenAction.actMvnTree(this));
            jMenuBar.add(mMaven);
        }

    }

    public static Icon resizeIcon(ImageIcon icon, int resizedWidth, int resizedHeight) {
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(resizedWidth, resizedHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    private void createToolBar() {
        // Create a toolbar
        jToolBar = new JToolBar();

        //jToolBar.add(actNew).setHideActionText(true);
        jToolBar.add(new IdeAction.actNewProject(this)).setHideActionText(true);
        jToolBar.add(new IdeAction.actNewFile(this)).setHideActionText(true);
        jToolBar.add(new IdeAction.actOpenProject(this)).setHideActionText(true);
        jToolBar.add(new IdeAction.actSave(this)).setHideActionText(true);
        jToolBar.add(new IdeAction.actOpenProject(this)).setHideActionText(true);
        jToolBar.addSeparator();
        jToolBar.add(new IdeAction.actCopy(this, jTextArea)).setHideActionText(true);
        jToolBar.add(new IdeAction.actCut(this, jTextArea)).setHideActionText(true);
        jToolBar.add(new IdeAction.actPaste(this, jTextArea)).setHideActionText(true);
        jToolBar.add(new GitAction.actGitCommit(this)).setHideActionText(true);
        jToolBar.add(new GitAction.actGitAdd(this)).setHideActionText(true);
        jToolBar.add(new GitAction.actGitPull(this)).setHideActionText(true);
        jToolBar.add(new GitAction.actGitPush(this)).setHideActionText(true);

        jToolBar.setFloatable(false);
    }

    ////////////////////////////////////////////
    //EXPLORER
    ////////////////////////////////////////////

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
        if (tp == null)
            return;
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
                if (e.getClickCount() == 1) {
                    TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());
                    if (tp == null)
                        return;
                    selectedFile = new File(createFilePath(tp));
                }
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

    public static void main(String[] args) {
        ProjectService projectService = MyIde.init(null);
        Path path = Path.of(new File("").getAbsolutePath());
        JFrame frame = new MainFrame("MyIDE", projectService, path);
        frame.setVisible(true);
    }

}
