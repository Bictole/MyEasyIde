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
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
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

import fr.epita.assistants.ping.UI.Panel.Graphics;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;


public class MainFrame extends JFrame implements SyntaxConstants {

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

    public MainFrame(String title, ProjectService projectService) {
        super(title);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        
        Graphics.ScrollPaneDesign(textView, Color.getColor("GRIS_MIDDLE"));
        Graphics.ScrollPaneDesign(treeView, Color.getColor("PRUNE"));

        jTree.setBorder(BorderFactory.createEmptyBorder());

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) jTree.getCellRenderer();
        renderer.setTextSelectionColor(Color.white);
        renderer.setBackgroundSelectionColor(Color.getColor("ROSE"));
        renderer.setBorderSelectionColor(Color.black);
        renderer.setTextNonSelectionColor(Color.getColor("BLEU_ELECTRIQUE"));
        renderer.setBackgroundNonSelectionColor(Color.getColor("PRUNE"));
        jTree.setBackground(Color.getColor("PRUNE"));


        createPopupMenu();
        createConsole();
        JScrollPane consoleView = console.scrollPane;
        Graphics.ScrollPaneDesign(consoleView, Color.getColor("GRIS_MIDDLE"));

        //jMenuBar.setBorder(new BevelBorder(BevelBorder.RAISED));
        //jToolBar.setBorder(new EtchedBorder());

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeView, textView);
        Graphics.BottomSplitPaneDesign(mainSplitPane);
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane, consoleView);
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

    private void createTextArea() {
        rSyntaxTextArea = new RSyntaxTextArea();
        rSyntaxTextArea.setEditable(false);
        rSyntaxTextArea.setBackground(Color.DARK_GRAY);
        rSyntaxTextArea.setForeground(Color.WHITE);
        rSyntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        rSyntaxTextArea.setCodeFoldingEnabled(true);
        rSyntaxTextArea.setAnimateBracketMatching(true);
        rSyntaxTextArea.setBorder(BorderFactory.createEmptyBorder());


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

        // Autocompletion Module
        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.install(rSyntaxTextArea);

        //Spell Checking Module
        File zip = new File("./src/main/resources/dico/english_dic.zip");
        boolean usEnglish = true; // "false" will use British English
        try {
            SpellingParser parser = SpellingParser.createEnglishSpellingParser(zip, usEnglish);
            rSyntaxTextArea.addParser(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Language Support
        LanguageSupportFactory lsf = LanguageSupportFactory.get();
        LanguageSupport support = lsf.getSupportFor(SYNTAX_STYLE_JAVA);
        LanguageSupportFactory.get().register(rSyntaxTextArea);
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
        mNew.setForeground(Color.WHITE);
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
        mEdit.setForeground(Color.WHITE);

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
        b.setBackground(Color.getColor("GRIS_MIDDLE"));
        b.setForeground(Color.getColor("GRIS_MIDDLE"));
        b.setBorder(BorderFactory.createLineBorder(Color.getColor("GRIS_MIDDLE"), 7));
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

        jToolBar.add(createToolBarButton(new IdeAction.actSave(this, rSyntaxTextArea)));
        jToolBar.addSeparator();
        jToolBar.add(createToolBarButton(new IdeAction.actUndo(this)));
        jToolBar.add(createToolBarButton(new IdeAction.actRedo(this)));
        jToolBar.add(createToolBarButton(new IdeAction.actCopy(this, rSyntaxTextArea)));
        jToolBar.add(createToolBarButton(new IdeAction.actCut(this, rSyntaxTextArea)));
        jToolBar.add(createToolBarButton(new IdeAction.actPaste(this, rSyntaxTextArea)));

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

    private void createPopupMenu() {
        JPopupMenu textPopupMenu = new JPopupMenu();
        JPopupMenu treePopupMenu = new JPopupMenu();
        textPopupMenu.add(new IdeAction.actCopy(this, rSyntaxTextArea));
        textPopupMenu.add(new IdeAction.actCut(this, rSyntaxTextArea));
        textPopupMenu.add(new IdeAction.actPaste(this, rSyntaxTextArea));

        rSyntaxTextArea.setPopupMenu(textPopupMenu);


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
