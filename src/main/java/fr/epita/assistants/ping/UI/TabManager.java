package fr.epita.assistants.ping.UI;

import fr.epita.assistants.ping.UI.Panel.Tab;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.print.attribute.standard.MediaSize;
import javax.swing.*;
import javax.swing.text.LayeredHighlighter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.epita.assistants.ping.UI.Panel.Graphics;

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
        tabPane.setBackground(Color.getColor("GRIS_MIDDLE"));
        tabPane.setBorder(BorderFactory.createRaisedBevelBorder());
        tabPane.setForeground(Color.getColor("ROSE"));
    }

    public void addPane()
    {
        JScrollPane textView = new JScrollPane(currentFile);
        Graphics.ScrollPaneDesign(textView, Color.getColor("GRIS_MIDDLE"));
        tabPane.addTab(currentFile.getFileName(), null, textView,
                currentFile.getFile().getPath());
        tabPane.setSelectedComponent(textView);
    }

    public Tab OpenFile(File openedFile)
    {
        if (!openedFiles.contains(openedFile.getName())) {
            openedFiles.add(openedFile.getName());
            Tab newOpenedFile = new Tab(openedFile, theme);
            openedTabs.add(newOpenedFile);
            setCurrentFile(newOpenedFile);

            addPane();
            return currentFile;
        }
        else
        {
            var i = openedFiles.indexOf(openedFile.getName());
            setCurrentFile(openedTabs.get(i));
            tabPane.setSelectedComponent(currentFile.getrSyntaxTextArea());
            return currentFile;
        }
    }

    public void CloseFile(Tab closedFile)
    {
        openedFiles.remove(closedFile.getFileName());
        openedTabs.remove(closedFile);

        if (openedFiles.size() > 0 && currentFile == closedFile)
        {
            currentFile = openedTabs.get(0);
            currentTextArea = currentFile.getrSyntaxTextArea();
        }
    }

    public void setCurrentFile(Tab currentFile) {
        this.currentFile = currentFile;
        this.currentTextArea = currentFile.getrSyntaxTextArea();
    }

    public RSyntaxTextArea getCurrentTextArea() {
        return currentTextArea;
    }

    public Tab getCurrentFile() {
        return currentFile;
    }
}
