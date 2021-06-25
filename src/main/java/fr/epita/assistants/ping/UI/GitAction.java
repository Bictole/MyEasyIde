package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.project.AnyProject;
import org.eclipse.jgit.api.Status;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class GitAction {

    public static class actGitCommit extends AbstractAction
    {
        MainFrame mainFrame;

        public actGitCommit(MainFrame frame)
        {
            super("Git Commit");
            putValue(Action.NAME, "Git Commit");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitCommit.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Git Commit");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Git Commit");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Git.COMMIT);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A GIT PROJECT");
                return;
            }

            var GitCommit = f.get();
            String CommitMsg = JOptionPane.showInputDialog(mainFrame.jFrame,
                    "Write a Commit messsage", null);
            Feature.ExecutionReport report = GitCommit.execute(mainFrame.project, CommitMsg);

            if (!report.isSuccess())
                System.out.println("Commit Failed");
            else
                System.out.println("Commit Done");
        }
    };

    public static class actGitAdd extends AbstractAction {

        MainFrame mainFrame;

        public actGitAdd(MainFrame frame)
        {
            putValue(Action.NAME, "Git Add");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Git Add");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        Set<String> ToAdd = new HashSet<String>();

        void itemStateChanged(ItemEvent e) {
            JCheckBox cb = (JCheckBox) e.getItem();
            int state = e.getStateChange();
            if (state != ItemEvent.DESELECTED)
            {
                System.out.println(cb.getText() + " IS SELECTED");
                ToAdd.add(cb.getText());
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Gid Add");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Git.ADD);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A GIT PROJECT");
                return;
            }
            try {
                Status status = ((AnyProject) mainFrame.project).getgit().status().call();
                Set<String> to_add = new HashSet<String>();
                to_add.addAll(status.getUntracked());
                to_add.addAll(status.getChanged());

                Box pbox = Box.createVerticalBox();
                System.out.println("File you can add : \n");
                for (var file : to_add) {
                    System.out.println(file);
                    JCheckBox box = new JCheckBox(file);
                    box.addItemListener(this::itemStateChanged);
                    pbox.add(box);
                }
                JOptionPane.showConfirmDialog(mainFrame.jFrame, pbox, "Select the files you want to add", JOptionPane.OK_CANCEL_OPTION);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            var GitAdd = f.get();
            Feature.ExecutionReport report = GitAdd.execute(mainFrame.project, ToAdd);
        }
    };

    public static class actGitPull extends AbstractAction
    {
        MainFrame mainFrame;

        public actGitPull(MainFrame frame)
        {
            super("Git Commit");
            putValue(Action.NAME, "Git Pull");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitPull.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Git Pull");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Git Commit");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Git.PULL);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A GIT PROJECT");
                return;
            }

            var GitPull = f.get();
            Feature.ExecutionReport report = GitPull.execute(mainFrame.project);

            if (!report.isSuccess())
                System.out.println("Pull Failed");
            else
                System.out.println("Pull Done");
        }
    };

    public static class actGitPush extends AbstractAction
    {
        MainFrame mainFrame;

        public actGitPush(MainFrame frame)
        {
            super("Git Commit");
            putValue(Action.NAME, "Git Push");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitPush.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Git Push");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Git.PUSH);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A GIT PROJECT");
                return;
            }

            var GitPush = f.get();
            Feature.ExecutionReport report = GitPush.execute(mainFrame.project);

            if (!report.isSuccess())
                System.out.println("Push Failed");
            else
                System.out.println("Push Done");
        }
    };
}
