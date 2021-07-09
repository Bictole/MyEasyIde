package fr.epita.assistants.ping.UI;

import fr.epita.assistants.ping.UI.Action.IdeAction;
import fr.epita.assistants.ping.UI.Action.TreeAction;
import fr.epita.assistants.ping.UI.Panel.Tab;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.print.attribute.standard.MediaSize;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import javax.swing.plaf.multi.MultiTabbedPaneUI;
import javax.swing.plaf.synth.SynthTabbedPaneUI;
import javax.swing.text.LayeredHighlighter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TabManager {

    private Tab currentFile;
    private RSyntaxTextArea currentTextArea;
    private List<String> openedFiles;
    private List<Tab> openedTabs;
    private String theme;
    private MainFrame mainFrame;

    public JTabbedPane tabPane;

    public TabManager(String Theme, MainFrame frame)
    {
        currentFile = null;
        currentTextArea = null;
        openedTabs = new ArrayList<>();
        openedFiles = new ArrayList<>();
        theme = Theme;
        mainFrame = frame;

        this.tabPane = new JTabbedPane();
        this.tabPane.setBorder(BorderFactory.createEmptyBorder());
        this.tabPane.setUI(new PaneUI());
        this.tabPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabPane.getSelectedComponent() != currentTextArea)
                {
                    var i = tabPane.getSelectedIndex();
                    if (i >= 0) {
                        currentFile = openedTabs.get(i);
                        currentTextArea = currentFile.getrSyntaxTextArea();
                    }
                }
            }
        });
    }

    public void addPane()
    {
        tabPane.addTab(currentFile.getFileName(), null, currentFile.getTextView(),
                currentFile.getFile().getPath());
        var button = new Tab.ButtonTabComponent(this);
        tabPane.setTabComponentAt(openedTabs.size()-1, button);
        tabPane.setBackgroundAt(openedTabs.size()-1, Color.getColor("GRIS_MIDDLE"));
        tabPane.setSelectedComponent(currentFile.getTextView());
        MainFrame.createTextPopupMenu(mainFrame, currentTextArea);
    }

    public Tab OpenFile(File openedFile)
    {
        if (!openedFiles.contains(openedFile.getName())) {
            openedFiles.add(openedFile.getName());
            Tab newOpenedFile = new Tab(openedFile, theme);
            openedTabs.add(newOpenedFile);
            setCurrentFile(newOpenedFile);
            newOpenedFile.getText();
            addPane();

            return currentFile;
        }
        else
        {
            var i = openedFiles.indexOf(openedFile.getName());
            setCurrentFile(openedTabs.get(i));
            tabPane.setSelectedComponent(currentFile.getTextView());
            return currentFile;
        }
    }

    public void CloseFile(int i)
    {
        Tab toClose = openedTabs.get(i);
        openedFiles.remove(i);
        openedTabs.remove(i);
        //tabPane.remove(i);
        if (openedFiles.size() > 0 && currentFile == toClose)
        {
            var j = tabPane.getSelectedIndex();
            setCurrentFile(openedTabs.get(j));
        }
    }

    public void setCurrentFile(Tab currentFile) {
        this.currentFile = currentFile;
        this.currentTextArea = currentFile.getrSyntaxTextArea();
    }

    public UndoManager getUndoManager()
    {
        if (currentFile == null)
            return null;
        return currentFile.getUndoManager();
    }

    public RSyntaxTextArea getCurrentTextArea() {
        return currentTextArea;
    }

    public Tab getCurrentFile() {
        return currentFile;
    }

    public static class PaneUI extends BasicTabbedPaneUI {

        private FontMetrics boldFontMetrics;
        private Font boldFont;

        protected void paintTabBackground(Graphics g, int tabPlacement,
                                          int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Rectangle rect = new Rectangle();
            g.setColor(Color.GRAY);
            g.fillRect(x, y, w, h);
            if(isSelected) {
                g.setColor(Color.getColor("GRIS_MIDDLE"));
                g.fillRect(x, y, w, h);
            }
        }

        protected void paintTabBorder(Graphics g, int tabPlacement,
                                      int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Rectangle rect = getTabBounds(tabIndex, new Rectangle(x, y, w, h));
            g.setColor(Color.getColor("GRIS_CLAIR"));
            g.drawRect(x, y, w, h);
            if(isSelected) {
                g.setColor(Color.getColor("BLEU_ELECTRIQUE"));
                g.drawRect(x, y, w, h);
            }
        }

        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[]
                rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        }

        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            int vHeight = fontHeight;
            if (vHeight % 2 > 0)
                vHeight += 2;
            return vHeight + 8;
        }

        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics
                metrics){
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) +
                    metrics.getHeight()+15;
        }

        protected int getTabLabelShiftY(int tabPlacement,int tabIndex,boolean isSelected) {
            return 0;
        }

        protected Insets getContentBorderInsets(int tabPlacement) {
            return new Insets(0,0,0,0);
        }


        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets.left = 0;
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
            tabInsets = selectedTabPadInsets;
            boldFont = tabPane.getFont().deriveFont(Font.BOLD);
            boldFontMetrics = tabPane.getFontMetrics(boldFont);
        }

        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics
                metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            if (isSelected) {
                int vDifference = (int)(boldFontMetrics.getStringBounds(title,g).getWidth())
                        - textRect.width;
                textRect.x -= (vDifference / 2);
                super.paintText(g, tabPlacement, boldFont, boldFontMetrics, tabIndex,
                        title, textRect, isSelected);
            }
            else
                super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect,
                        isSelected);
        }
    }
}
