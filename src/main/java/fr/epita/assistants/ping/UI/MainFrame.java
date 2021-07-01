package fr.epita.assistants.ping.UI;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.UI.Action.AnyAction;
import fr.epita.assistants.ping.UI.Action.GitAction;
import fr.epita.assistants.ping.UI.Action.IdeAction;
import fr.epita.assistants.ping.UI.Action.TreeAction;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public class MainFrame extends JFrame {

    public JFrame jFrame;

    private JTree jTree;
    private ProjectExplorer projectExplorer;

    private RSyntaxTextArea rSyntaxTextArea;
    private JToolBar jToolBar;
    private JMenuBar jMenuBar;

    public Integer iconWidth = 24;
    public Integer iconHeight = 24;

    public Project project;
    private ProjectService projectService;

    private File selectedFile = null;
    private File openedFile = null;
    public fr.epita.assistants.ping.UI.Panel.Console console;

    private UndoManager undoManager;

    public MainFrame(String title, ProjectService projectService)
    {
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
    }

    public File getOpenedFile() {
        return openedFile;
    }

    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }

    public JTree getjTree() {
        return jTree;
    }

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    public ProjectService getProjectService() {
        return projectService;
    }

    public ProjectExplorer getProjectExplorer() {
        return projectExplorer;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public RSyntaxTextArea getrSyntaxTextArea() {
        return rSyntaxTextArea;
    }

    public void setrSyntaxTextArea(RSyntaxTextArea rSyntaxTextArea) {
        this.rSyntaxTextArea = rSyntaxTextArea;
    }

    public void loadProjectFrame(Path path) {

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.removeAll();

        project = projectService.load(path);

        createTextArea();
        createMenuBar();
        createToolBar();
        JScrollPane textView = new JScrollPane(rSyntaxTextArea);

        projectExplorer= new ProjectExplorer(this, project.getRootNode());
        jTree = projectExplorer.getjTree();
        JScrollPane treeView = new JScrollPane(jTree);
        createPopupMenu();
        createConsole();
        JScrollPane consoleView = console.scrollPane;

        //jMenuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        //jToolBar.setBorder(new EtchedBorder());

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,treeView, textView);
        mainSplitPane.setResizeWeight(0.10);
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane, consoleView);


        this.setJMenuBar(jMenuBar);
        contentPane.add(jToolBar, BorderLayout.NORTH);
        contentPane.add(bottomSplitPane, BorderLayout.CENTER);
        this.pack();
        if (!this.isVisible())
            this.setVisible(true);
    }

    public static void setFont(RSyntaxTextArea textArea, Font font) {
        if (font != null) {
            SyntaxScheme ss = textArea.getSyntaxScheme();
            ss = (SyntaxScheme) ss.clone();
            for (int i = 0; i < ss.getStyleCount(); i++) {
                if (ss.getStyle(i) != null) {
                    ss.getStyle(i).font = font;
                }
            }
            textArea.setSyntaxScheme(ss);
            textArea.setFont(font);
        }
    }

    private void createTextArea() {
        rSyntaxTextArea = new RSyntaxTextArea();
        rSyntaxTextArea.setEditable(false);
        rSyntaxTextArea.setBackground(Color.DARK_GRAY);
        rSyntaxTextArea.setForeground(Color.WHITE);
        rSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        rSyntaxTextArea.setCodeFoldingEnabled(true);
        rSyntaxTextArea.setAnimateBracketMatching(true);


        undoManager = new UndoManager();
        rSyntaxTextArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        // Set the font for all token types.
        // setFont(rSyntaxTextArea, new Font("Comic Sans MS", Font.PLAIN, 16));

        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(
                    "/themes/dark.xml"));
            theme.apply(rSyntaxTextArea);
        } catch (IOException ioe) { // Never happens
            ioe.printStackTrace();
        }
    }

    private void createConsole()
    {
        console = new fr.epita.assistants.ping.UI.Panel.Console(jFrame);
        return;
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
        mFile.add(new IdeAction.actSave(this, rSyntaxTextArea));
        mFile.add(new IdeAction.actSaveAs(this, rSyntaxTextArea));
        mFile.add(new IdeAction.actExit(this));

        jMenuBar.add(mFile);
        JMenu mEdit = new JMenu("Edit");
        mEdit.setMnemonic('E');

        mEdit.add(new IdeAction.actUndo(this));
        mEdit.add(new IdeAction.actRedo(this));
        mEdit.addSeparator();
        mEdit.add(new IdeAction.actCut(this, rSyntaxTextArea));
        mEdit.add(new IdeAction.actCopy(this, rSyntaxTextArea));
        mEdit.add(new IdeAction.actPaste(this, rSyntaxTextArea));
        mEdit.add(new AnyAction.actAnyRun(this));
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
        jToolBar.setBackground(Color.GRAY);

        jToolBar.add(new IdeAction.actOpenProject(this)).setHideActionText(true);
        jToolBar.add(new IdeAction.actSave(this, rSyntaxTextArea)).setHideActionText(true);
        jToolBar.addSeparator();
        jToolBar.add(new IdeAction.actUndo(this)).setHideActionText(true);
        jToolBar.add(new IdeAction.actRedo(this)).setHideActionText(true);
        jToolBar.add(new IdeAction.actCopy(this, rSyntaxTextArea)).setHideActionText(true);
        jToolBar.add(new IdeAction.actCut(this, rSyntaxTextArea)).setHideActionText(true);
        jToolBar.add(new IdeAction.actPaste(this, rSyntaxTextArea)).setHideActionText(true);
        jToolBar.add(new AnyAction.actAnyRun(this)).setHideActionText(true);
        jToolBar.add(Box.createHorizontalGlue());
        JLabel label = new JLabel("Git:");
        jToolBar.add(label);
        jToolBar.addSeparator();
        jToolBar.add(new GitAction.actGitPull(this)).setHideActionText(true);
        jToolBar.add(new GitAction.actGitAdd(this)).setHideActionText(true);
        jToolBar.add(new GitAction.actGitCommit(this)).setHideActionText(true);
        jToolBar.add(new GitAction.actGitPush(this)).setHideActionText(true);

        jToolBar.setFloatable(false);
    }

    private void createPopupMenu()
    {
        JPopupMenu textPopupMenu = new JPopupMenu();
        JPopupMenu treePopupMenu = new JPopupMenu();
        textPopupMenu.add(new IdeAction.actCopy(this, rSyntaxTextArea));
        textPopupMenu.add(new IdeAction.actCut(this, rSyntaxTextArea));
        textPopupMenu.add(new IdeAction.actPaste(this, rSyntaxTextArea));

        JMenu mNew = new JMenu("New");
        mNew.add(new IdeAction.actNewFile(this));
        mNew.add(new IdeAction.actNewFolder(this));
        treePopupMenu.add(mNew);
        treePopupMenu.add(new TreeAction.actCopy(this));
        treePopupMenu.add(new TreeAction.actCut(this));
        treePopupMenu.add(new TreeAction.actPaste(this));

        rSyntaxTextArea.addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent event ) {
                if ( event.isPopupTrigger() ) {
                    textPopupMenu.show( event.getComponent(), event.getX(), event.getY() );
                }
            }
        });

        jTree.addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent event ) {
                if ( event.isPopupTrigger() ) {
                    treePopupMenu.show( event.getComponent(), event.getX(), event.getY() );
                }
            }
        } );
    }

    public static void main(String[] args) {
        ProjectService projectService = MyIde.init(null);

        Path path = Path.of(new File("").getAbsolutePath());
        MainFrame frame = new MainFrame("PingIDE", projectService);
        frame.loadProjectFrame(path);
        frame.setVisible(true);
    }

}
