package fr.epita.assistants.ping.UI;

import fr.epita.assistants.ping.UI.Panel.Tab;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.print.attribute.standard.MediaSize;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
        this.tabPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabPane.getSelectedComponent() != currentTextArea)
                {
                    var selected = tabPane.getSelectedComponent();
                    for (var t : openedTabs)
                    {
                        if (t.getTextView() == selected)
                        {
                            currentFile = t;
                            currentTextArea = t.getrSyntaxTextArea();
                            System.out.println(currentFile.getFileName());
                            return;
                        }
                    }
                }
            }
        });
        tabPane.setBackground(Color.getColor("GRIS_MIDDLE"));
        tabPane.setBorder(BorderFactory.createRaisedBevelBorder());
        tabPane.setForeground(Color.getColor("ROSE"));

    }

    public void addPane()
    {
        tabPane.addTab(currentFile.getFileName(), null, currentFile.getTextView(),
                currentFile.getFile().getPath());
        tabPane.setTabComponentAt(openedTabs.size()-1, new Tab.ButtonTabComponent(this));
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
