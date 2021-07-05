package fr.epita.assistants.ping.UI;

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

    public JTabbedPane tabPane;

    public TabManager(String Theme)
    {
        currentFile = null;
        currentTextArea = null;
        openedTabs = new ArrayList<>();
        openedFiles = new ArrayList<>();
        theme = Theme;

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
                    System.out.println(currentFile.getFileName());
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
        return currentFile.getUndoManager();
    }

    public RSyntaxTextArea getCurrentTextArea() {
        return currentTextArea;
    }

    public Tab getCurrentFile() {
        return currentFile;
    }

    public static class PaneUI extends BasicTabbedPaneUI {

        @Override
        protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects,
                                int tabIndex, Rectangle iconRect, Rectangle textRect)
        {
            g.setColor(Color.getColor("GRIS_MIDDLE"));
            g.fillRect(rects[tabIndex].x, rects[tabIndex].y,
                    rects[tabIndex].width, rects[tabIndex].height);
            g.setColor(Color.getColor("BLEU_ELECTRIQUE"));
            g.drawRect(rects[tabIndex].x, rects[tabIndex].y,
                    rects[tabIndex].width, rects[tabIndex].height);
        }

    }

}
