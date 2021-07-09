package fr.epita.assistants.ping.UI;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.UI.Action.*;
import fr.epita.assistants.ping.UI.Panel.Tab;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import fr.epita.assistants.ping.UI.Panel.Graphics;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;


public class MainFrame extends JFrame implements SyntaxConstants {

    public JFrame jFrame;

    private JTree jTree;
    private ProjectExplorer projectExplorer;

    private RSyntaxTextArea rSyntaxTextArea;
    public JPanel textArea;
    private JToolBar jToolBar;
    private JMenuBar jMenuBar;

    public Integer iconWidth = 24;
    public Integer iconHeight = 24;

    public Project project;
    private ProjectService projectService;

    private File selectedFile = null;
    private File openedFile = null;
    public fr.epita.assistants.ping.UI.Panel.Console console;

    public TabManager tabManager;
    public JScrollPane textView;

    public MainFrame(String title, ProjectService projectService) {
        super(title);

        /*try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        jFrame = this;
        this.projectService = projectService;

        System.setProperty("GRIS_SOMBRE", "0X221F1F");
        System.setProperty("GRIS_CLAIR", "0X737373");
        System.setProperty("GRIS_MIDDLE", "0x3A353C");
        System.setProperty("PRUNE", "0X44233B");
        System.setProperty("BORDEAU", "0X682636");
        System.setProperty("BLEU_ELECTRIQUE", "0X5AC0E6");
        System.setProperty("VIOLET", "0X844CA2");
        System.setProperty("ROSE", "0XE54F72");
        System.setProperty("ROSE_CLAIR", "0XF19CBD");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.tabManager = new TabManager("dark", this);
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
        return tabManager.getUndoManager();
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

        //createTextArea();
        createMenuBar();
        createToolBar();

        projectExplorer= new ProjectExplorer(this, project.getRootNode());
        jTree = projectExplorer.getjTree();
        JScrollPane treeView = new JScrollPane(jTree);
        
        //Graphics.ScrollPaneDesign(textView, Color.getColor("GRIS_MIDDLE"));
        Graphics.ScrollPaneDesign(treeView, Color.getColor("PRUNE"));

        jTree.setBorder(BorderFactory.createEmptyBorder());

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setTextSelectionColor(Color.white);
        renderer.setBackgroundSelectionColor(Color.getColor("ROSE"));
        renderer.setBorderSelectionColor(Color.black);
        renderer.setTextNonSelectionColor(Color.getColor("BLEU_ELECTRIQUE"));
        renderer.setBackgroundNonSelectionColor(Color.getColor("PRUNE"));
        renderer.setClosedIcon(new ImageIcon(UITools.ImageResize.ImageTest(Icons.FOLDER, 10)));
        renderer.setLeafIcon(new ImageIcon(UITools.ImageResize.ImageTest(Icons.LEAF, 10)));
        renderer.setOpenIcon(new ImageIcon(UITools.ImageResize.ImageTest(Icons.OPENED_FOLDER, 10)));
        jTree.putClientProperty("JTree.lineStyle", "None");

        jTree.setCellRenderer(renderer);
        jTree.setBackground(Color.getColor("PRUNE"));


        createTreePopUpMenu();
        createConsole();
        JScrollPane consoleView = console.scrollPane;
        Graphics.ScrollPaneDesign(consoleView, Color.getColor("GRIS_MIDDLE"));

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, tabManager.tabPane);
        mainSplitPane.setResizeWeight(0.10);
        Graphics.BottomSplitPaneDesign(mainSplitPane);
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane, consoleView);
        bottomSplitPane.setResizeWeight(0.70);
        Graphics.BottomSplitPaneDesign(bottomSplitPane);

        this.setJMenuBar(jMenuBar);
        jFrame.add(jToolBar, BorderLayout.NORTH);
        jFrame.add(bottomSplitPane, BorderLayout.CENTER);
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

    private CompletionProvider createCompletionProvider() {
        // This provider has no understanding of
        // language semantics. It simply checks the text entered up to the
        // caret position for a match against known completions.
        DefaultCompletionProvider provider = new DefaultCompletionProvider();

        // Add completions for all Java keywords
        provider.addCompletion(new BasicCompletion(provider, "abstract"));
        provider.addCompletion(new BasicCompletion(provider, "assert"));
        provider.addCompletion(new BasicCompletion(provider, "break"));
        provider.addCompletion(new BasicCompletion(provider, "case"));

        provider.addCompletion(new BasicCompletion(provider, "private"));
        provider.addCompletion(new BasicCompletion(provider, "public"));
        provider.addCompletion(new BasicCompletion(provider, "protected"));
        provider.addCompletion(new BasicCompletion(provider, "class"));
        // ... etc ...
        provider.addCompletion(new BasicCompletion(provider, "transient"));
        provider.addCompletion(new BasicCompletion(provider, "try"));
        provider.addCompletion(new BasicCompletion(provider, "void"));
        provider.addCompletion(new BasicCompletion(provider, "volatile"));
        provider.addCompletion(new BasicCompletion(provider, "while"));

        // Add a couple of "shorthand" completions. These completions don't
        // require the input text to be the same thing as the replacement text.
        provider.addCompletion(new ShorthandCompletion(provider, "sout",
                "System.out.println(", "System.out.println("));
        provider.addCompletion(new ShorthandCompletion(provider, "serr",
                "System.err.println(", "System.err.println("));

        return provider;
    }

    private void createConsole() {
        console = new fr.epita.assistants.ping.UI.Panel.Console(jFrame);
        return;
    }

    private void createMenuBar() {
        // Create a menubar
        jMenuBar = new JMenuBar();
        jMenuBar.setBackground(Color.getColor("GRIS_SOMBRE"));
        jMenuBar.setBorder(BorderFactory.createEmptyBorder());

        // Create a menu
        JMenu mFile = new JMenu("File");
        mFile.setForeground(Color.WHITE);
        mFile.setMnemonic('F');
        JMenu mNew = new JMenu("New");
        mNew.add(new IdeAction.actNewProject(this));
        mNew.addSeparator();
        mNew.add(new IdeAction.actNewFile(this));
        mNew.add(new IdeAction.actNewFolder(this));

        mFile.add(mNew);
        mFile.add(new IdeAction.actOpenProject(this));
        mFile.add(new IdeAction.actSave(this));
        mFile.add(new IdeAction.actSaveAs(this, tabManager.getCurrentTextArea()));
        mFile.add(new IdeAction.actExit(this));

        jMenuBar.add(mFile);
        JMenu mEdit = new JMenu("Edit");
        mEdit.setMnemonic('E');
        mEdit.setForeground(Color.WHITE);

        mEdit.add(new IdeAction.actUndo(this));
        mEdit.add(new IdeAction.actRedo(this));
        mEdit.addSeparator();
        mEdit.add(new IdeAction.actCut(this, tabManager.getCurrentTextArea()));
        mEdit.add(new IdeAction.actCopy(this, tabManager.getCurrentTextArea()));
        mEdit.add(new IdeAction.actPaste(this, tabManager.getCurrentTextArea()));
        jMenuBar.add(mEdit);

        JMenu mTools= new JMenu("Tools");
        mTools.setMnemonic('T');
        mTools.setForeground(Color.WHITE);

        mTools.add(new AnyAction.actAnyCleanUp(this));
        mTools.add(new AnyAction.actAnyDist(this));
        mTools.addSeparator();
        mTools.add(new AnyAction.actAnyRun(this));
        jMenuBar.add(mTools);

        if (project.getAspects().stream().anyMatch(a -> a.getType() == Mandatory.Aspects.GIT)) {
            JMenu mGit = new JMenu("Git");
            mGit.setForeground(Color.WHITE);
            mGit.setMnemonic('G');
            mGit.add(new GitAction.actGitPull(this));
            mGit.add(new GitAction.actGitAdd(this));
            mGit.add(new GitAction.actGitCommit(this));
            mGit.add(new GitAction.actGitPush(this));

            jMenuBar.add(mGit);
        }

        if (project.getAspects().stream().anyMatch(a -> a.getType() == Mandatory.Aspects.MAVEN)) {
            JMenu mMaven = new JMenu("Maven");
            mMaven.setForeground(Color.WHITE);
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

    private JButton createToolBarButton(AbstractAction action)
    {
        JButton b = new JButton(action);
        //b.setBackground(Color.getColor("GRIS_MIDDLE"));
        //b.setForeground(Color.getColor("GRIS_MIDDLE"));
        b.setOpaque(false);
        b.setBorder(BorderFactory.createLineBorder(Color.getColor("GRIS_MIDDLE"), 5));
        b.setHideActionText(true);
        b.setRolloverEnabled(true);
        //b.setIcon(new ImageIcon(UITools.ImageResize.ImageTest(Icons.OPEN)));
        return b;
    }

    private void createToolBar() {
        // Create a toolbar
        jToolBar = new JToolBar();
        jToolBar.setBorder(BorderFactory.createBevelBorder(1, Color.getColor("GRIS_MIDDLE"), Color.getColor("GRIS_MIDDLE")));
        jToolBar.setBackground(Color.getColor("GRIS_MIDDLE"));
        jToolBar.setForeground(Color.getColor("GRIS_MIDDLE"));

        jToolBar.add(createToolBarButton(new IdeAction.actOpenProject(this)));

        jToolBar.add(createToolBarButton(new IdeAction.actSave(this)));
        jToolBar.addSeparator();
        jToolBar.add(createToolBarButton(new IdeAction.actUndo(this)));
        jToolBar.add(createToolBarButton(new IdeAction.actRedo(this)));
        jToolBar.add(createToolBarButton(new IdeAction.actCopy(this, tabManager.getCurrentTextArea())));
        jToolBar.add(createToolBarButton(new IdeAction.actCut(this, tabManager.getCurrentTextArea())));
        jToolBar.add(createToolBarButton(new IdeAction.actPaste(this, tabManager.getCurrentTextArea())));

        jToolBar.add(Box.createHorizontalGlue());
        if (project.getAspects().stream().anyMatch(aspect -> aspect.getType()== Mandatory.Aspects.MAVEN))
            jToolBar.add(createToolBarButton(new MavenAction.actMvnExec(this)));
        else if (project.getAspects().stream().anyMatch(aspect -> aspect.getType()== Mandatory.Aspects.ANY))
            jToolBar.add(createToolBarButton(new AnyAction.actAnyRun(this)));
        
        jToolBar.add(Box.createHorizontalGlue());
        JLabel label = new JLabel("Git:");
        label.setForeground(Color.WHITE);
        jToolBar.add(label);
        jToolBar.addSeparator();
        jToolBar.add(createToolBarButton(new GitAction.actGitPull(this)));
        jToolBar.add(createToolBarButton(new GitAction.actGitAdd(this)));
        jToolBar.add(createToolBarButton(new GitAction.actGitCommit(this)));
        jToolBar.add(createToolBarButton(new GitAction.actGitPush(this)));

        jToolBar.setFloatable(false);
    }

    public static void createTextPopupMenu(MainFrame frame, RSyntaxTextArea textArea)
    {
        JPopupMenu textPopupMenu = new JPopupMenu();
        textPopupMenu.add(new IdeAction.actCopy(frame, textArea));
        textPopupMenu.add(new IdeAction.actCut(frame, textArea));
        textPopupMenu.add(new IdeAction.actPaste(frame, textArea));
        textPopupMenu.add(new IdeAction.actUndo(frame));
        textPopupMenu.add(new IdeAction.actRedo(frame));


        textArea.setPopupMenu(textPopupMenu);
    }

    private void createTreePopUpMenu()
    {
        JPopupMenu treePopupMenu = new JPopupMenu();
        JMenu mNew = new JMenu("New");
        mNew.add(new IdeAction.actNewFile(this));
        mNew.add(new IdeAction.actNewFolder(this));
        treePopupMenu.add(mNew);
        treePopupMenu.add(new TreeAction.actCopy(this));
        treePopupMenu.add(new TreeAction.actCut(this));
        treePopupMenu.add(new TreeAction.actPaste(this));
        treePopupMenu.add(new TreeAction.actDelete(this));

        jTree.setComponentPopupMenu(treePopupMenu);
    }

    public static void main(String[] args) {
        ProjectService projectService = MyIde.init(null);

        Path path = Path.of(new File("").getAbsolutePath());
        MainFrame frame = new MainFrame("PingIDE", projectService);
        frame.loadProjectFrame(path);
        frame.setVisible(true);
    }

}
