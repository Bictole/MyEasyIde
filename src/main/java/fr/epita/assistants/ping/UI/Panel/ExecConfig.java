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

    private String mainClass = null;

    private String mainFile;
    private String mainParentPath;
    private String mainPackagePath;

    public Boolean isSuccess;

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

    private String PathTreatment(Path p)
    {
        String path = p.toString();
        String toRemove = "src" + File.separator + "main" + File.separator + "java" + File.separator;
        if (!path.contains(toRemove))
            return null;
        String elt = path.substring(path.lastIndexOf(toRemove));
        StringBuilder builder = new StringBuilder(elt);
        builder.delete(0, toRemove.length());
        elt = builder.toString();
        elt = FilenameUtils.removeExtension(elt);
        elt = elt.replace(File.separator, ".");
        return elt;
    }

    private void MavenMainClass(MainFrame mainFrame, List<Path> filesMatch){

        if (filesMatch.size() == 1)
        {
            String elt = PathTreatment(filesMatch.get(0));
            if (elt == null)
                JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class found in src/main/java", "Error main class", JOptionPane.ERROR_MESSAGE);
            this.mainClass = elt;
            this.isSuccess = true;
            return;
        }

        Set<Path> to_Exec = new HashSet<Path>();
        to_Exec.addAll(filesMatch);

        final String[] mainClass = {new String()};

        Box pbox = Box.createVerticalBox();
        JLabel label = new JLabel("Choose main class");
        ButtonGroup buttonGroup = new ButtonGroup();
        pbox.add(label);
        Boolean selectDefault = false;
        for (var p : to_Exec) {
            String elt = PathTreatment(p);
            if (elt == null)
                continue;

            JRadioButton radioButton = new JRadioButton(elt);
            if (!selectDefault)
            {
                radioButton.setSelected(true);
                selectDefault = true;
                this.mainClass = elt;
            }
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
        int output = JOptionPane.showConfirmDialog(mainFrame.jFrame, pbox, "Select the main class you want to exec", JOptionPane.OK_CANCEL_OPTION);
        this.isSuccess = output == 0;
        if(!mainClass[0].isEmpty())
            this.mainClass = mainClass[0];
    }

    private void AnyExecMainClass(MainFrame mainFrame, List<Path> filesMatch){

        if (filesMatch.size() == 1)
        {
            Path p = filesMatch.get(0);
            String elt = PathTreatment(p);
            String toRemove = "src" + File.separator + "main" + File.separator + "java" + File.separator;
            if (elt == null)
                JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class found in src/main/java", "Error main class", JOptionPane.ERROR_MESSAGE);

            mainClass = elt;
            mainFile = p.toFile().getName();
            mainParentPath = p.toFile().getParent();
            mainPackagePath = p.toFile().getPath().substring(0, p.toFile().getPath().indexOf(toRemove) + toRemove.length());
            this.isSuccess = true;

            return;
        }

        Set<Path> to_Exec = new HashSet<Path>();
        to_Exec.addAll(filesMatch);


        Box pbox = Box.createVerticalBox();
        JLabel label = new JLabel("Choose main class");
        ButtonGroup buttonGroup = new ButtonGroup();
        pbox.add(label);
        boolean selectDefault = false;
        for (var p : to_Exec) {

            String elt = PathTreatment(p);
            String toRemove = "src" + File.separator + "main" + File.separator + "java" + File.separator;
            if (elt == null)
                continue;

            JRadioButton radioButton = new JRadioButton(elt);
            //Default RadioButton Selected
            if (!selectDefault)
            {
                radioButton.setSelected(true);
                selectDefault = true;
                mainClass = elt;
                mainFile = p.toFile().getName();
                mainParentPath = p.toFile().getParent();
                mainPackagePath = p.toFile().getPath().substring(0, p.toFile().getPath().indexOf(toRemove) + toRemove.length());
            }
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
        int output = JOptionPane.showConfirmDialog(mainFrame.jFrame, pbox, "Select the main class you want to exec", JOptionPane.OK_CANCEL_OPTION);
        this.isSuccess = output == 0;
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
                //e.printStackTrace();
            }
        }
        else
            JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class found", "Error main class", JOptionPane.ERROR_MESSAGE);

    }
}
