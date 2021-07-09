package fr.epita.assistants.ping.UI.Action;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.UI.Icons;
import fr.epita.assistants.ping.UI.MainFrame;
import fr.epita.assistants.ping.UI.Panel.ExecConfig;
import fr.epita.assistants.ping.feature.any.CleanUp;
import fr.epita.assistants.ping.feature.any.Run;
import fr.epita.assistants.ping.feature.any.Search;
import fr.epita.assistants.ping.feature.any.Stop;

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
            CleanUp.ExecutionReportCleanUp report = (CleanUp.ExecutionReportCleanUp) AnyCleanUp.execute(frame.project);

            if (!report.isSuccess())
                JOptionPane.showMessageDialog(frame, report.getOutput(), "Cleanup status", JOptionPane.ERROR_MESSAGE);
            else
                System.out.println("[ CLEANUP ] [OUTPUT] : \n\nClean Up successfully done");
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
            else
                System.out.println("[ DIST ] [OUTPUT] : \n\nDist successfully done. " +
                        "Compressed folder can be found at the same location than your current project.");
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

            if (searchReport.getFilesMatch().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No main class found.", "Run status", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ExecConfig execConfig = new ExecConfig(frame, searchReport.getFilesMatch());
            if (execConfig.getMainClass() == null) {
                return;
            }
            Run.ExecutionReportRun report = (Run.ExecutionReportRun) AnyRun.execute(frame.project, execConfig.getMainFile(), execConfig.getMainParentPath(), execConfig.getMainClass(), execConfig.getMainPackagePath(), execConfig.getArgs());
            if (report.isSuccess())
                System.out.println("[ " + execConfig.getMainClass() + ".java ] [OUTPUT] : \n\n" + report.getOutput());
            else
                System.out.println("[ " + execConfig.getMainClass() + ".java ] [ERROR] : \n\n" + report.getOutput());
        }
    };

    public static class actAnyStop extends ActionTemplate {

        MainFrame frame;

        public actAnyStop(MainFrame frame) {
            super(
                    "Stop",
                    getResizedIcon(frame,Icons.STOP),
                    KeyEvent.VK_S,
                    "Stop",
                    null);
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = frame.project.getFeature(Mandatory.Features.Any.STOP);

            if (f.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Error when finding the Feature.", "Stop status", JOptionPane.ERROR_MESSAGE);
                return;
            }

            var AnyStop = f.get();
            Stop.ExecutionReportStop report = (Stop.ExecutionReportStop) AnyStop.execute(frame.project, frame.ongoing);

            if (!report.isSuccess())
                JOptionPane.showMessageDialog(frame, report.getOutput(), "Stop status", JOptionPane.ERROR_MESSAGE);
            else
                System.out.println("[ CLEANUP ] [OUTPUT] : \n\n" + report.getOutput());
        }
    };
}
