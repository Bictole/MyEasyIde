package fr.epita.assistants.ping.UI;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.ping.UI.Action.IdeAction;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;

public class WelcomeFrame extends JFrame {

    private MainFrame mainFrame;

    public WelcomeFrame(String title, ProjectService projectService) {
        super(title);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame = new MainFrame("PingIDE", projectService);

        JPanel panel = new JPanel();
        JButton newProjectButton = new JButton("New Project");
        newProjectButton.addActionListener(new IdeAction.actNewProject(mainFrame));

        JButton openProjectButton = new JButton("Open Project");
        openProjectButton.addActionListener(new IdeAction.actOpenProject(mainFrame));

        panel.add(newProjectButton);
        panel.add(openProjectButton);

        this.add(panel);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
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
        JFrame frame = new WelcomeFrame("Welcome", projectService);
    }
}
