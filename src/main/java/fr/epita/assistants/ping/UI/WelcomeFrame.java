package fr.epita.assistants.ping.UI;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.UI.Action.IdeAction;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WelcomeFrame extends JFrame {

    private MainFrame mainFrame;

    public WelcomeFrame(String title, ProjectService projectService) throws IOException {
        super(title);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame = new MainFrame("PingIDE", projectService);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        JPanel panel = new JPanel();
        panel.setBackground(Color.getColor("GRIS_MIDDLE"));
        panel.setBorder(BorderFactory.createEmptyBorder());
        JPanel imgpanel = new JPanel();
        imgpanel.setBackground(Color.getColor("GRIS_MIDDLE"));
        imgpanel.setBorder(BorderFactory.createEmptyBorder());

        JLabel imgLabel = new JLabel(new ImageIcon("src/main/resources/images/welcomeframe.png"));
        imgLabel.setBorder(BorderFactory.createEmptyBorder());
        imgpanel.add(imgLabel);

        JButton newProjectButton = new JButton("New Project");
        newProjectButton.addActionListener(new IdeAction.actNewProject(mainFrame));
        newProjectButton.setBackground(Color.getColor("GRIS_MIDDLE"));
        newProjectButton.setForeground(Color.getColor("GRIS_CLAIR"));
        Dimension buttonDim = new Dimension();
        buttonDim.height = 50;
        buttonDim.width = 250;
        newProjectButton.setPreferredSize(buttonDim);

        JButton openProjectButton = new JButton("Open Project");
        openProjectButton.addActionListener(new IdeAction.actOpenProject(mainFrame));
        openProjectButton.setBackground(Color.getColor("GRIS_MIDDLE"));
        openProjectButton.setForeground(Color.getColor("GRIS_CLAIR"));
        openProjectButton.setPreferredSize(buttonDim);

        newProjectButton.setBorder(BorderFactory.createRaisedBevelBorder());
        openProjectButton.setBorder(BorderFactory.createRaisedBevelBorder());
        panel.add(newProjectButton);
        panel.add(openProjectButton);

        this.add(imgpanel, BorderLayout.NORTH);
        this.add(panel);
        this.setBackground(Color.getColor("GRIS_MIDDLE"));
        this.setResizable(false);


        this.setSize(dim.width / 4, dim.height / 4);
        this.pack();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setVisible(true);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        while (!mainFrame.isActive()){
        }
        this.setVisible(false);
        this.dispose();
    }

    public static void main(String[] args) {
        ProjectService projectService = MyIde.init(null);
        try {
            JFrame frame = new WelcomeFrame("Welcome", projectService);
        }
        catch (Exception e)
        {}
    }
}
