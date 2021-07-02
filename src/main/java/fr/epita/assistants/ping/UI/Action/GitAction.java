package fr.epita.assistants.ping.UI.Action;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.UI.Icons;
import fr.epita.assistants.ping.UI.MainFrame;
import fr.epita.assistants.ping.project.AnyProject;
import org.eclipse.jgit.api.Status;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static fr.epita.assistants.ping.UI.UITools.*;


public class GitAction {

    public static class actGitCommit extends ActionTemplate {
        private final MainFrame mainFrame;

        public actGitCommit(MainFrame frame)
        {
            super(
                    "Git commit",
                    getResizedIcon(frame, Icons.GIT_COMMIT),
                    KeyEvent.VK_C,
                    "Git commit",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Git.COMMIT);

            if (f.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "This project is not a git project.", "Commit status", JOptionPane.ERROR_MESSAGE);
                return;
            }

            var GitCommit = f.get();
            String CommitMsg = JOptionPane.showInputDialog(mainFrame.jFrame,
                    "Write a Commit messsage", null);
            if (CommitMsg == null)
                return;
            Feature.ExecutionReport report = GitCommit.execute(mainFrame.project, CommitMsg);

            if (!report.isSuccess())
                JOptionPane.showMessageDialog(mainFrame, "Commit failed.", "Commit status", JOptionPane.ERROR_MESSAGE);
        }
    };

    public static class actGitAdd extends ActionTemplate {

        private final MainFrame mainFrame;

        public actGitAdd(MainFrame frame) {
            super(
                    "Git add",
                    getResizedIcon(frame, Icons.GIT_ADD),
                    KeyEvent.VK_A,
                    "Git add",
                    null);
            this.mainFrame = frame;
        }

        Set<String> ToAdd = new HashSet<String>();

        void itemStateChanged(ItemEvent e) {
            JCheckBox cb = (JCheckBox) e.getItem();
            int state = e.getStateChange();
            if (state != ItemEvent.DESELECTED)
            {
                //System.out.println(cb.getText() + " IS SELECTED");
                ToAdd.add(cb.getText());
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Git.ADD);

            if (f.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "This project is not a git project.", "Add status", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Status status = ((AnyProject) mainFrame.project).getgit().status().call();
                Set<String> to_add = new HashSet<String>();
                to_add.addAll(status.getUntracked());
                to_add.addAll(status.getChanged());

                Box pbox = Box.createVerticalBox();
                for (var file : to_add) {
                    JCheckBox box = new JCheckBox(file);
                    box.addItemListener(this::itemStateChanged);
                    pbox.add(box);
                }
                if(JOptionPane.showConfirmDialog(mainFrame.jFrame, pbox, "Select the files you want to add", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                    return;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            var GitAdd = f.get();
            Feature.ExecutionReport report = GitAdd.execute(mainFrame.project, ToAdd);
            if (!report.isSuccess())
                JOptionPane.showMessageDialog(mainFrame, "Add failed.", "Add status", JOptionPane.ERROR_MESSAGE);
        }
    };

    public static class actGitPull extends ActionTemplate {
        private final MainFrame mainFrame;

        public actGitPull(MainFrame frame)
        {
            super(
                    "Git pull",
                    getResizedIcon(frame, Icons.GIT_PULL),
                    KeyEvent.VK_P,
                    "Git pull",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Git.PULL);

            if (f.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "This project is not a git project.", "Pull status", JOptionPane.ERROR_MESSAGE);
                return;
            }

            var GitPull = f.get();
            Feature.ExecutionReport report = GitPull.execute(mainFrame.project);

            if (!report.isSuccess())
                JOptionPane.showMessageDialog(mainFrame, "Pull failed.", "Pull status", JOptionPane.ERROR_MESSAGE);
        }
    };

    public static class actGitPush extends ActionTemplate {
        private final MainFrame mainFrame;

        public actGitPush(MainFrame frame)
        {
            super(
                    "Git push",
                    getResizedIcon(frame, Icons.GIT_PUSH),
                    KeyEvent.VK_U,
                    "Git push",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Git.PUSH);

            if (f.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "This project is not a git project.", "Pull status", JOptionPane.ERROR_MESSAGE);
                return;
            }

            var GitPush = f.get();
            Feature.ExecutionReport report = GitPush.execute(mainFrame.project);

            if (!report.isSuccess())
                JOptionPane.showMessageDialog(mainFrame, "Push failed.", "Push status", JOptionPane.ERROR_MESSAGE);
        }
    };
}
