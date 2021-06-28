package fr.epita.assistants.ping.UI;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

public class AnyAction {

    public static class actAnyCleanUp extends AbstractAction {

        MainFrame frame;

        public actAnyCleanUp(MainFrame frame)
        {
            putValue(Action.NAME, "Cleanup");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Cleanup");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Cleanup");
            Optional<Feature> f = frame.project.getFeature(Mandatory.Features.Any.CLEANUP);

            if (f.isEmpty()) {
                System.out.println("Error when finding the Feature");
                return;
            }

            var AnyCleanUp = f.get();
            Feature.ExecutionReport report = AnyCleanUp.execute(frame.project);

            if (!report.isSuccess()) {
                System.out.println("Cleanup Failed");
            }
            else
                System.out.println("Cleanup Done");
        }
    };

    public static class actAnyDist extends AbstractAction {

        MainFrame frame;

        public actAnyDist(MainFrame frame)
        {
            putValue(Action.NAME, "Dist");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Dist");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Dist");
            Optional<Feature> f = frame.project.getFeature(Mandatory.Features.Any.DIST);

            if (f.isEmpty()) {
                System.out.println("Error when finding the Feature");
                return;
            }

            var AnyDist = f.get();
            Feature.ExecutionReport report = AnyDist.execute(frame.project);

            if (!report.isSuccess())
                System.out.println("Dist Failed");
            else
                System.out.println("Dist Done");
        }
    }

    public static class actAnySearch extends AbstractAction {

        MainFrame frame;

        public actAnySearch(MainFrame frame)
        {
            putValue(Action.NAME, "Search");
            putValue(Action.SMALL_ICON, frame.resizeIcon(new ImageIcon("src/main/resources/icons/gitAdd.png"), frame.iconWidth, frame.iconHeight));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_G);
            putValue(Action.SHORT_DESCRIPTION, "Search");
            //putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
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
}
