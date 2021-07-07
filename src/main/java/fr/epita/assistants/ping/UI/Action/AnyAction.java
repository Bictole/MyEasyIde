package fr.epita.assistants.ping.UI.Action;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.UI.Icons;
import fr.epita.assistants.ping.UI.MainFrame;
import fr.epita.assistants.ping.UI.Panel.ExecConfig;
import fr.epita.assistants.ping.feature.any.Run;
import fr.epita.assistants.ping.feature.any.Search;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

import static fr.epita.assistants.ping.UI.UITools.*;

public class AnyAction {

    public static class actAnyCleanUp extends ActionTemplate {

        MainFrame frame;

        public actAnyCleanUp(MainFrame frame) {
            super(
                    "Cleanup",
                    null,
                    KeyEvent.VK_C,
                    "Cleanup",
                    null);
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = frame.project.getFeature(Mandatory.Features.Any.CLEANUP);

            if (f.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Error when finding the Feature.", "Cleanup status", JOptionPane.ERROR_MESSAGE);
                return;
            }

            var AnyCleanUp = f.get();
            Feature.ExecutionReport report = AnyCleanUp.execute(frame.project);

            if (!report.isSuccess())
                JOptionPane.showMessageDialog(frame, "Cleanup failed.", "Cleanup status", JOptionPane.ERROR_MESSAGE);

        }
    };

    public static class actAnyDist extends ActionTemplate {

        MainFrame frame;

        public actAnyDist(MainFrame frame) {
            super(
                    "Dist",
                    null,
                    KeyEvent.VK_D,
                    "Dist",
                    null);
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = frame.project.getFeature(Mandatory.Features.Any.DIST);

            if (f.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Error when finding the feature.", "Dist status", JOptionPane.ERROR_MESSAGE);
                return;
            }

            var AnyDist = f.get();
            Feature.ExecutionReport report = AnyDist.execute(frame.project);

            if (!report.isSuccess())
                JOptionPane.showMessageDialog(frame, "Dist failed.", "Dist status", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class actAnySearch extends ActionTemplate {

        MainFrame frame;

        public actAnySearch(MainFrame frame) {
            super(
                    "Search",
                    getResizedIcon(frame, Icons.GIT_ADD),
                    KeyEvent.VK_G,
                    "Search",
                    null);
            // putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Search");
            Optional<Feature> f = frame.project.getFeature(Mandatory.Features.Any.SEARCH);

            if (f.isEmpty()) {
                System.out.println("Error when finding the Feature");
                return;
            }

            // FIXME : Small box to get the researched word/message and discuss about the return

            var AnyDist = f.get();
            Feature.ExecutionReport report = AnyDist.execute(frame.project);

            if (!report.isSuccess())
                System.out.println("Search Failed");
            else
                System.out.println("Search Done");
        }
    };

    public static class actAnyRun extends ActionTemplate {

        MainFrame frame;

        public actAnyRun(MainFrame frame) {
            super(
                    "Run",
                    getResizedIcon(frame, Icons.RUN),
                    KeyEvent.VK_R,
                    "Run",
                    null);
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = frame.project.getFeature(Mandatory.Features.Any.RUN);

            if (f.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Error when finding the feature.", "Run status", JOptionPane.ERROR_MESSAGE);
                return;
            }

            var AnyRun = f.get();

            Search.ExecutionReportSearch searchReport = (Search.ExecutionReportSearch)
                    frame.getProjectService().execute(frame.project, Mandatory.Features.Any.SEARCH, "public static void main");

            ExecConfig execConfig = new ExecConfig(frame, searchReport.getFilesMatch());
            if (execConfig.getMainClass() == null) {
                JOptionPane.showMessageDialog(frame, "No main class found.", "Run status", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Run.ExecutionReportRun report = (Run.ExecutionReportRun) AnyRun.execute(frame.project, execConfig.getMainFile(), execConfig.getMainParentPath(), execConfig.getMainClass(), execConfig.getMainPackagePath());
            if (report.isSuccess())
                System.out.println("[ " + execConfig.getMainClass() + ".java ] [OUTPUT] : " + report.getOutput());
            else
                System.out.println("[ " + execConfig.getMainClass() + ".java ] [ERROR] : " + report.getOutput());
        }
    };
}
