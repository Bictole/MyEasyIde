package fr.epita.assistants.ping.UI.Panel;

import fr.epita.assistants.ping.UI.TabManager;
import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class Tab {

    private RSyntaxTextArea rSyntaxTextArea;
    private String fileName;
    private File file;
    private UndoManager undoManager;
    private JScrollPane textView;

    public Tab(File file, String theme)
    {
        this.file = file;
        if (file != null)
            this.fileName = file.getName();
        this.rSyntaxTextArea = new RSyntaxTextArea();
        rSyntaxTextArea.setEditable(false);
        rSyntaxTextArea.setBackground(Color.getColor("GRIS_MIDDLE"));
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
        loadTheme(theme);

        // Autocompletion Module
        CompletionProvider provider = createCompletionProvider();
        AutoCompletion ac = new AutoCompletion(provider);
        ac.install(rSyntaxTextArea);

        //Spell Checking Module
        initSpellModule();

        // Language Support
        LanguageSupportFactory lsf = LanguageSupportFactory.get();
        LanguageSupport support = lsf.getSupportFor(SyntaxConstants.SYNTAX_STYLE_JAVA);
        LanguageSupportFactory.get().register(rSyntaxTextArea);

        textView = new JScrollPane(rSyntaxTextArea);
        Graphics.ScrollPaneDesign(textView, Color.getColor("GRIS_MIDDLE"));
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

    private void loadTheme(String t)
    {
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(
                    "/themes/" + t + ".xml"));
            theme.apply(rSyntaxTextArea);
        } catch (IOException ioe) { // Never happens
            ioe.printStackTrace();
        }
    }

    private void initSpellModule()
    {
        File zip = new File("./src/main/resources/dico/english_dic.zip");
        boolean usEnglish = true; // "false" will use British English
        try {
            SpellingParser parser = SpellingParser.createEnglishSpellingParser(zip, usEnglish);
            rSyntaxTextArea.addParser(parser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RSyntaxTextArea getrSyntaxTextArea() {
        return rSyntaxTextArea;
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return file;
    }

    public void getText()
    {
        try {
            String text = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
            // Set the text
            rSyntaxTextArea.setText(text);
            rSyntaxTextArea.setEditable(true);
        }
        catch (Exception e)
        {}
    }

    public JScrollPane getTextView() {
        return textView;
    }

    /**
     * Component to be used as tabComponent;
     * Contains a JLabel to show the text and
     * a JButton to close the tab it belongs to
     */
    public static class ButtonTabComponent extends JPanel {
        private final JTabbedPane pane;
        private final TabManager tabManager;

        public ButtonTabComponent(final TabManager tabManager) {
            //unset default FlowLayout' gaps
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            if (tabManager.tabPane== null) {
                throw new NullPointerException("TabbedPane is null");
            }
            this.pane = tabManager.tabPane;
            this.tabManager = tabManager;
            setOpaque(false);

            //make JLabel read titles from JTabbedPane
            JLabel label = new JLabel() {
                public String getText() {
                    int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                    if (i != -1) {
                        return pane.getTitleAt(i);
                    }
                    return null;
                }
            };

            add(label);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            JButton button = new TabButton();
            add(button);
            setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        }

        private class TabButton extends JButton implements ActionListener {
            public TabButton() {
                int size = 17;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("close");

                setUI(new BasicButtonUI());
                setContentAreaFilled(false);
                setFocusable(false);
                setBorder(BorderFactory.createEtchedBorder());
                setBorderPainted(false);

                addMouseListener(buttonMouseListener);
                setRolloverEnabled(true);

                addActionListener(this);
            }

            public void actionPerformed(ActionEvent e) {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1)
                {
                    var toRemove = pane.getComponent(i);
                    tabManager.CloseFile(toRemove);
                    pane.remove(i);
                }
            }

            //paint the cross
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                //shift the image for pressed buttons
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.getColor("GRIS_MIDDLE"));
                if (getModel().isRollover()) {
                    g2.setColor(Color.getColor("ROSE"));
                }
                int delta = 6;
                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                g2.dispose();
            }
        }

        private final static MouseListener buttonMouseListener = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                Component component = e.getComponent();
                if (component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;
                    button.setBorderPainted(true);
                }
            }

            public void mouseExited(MouseEvent e) {
                Component component = e.getComponent();
                if (component instanceof AbstractButton) {
                    AbstractButton button = (AbstractButton) component;
                    button.setBorderPainted(false);
                }
            }
        };
    }
}


