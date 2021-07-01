package fr.epita.assistants.ping.UI.Panel;

import fr.epita.assistants.ping.UI.Icons;
import fr.epita.assistants.ping.UI.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class NewProject {

    public NewProject(MainFrame mainFrame) {
        JFrame f = new JFrame("New Project");
        JLabel nameLabel = new JLabel("Name:");

        JTextField nameTextField = new JTextField("undefined");

        JLabel locationLabel = new JLabel("Location:");
        JTextField locationTextField = new JTextField();
        JButton locationButton = new JButton(MainFrame.resizeIcon(new ImageIcon(Icons.OPEN_PROJECT.path), 16, 16));
        locationButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String home = System.getProperty("user.home");
                JFileChooser j = new JFileChooser(Path.of(home).toFile());
                j.setDialogTitle("Choose project location");
                j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                j.setAcceptAllFileFilterUsed(false);
                // Invoke the showsOpenDialog function to show the save dialog
                int r = j.showOpenDialog(null);

                // If the user selects a file
                if (r == JFileChooser.APPROVE_OPTION) {

                    File fi = new File(j.getSelectedFile().getAbsolutePath());
                    locationTextField.setText(fi.getAbsolutePath());
                }
            }
        });

        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(f, "Enter a project name", "Error name", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (locationTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(f, "Enter a project location", "Error location", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    File tmp = Path.of(locationTextField.getText()).resolve(nameTextField.getText()).toFile();
                    if (tmp.exists()) {
                        if (JOptionPane.showConfirmDialog(f,
                                "Project already exists at this location.\nDo you want to open it ?",
                                "Already exist project", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            mainFrame.loadProjectFrame(tmp.toPath());
                            f.dispose();
                        }
                        return;
                    }
                    if (JOptionPane.showConfirmDialog(f,
                            "No project exists at this location.\nDo you want to create it ?",
                            "Confirm project creation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        Files.createDirectories(tmp.toPath());
                        mainFrame.loadProjectFrame(tmp.toPath());
                        f.dispose();
                    }
                    return;

                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        });
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                f.dispose();
                return;
            }
        });

        GroupLayout layout = new GroupLayout(f.getContentPane());

        f.getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);

        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nameLabel)
                        .addComponent(locationLabel))

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nameTextField)
                        .addComponent(locationTextField)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(btnOK))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(btnCancel))))

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(locationButton))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, btnOK, btnCancel);

        layout.setVerticalGroup(layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nameLabel)
                        .addComponent(nameTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(locationLabel)
                        .addComponent(locationTextField)
                        .addComponent(locationButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOK)
                        .addComponent(btnCancel))
        );

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        f.setSize(dim.width / 4, dim.height / 8);
        f.pack();
        f.setLocation(dim.width / 2 - f.getSize().width / 2, dim.height / 2 - f.getSize().height / 2);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
