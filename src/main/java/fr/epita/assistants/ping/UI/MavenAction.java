package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.ping.UI.Panel.ExecConfig;
import fr.epita.assistants.ping.feature.any.Search;
import fr.epita.assistants.ping.feature.maven.*;
import fr.epita.assistants.ping.feature.maven.Package;
import fr.epita.assistants.ping.project.AnyProject;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Status;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MavenAction {

    public static class actMvnClean extends AbstractAction {

        MainFrame mainFrame;

        public actMvnClean(MainFrame frame) {

            putValue(Action.NAME, "Maven Clean");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Maven Clean");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Maven Clean");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.CLEAN);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenClean = f.get();
            Clean.ExecutionReportClean report = (Clean.ExecutionReportClean) MavenClean.execute(mainFrame.project);

            if (!report.isSuccess()) {
                System.out.println("Maven Clean Failed : " + report.errorMessage);
            } else
                System.out.println("Maven Clean Done");
        }
    }

    public static class actMvnCompile extends AbstractAction {

        MainFrame mainFrame;

        public actMvnCompile(MainFrame frame) {
            putValue(Action.NAME, "Maven Compile");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Maven Compile");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Maven Compile");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.COMPILE);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenCompile = f.get();
            Compile.ExecutionReportCompile report = (Compile.ExecutionReportCompile) MavenCompile.execute(mainFrame.project);

            if (!report.isSuccess())
                System.out.println("Maven Compile Failed" + report.getErrorMessage());
            else
                System.out.println("Maven Compile Done");
        }
    }

    public static class actMvnExec extends AbstractAction {

        MainFrame mainFrame;

        public actMvnExec(MainFrame frame) {
            putValue(Action.NAME, "Maven Exec");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Maven Exec");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Maven Exec");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.EXEC);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }
            var MavenExec = f.get();

            Search.ExecutionReportSearch searchReport = (Search.ExecutionReportSearch)
                    mainFrame.getProjectService().execute(mainFrame.project, Mandatory.Features.Any.SEARCH, "public static void main");

            ExecConfig execConfig = new ExecConfig(mainFrame, searchReport.getFilesMatch());
            Exec.ExecutionReportExecute report = (Exec.ExecutionReportExecute) MavenExec.execute(mainFrame.project, execConfig.getMainClass());
            if (!report.isSuccess())
                System.out.println("Maven Exec Failed" + report.getErrorMessage());
            else
                System.out.println("Maven Exec Done");

        }
    }

    public static class actMvnInstall extends AbstractAction {

        MainFrame mainFrame;

        public actMvnInstall(MainFrame frame) {
            putValue(Action.NAME, "Maven Install");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Maven Install");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Maven Install");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.INSTALL);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenInstall = f.get();
            Install.ExecutionReportInstall report = (Install.ExecutionReportInstall) MavenInstall.execute(mainFrame.project);

            if (!report.isSuccess())
                System.out.println("Maven Install Failed" + report.getErrorMessage());
            else
                System.out.println("Maven Install Done");
        }
    }

    public static class actMvnPackage extends AbstractAction {

        MainFrame mainFrame;

        public actMvnPackage(MainFrame frame) {
            putValue(Action.NAME, "Maven Package");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Maven Package");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Maven Package");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.PACKAGE);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenPackage = f.get();
            Package.ExecutionReportPackage report = (Package.ExecutionReportPackage) MavenPackage.execute(mainFrame.project);

            if (!report.isSuccess())
                System.out.println("Maven Package Failed" + report.getErrorMessage());
            else
                System.out.println("Maven Package Done");
        }
    }

    public static class actMvnTest extends AbstractAction {

        MainFrame mainFrame;

        public actMvnTest(MainFrame frame) {
            putValue(Action.NAME, "Maven Test");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Maven Test");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Maven Test");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.TEST);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenTest = f.get();
            Test.ExecutionReportTest report = (Test.ExecutionReportTest) MavenTest.execute(mainFrame.project);

            if (!report.isSuccess())
                System.out.println("Maven Test Failed" + report.getErrorMessage());
            else
                System.out.println("Maven Test Done");
        }
    }

    public static class actMvnTree extends AbstractAction {

        MainFrame mainFrame;

        public actMvnTree(MainFrame frame) {
            putValue(Action.NAME, "Maven Tree");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Maven Tree");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.mainFrame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Maven Tree");
            Optional<Feature> f = mainFrame.project.getFeature(Mandatory.Features.Maven.TREE);

            if (f.isEmpty()) {
                System.out.println("THIS PROJECT IS NOT A MAVEN PROJECT");
                return;
            }

            var MavenClean = f.get();
            Clean.ExecutionReportClean report = (Clean.ExecutionReportClean) MavenClean.execute(mainFrame.project, "-Doutput=tree_output.txt");

            if (!report.isSuccess())
                System.out.println("Maven Tree Failed" + report.getErrorMessage());
            else
                System.out.println("Maven Tree Done");
        }
    }
}
