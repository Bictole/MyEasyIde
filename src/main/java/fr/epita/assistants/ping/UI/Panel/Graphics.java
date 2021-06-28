package fr.epita.assistants.ping.UI.Panel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class Graphics {

    public static void ScrollPaneDesign(JScrollPane scrollPane)
    {
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.getColor("GRIS_CLAIR");
                this.trackColor = Color.getColor("GRIS_MIDDLE");
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

        scrollPane.setBorder(null);

        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = Color.getColor("GRIS_CLAIR");
                this.trackColor = Color.getColor("GRIS_MIDDLE");
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
