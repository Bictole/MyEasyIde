package fr.epita.assistants.ping.UI.Panel;

import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.UI.MainFrame;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class ExecConfig {

    private String mainClass = null;

    private String mainFile;
    private String mainParentPath;
    private String mainPackagePath;

    private String args = "";

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

    public String getArgs() {
        return args;
    }

    private String PathTreatment(Path p) {
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

    private void MavenMainClass(MainFrame mainFrame, List<Path> filesMatch) {

        if (filesMatch.size() == 1) {
            String elt = PathTreatment(filesMatch.get(0));
            if (elt == null)
                JOptionPane.showMessageDialog(mainFrame.jFrame,
                        "No main class found in src" + File.separator + "main" + File.separator + "java",
                        "Error main class", JOptionPane.ERROR_MESSAGE);
            this.mainClass = elt;
            return;
        }

        JPanel p = new JPanel();

        p.setBackground(Color.getColor("GRIS_MIDDLE"));

        Map<String, Path> map = new HashMap<>();

        Set<Path> to_Exec = new HashSet<Path>();
        to_Exec.addAll(filesMatch);

        final Path[] selectList = {null};

        JLabel mainClassLabel = new JLabel("Main class:");
        mainClassLabel.setForeground(Color.getColor("ROSE"));
        JTextField mainClassField = new JTextField();
        mainClassField.setBackground(Color.getColor("GRIS_SOMBRE"));
        mainClassField.setForeground(Color.getColor("BLEU_ELECTRIQUE"));
        mainClassField.setColumns(50);
        JLabel argsLabel = new JLabel("Arguments:");
        argsLabel.setForeground(Color.getColor("ROSE"));
        JTextField argsField = new JTextField();
        argsField.setBackground(Color.getColor("GRIS_SOMBRE"));
        argsField.setForeground(Color.getColor("BLEU_ELECTRIQUE"));
        argsField.setColumns(50);

        List<String> to_ExecName = new ArrayList<>();
        for (var e : to_Exec) {
            String name = FilenameUtils.removeExtension(e.toFile().getName());
            to_ExecName.add(name);
            map.put(name, e);
        }

        JList jList = new JList(to_ExecName.toArray());
        jList.setBackground(Color.getColor("GRIS_SOMBRE"));
        jList.setForeground(Color.getColor("ROSE"));
        jList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Path p = map.get(jList.getSelectedValue());
                selectList[0] = p;
                mainClassField.setText(PathTreatment(p));
            }
        });
        jList.setSelectedIndex(0);

        GroupLayout layout = new GroupLayout(p);
        p.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jList))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainClassLabel)
                        .addComponent(argsLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainClassField)
                        .addComponent(argsField))
        );

        layout.linkSize(SwingConstants.VERTICAL, argsField, mainClassField);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jList))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, to_ExecName.size() * 8 / 2, to_ExecName.size() * 8 / 2)
                                .addComponent(mainClassLabel)
                                .addComponent(argsLabel))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, to_ExecName.size() * 8 / 2, to_ExecName.size() * 8 / 2)
                                .addComponent(mainClassField)
                                .addComponent(argsField)))
        );
        int output = JOptionPane.showConfirmDialog(mainFrame.jFrame, p, "Select the main class you want to exec", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (output == 0){
            if (mainClassField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class given", "Error main class", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mainClass = PathTreatment(selectList[0]);
            args = argsField.getText();
        }
    }

    private void AnyExecMainClass(MainFrame mainFrame, List<Path> filesMatch) {
        String toRemove = "src" + File.separator + "main" + File.separator + "java" + File.separator;

        if (filesMatch.size() == 1) {
            Path p = filesMatch.get(0);
            String elt = PathTreatment(p);
            if (elt == null)
                JOptionPane.showMessageDialog(mainFrame.jFrame,
                        "No main class found in src" + File.separator + "main" + File.separator + "java",
                        "Error main class", JOptionPane.ERROR_MESSAGE);

            mainClass = elt;
            mainFile = p.toFile().getName();
            mainParentPath = p.toFile().getParent();
            mainPackagePath = p.toFile().getPath().substring(0, p.toFile().getPath().indexOf(toRemove) + toRemove.length());

            return;
        }

        JPanel p = new JPanel();

        p.setBackground(Color.getColor("GRIS_MIDDLE"));

        Map<String, Path> map = new HashMap<>();

        Set<Path> to_Exec = new HashSet<Path>();
        to_Exec.addAll(filesMatch);

        final Path[] selectList = {null};

        JLabel mainClassLabel = new JLabel("Main class:");
        mainClassLabel.setForeground(Color.getColor("ROSE"));
        JTextField mainClassField = new JTextField();
        mainClassField.setBackground(Color.getColor("GRIS_SOMBRE"));
        mainClassField.setForeground(Color.getColor("BLEU_ELECTRIQUE"));
        mainClassField.setColumns(50);
        JLabel argsLabel = new JLabel("Arguments:");
        argsLabel.setForeground(Color.getColor("ROSE"));
        JTextField argsField = new JTextField();
        argsField.setBackground(Color.getColor("GRIS_SOMBRE"));
        argsField.setForeground(Color.getColor("BLEU_ELECTRIQUE"));
        argsField.setColumns(50);

        List<String> to_ExecName = new ArrayList<>();
        for (var e : to_Exec) {
            String name = FilenameUtils.removeExtension(e.toFile().getName());
            to_ExecName.add(name);
            map.put(name, e);
        }

        JList jList = new JList(to_ExecName.toArray());
        jList.setBackground(Color.getColor("GRIS_SOMBRE"));
        jList.setForeground(Color.getColor("ROSE"));
        jList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Path p = map.get(jList.getSelectedValue());
                selectList[0] = p;
                mainClassField.setText(PathTreatment(p));
            }
        });
        jList.setSelectedIndex(0);

        GroupLayout layout = new GroupLayout(p);
        p.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jList))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainClassLabel)
                        .addComponent(argsLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mainClassField)
                        .addComponent(argsField))
        );

        layout.linkSize(SwingConstants.VERTICAL, argsField, mainClassField);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jList))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, to_ExecName.size() * 8 / 2, to_ExecName.size() * 8 / 2)
                                .addComponent(mainClassLabel)
                                .addComponent(argsLabel))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, to_ExecName.size() * 8 / 2, to_ExecName.size() * 8 / 2)
                                .addComponent(mainClassField)
                                .addComponent(argsField)))
        );
        int output = JOptionPane.showConfirmDialog(mainFrame.jFrame, p, "Select the main class you want to exec", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (output == 0){
            if (mainClassField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class given", "Error main class", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Path path = selectList[0];
            mainClass = mainClassField.getText();
            mainFile = path.toFile().getName();
            mainParentPath = path.toFile().getParent();
            mainPackagePath = path.toFile().getPath().substring(0, path.toFile().getPath().indexOf(toRemove) + toRemove.length());
            args = argsField.getText();
        }
    }

    public ExecConfig(MainFrame mainFrame, List<Path> filesMatch) {

        if (!filesMatch.isEmpty()) {
            UIManager ui = new UIManager();
            Color OpBack = (Color) UIManager.get("OptionPane.background");
            Color PBack = (Color) UIManager.get("Panel.background");
            Color PFront = (Color) UIManager.get("OptionPane.messageForeground");
            ui.put("OptionPane.background", Color.getColor("GRIS_MIDDLE"));
            ui.put("OptionPane.messageForeground", Color.getColor("ROSE"));
            ui.put("Panel.background", Color.getColor("GRIS_MIDDLE"));
            try {
                if (mainFrame.project.getAspects().stream().anyMatch(aspect -> aspect.getType() == Mandatory.Aspects.MAVEN))
                    MavenMainClass(mainFrame, filesMatch);
                else if (mainFrame.project.getAspects().stream().anyMatch(aspect -> aspect.getType() == Mandatory.Aspects.ANY))
                    AnyExecMainClass(mainFrame, filesMatch);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ui.put("OptionPane.background", OpBack);
            ui.put("Panel.background", PBack);
            ui.put("OptionPane.messageForeground", PFront);

        } else
            JOptionPane.showMessageDialog(mainFrame.jFrame, "No main class found", "Error main class", JOptionPane.ERROR_MESSAGE);


    }
}
