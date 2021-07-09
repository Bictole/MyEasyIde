package fr.epita.assistants.ping.UI.Action;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.UI.Icons;
import fr.epita.assistants.ping.UI.MainFrame;
import fr.epita.assistants.ping.UI.Panel.ExecConfig;
import fr.epita.assistants.ping.UI.UITools;
import fr.epita.assistants.ping.feature.any.Search;
import fr.epita.assistants.ping.feature.maven.Package;
import fr.epita.assistants.ping.feature.maven.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

import static fr.epita.assistants.ping.UI.UITools.getResizedIcon;

public class MavenAction {

    public static class actMvnClean extends UITools.ActionTemplate {

        MainFrame mainFrame;

        public actMvnClean(MainFrame frame) {
            super(
                    "Maven Clean",
                    getResizedIcon(frame, Icons.GIT_ADD),
                    KeyEvent.VK_G,
                    "Maven Clean",
                    null);
                    this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.CLEAN);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenClean = f.get();
            Clean.ExecutionReportClean report = (Clean.ExecutionReportClean) MavenClean.execute(mainFrame.project);
            System.out.println(report.getOutput());
        }
    }

    public static class actMvnCompile extends UITools.ActionTemplate {

        MainFrame mainFrame;

        public actMvnCompile(MainFrame frame) {

            super(
                    "Maven Compile",
                    getResizedIcon(frame, Icons.GIT_ADD),
                    KeyEvent.VK_G,
                    "Maven Compile",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.COMPILE);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenCompile = f.get();
            Compile.ExecutionReportCompile report = (Compile.ExecutionReportCompile) MavenCompile.execute(mainFrame.project);
            System.out.println(report.getOutput());
        }
    }

    public static class actMvnExec extends UITools.ActionTemplate {

        MainFrame mainFrame;

        public actMvnExec(MainFrame frame) {

            super(
                    "Maven Exec",
                    getResizedIcon(frame, Icons.RUN),
                    KeyEvent.VK_G,
                    "Maven Exec",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.EXEC);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }
            var MavenExec = f.get();

            Search.ExecutionReportSearch searchReport = (Search.ExecutionReportSearch)
                    mainFrame.getProjectService().execute(mainFrame.project, Mandatory.Features.Any.SEARCH, "public static void main");
            if (searchReport.getFilesMatch().isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No main class found.", "Maven Exec status", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ExecConfig execConfig = new ExecConfig(mainFrame, searchReport.getFilesMatch());
            if (execConfig.getMainClass() == null) {
                return;
            }
            Exec.ExecutionReportExecute report = (Exec.ExecutionReportExecute) MavenExec.execute(mainFrame.project, execConfig.getMainClass(), execConfig.getArgs());
            System.out.println(report.getOutput());
        }
    }

    public static class actMvnInstall extends UITools.ActionTemplate {

        MainFrame mainFrame;

        public actMvnInstall(MainFrame frame) {

            super(
                    "Maven Install",
                    getResizedIcon(frame, Icons.GIT_ADD),
                    KeyEvent.VK_G,
                    "Maven Install",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.INSTALL);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenInstall = f.get();
            Install.ExecutionReportInstall report = (Install.ExecutionReportInstall) MavenInstall.execute(mainFrame.project);
            System.out.println(report.getOutput());
        }
    }

    public static class actMvnPackage extends UITools.ActionTemplate {

        MainFrame mainFrame;

        public actMvnPackage(MainFrame frame) {
            super(
                    "Maven Package",
                    getResizedIcon(frame, Icons.GIT_ADD),
                    KeyEvent.VK_G,
                    "Maven Package",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.PACKAGE);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenPackage = f.get();
            Package.ExecutionReportPackage report = (Package.ExecutionReportPackage) MavenPackage.execute(mainFrame.project);
            System.out.println(report.getOutput());
        }
    }

    public static class actMvnTest extends UITools.ActionTemplate {

        MainFrame mainFrame;

        public actMvnTest(MainFrame frame) {
            super(
                    "Maven Test",
                    getResizedIcon(frame, Icons.GIT_ADD),
                    KeyEvent.VK_G,
                    "Maven Test",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.TEST);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenTest = f.get();
            Test.ExecutionReportTest report = (Test.ExecutionReportTest) MavenTest.execute(mainFrame.project);
            System.out.println(report.getOutput());
        }
    }

    public static class actMvnTree extends UITools.ActionTemplate {

        MainFrame mainFrame;

        public actMvnTree(MainFrame frame) {
            super(
                    "Maven Tree",
                    getResizedIcon(frame, Icons.GIT_ADD),
                    KeyEvent.VK_G,
                    "Maven Tree",
                    null);
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.TREE);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenClean = f.get();
            Tree.ExecutionReportTree report = (Tree.ExecutionReportTree) MavenClean.execute(mainFrame.project, "-Doutput=tree_output.txt");
            System.out.println(report.getOutput());
        }
    }
}
