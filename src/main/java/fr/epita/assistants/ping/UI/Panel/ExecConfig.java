package fr.epita.assistants.ping.UI.Panel;

import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.UI.MainFrame;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExecConfig {

    private String mainClass;

    private String mainFile;
    private String mainParentPath;
    private String mainPackagePath;

    public String getMainClass() {
        return mainClass;
    }

    public String getMainFile() {
        return mainFile;
    }

    public String getMainParentPath() {
        return mainParentPath;
    }

    public String getMainPackagePath() {
        return mainPackagePath;
    }

    private void MavenMainClass(MainFrame mainFrame, List<Path> filesMatch){

        if (filesMatch.size() == 1)
        {
            String path = filesMatch.get(0).toString();
            String toRemove = "src" + File.separator + "main" + File.separator + "java" + File.separator;
            if (!path.contains(toRemove))
                JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class found in src/main/java", "Error main class", JOptionPane.ERROR_MESSAGE);
            String elt = path.substring(path.lastIndexOf(toRemove));
            StringBuilder builder = new StringBuilder(elt);
            builder.delete(0, toRemove.length());
            elt = builder.toString();
            elt = FilenameUtils.removeExtension(elt);
            elt = elt.replace(File.separator, ".");
            this.mainClass = elt;
            return;
        }

        Set<Path> to_Exec = new HashSet<Path>();
        to_Exec.addAll(filesMatch);

        final String[] mainClass = {new String()};

        Box pbox = Box.createVerticalBox();
        JLabel label = new JLabel("Choose main class");
        ButtonGroup buttonGroup = new ButtonGroup();
        pbox.add(label);
        for (var p : to_Exec) {
            String path = p.toString();
            String toRemove = "src" + File.separator + "main" + File.separator + "java" + File.separator;
            if (!path.contains(toRemove))
                continue;
            String elt = path.substring(path.lastIndexOf(toRemove));
            StringBuilder builder = new StringBuilder(elt);
            builder.delete(0, toRemove.length());
            elt = builder.toString();
            elt = FilenameUtils.removeExtension(elt);
            elt = elt.replace(File.separator, ".");
            JRadioButton radioButton = new JRadioButton(elt);
            radioButton.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (radioButton.isSelected())
                        mainClass[0] = radioButton.getText();

                }
            });
            buttonGroup.add(radioButton);
            pbox.add(radioButton);
        }
        JOptionPane.showConfirmDialog(mainFrame.jFrame, pbox, "Select the main class you want to exec", JOptionPane.OK_CANCEL_OPTION);
        this.mainClass = mainClass[0];
    }

    private void AnyExecMainClass(MainFrame mainFrame, List<Path> filesMatch){

        if (filesMatch.size() == 1)
        {
            String path = filesMatch.get(0).toString();
            String toRemove = "src" + File.separator + "main" + File.separator + "java" + File.separator;
            if (!path.contains(toRemove))
                JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class found in src/main/java", "Error main class", JOptionPane.ERROR_MESSAGE);
            String elt = path.substring(path.lastIndexOf(toRemove));
            StringBuilder builder = new StringBuilder(elt);
            builder.delete(0, toRemove.length());
            elt = builder.toString();
            elt = FilenameUtils.removeExtension(elt);
            elt = elt.replace(File.separator, ".");

            Path p = filesMatch.get(0);
            mainClass = elt;
            mainFile = p.toFile().getName();
            mainParentPath = p.toFile().getParent();
            mainPackagePath = p.toFile().getPath().substring(0, p.toFile().getPath().indexOf(toRemove) + toRemove.length());

            return;
        }

        Set<Path> to_Exec = new HashSet<Path>();
        to_Exec.addAll(filesMatch);


        Box pbox = Box.createVerticalBox();
        JLabel label = new JLabel("Choose main class");
        ButtonGroup buttonGroup = new ButtonGroup();
        pbox.add(label);
        for (var p : to_Exec) {
            String path = p.toString();
            String toRemove = "src" + File.separator + "main" + File.separator + "java" + File.separator;
            if (!path.contains(toRemove))
                continue;
            String elt = path.substring(path.lastIndexOf(toRemove));
            StringBuilder builder = new StringBuilder(elt);
            builder.delete(0, toRemove.length());
            elt = builder.toString();
            elt = FilenameUtils.removeExtension(elt);
            elt = elt.replace(File.separator, ".");
            JRadioButton radioButton = new JRadioButton(elt);
            radioButton.addItemListener(new ItemListener() {
                private Path path = p;
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (radioButton.isSelected()) {
                        mainClass = radioButton.getText();
                        mainFile = path.toFile().getName();
                        mainParentPath = path.toFile().getParent();
                        mainPackagePath = path.toFile().getPath().substring(0, path.toFile().getPath().indexOf(toRemove) + toRemove.length());
                    }

                }
            });
            buttonGroup.add(radioButton);
            pbox.add(radioButton);
        }
        JOptionPane.showConfirmDialog(mainFrame.jFrame, pbox, "Select the main class you want to exec", JOptionPane.OK_CANCEL_OPTION);
    }

    public ExecConfig(MainFrame mainFrame, List<Path> filesMatch) {

        if (!filesMatch.isEmpty()) {
            try {
                if (mainFrame.project.getAspects().stream().anyMatch(aspect -> aspect.getType()== Mandatory.Aspects.MAVEN))
                        MavenMainClass(mainFrame, filesMatch);
                else if (mainFrame.project.getAspects().stream().anyMatch(aspect -> aspect.getType()== Mandatory.Aspects.ANY))
                    AnyExecMainClass(mainFrame, filesMatch);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class found", "Error main class", JOptionPane.ERROR_MESSAGE);
    }
}
