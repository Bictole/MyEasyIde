package fr.epita.assistants.ping.UI.Panel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

public class Graphics {

    public static void BottomSplitPaneDesign(JSplitPane bottomSplitPane)
    {
        bottomSplitPane.setUI(new BasicSplitPaneUI());
        bottomSplitPane.setBackground(Color.getColor("GRIS_CLAIR"));
        bottomSplitPane.setForeground(Color.getColor("GRIS_CLAIR"));
        //var raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        bottomSplitPane.setBorder(BorderFactory.createEmptyBorder());
    }

    public static void ScrollPaneDesign(JScrollPane scrollPane, Color Background)
    {
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.getColor("GRIS_CLAIR");
                this.trackColor = Background;
                this.thumbDarkShadowColor = Color.getColor("GRIS_CLAIR");
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
        });

        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.getColor("GRIS_CLAIR");
                this.trackColor = Background;
                this.thumbDarkShadowColor = Color.getColor("GRIS_CLAIR");
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
        });
    }


    public static JButton createZeroButton() {
        JButton button = new JButton("zero button");
        Dimension zeroDim = new Dimension(0,0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }
}
