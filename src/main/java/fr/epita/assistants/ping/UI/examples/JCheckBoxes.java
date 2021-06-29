package fr.epita.assistants.ping.UI.examples;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class JCheckBoxes extends JFrame {

    JLabel lblMessage = new JLabel("Choose your color");

    JCheckBox chkRed = new JCheckBox("Red");
    JCheckBox chkGreen = new JCheckBox("Green");
    JCheckBox chkBlue = new JCheckBox("Blue");

    JPanel pnlPreview = new JPanel();
    JButton btnQuit = new JButton("Quit");

    public JCheckBoxes() {
        super( "Exemple d'utilisation de la classe JRadioButton" );

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.add( lblMessage, BorderLayout.NORTH );

        JPanel pnlLeft = new JPanel( new GridLayout( 3, 1 ) );
        // On cherche à imposer la largeur
        pnlLeft.setPreferredSize( new Dimension( 100, 0 ) );

        chkRed.setSelected( true );
        pnlLeft.add( chkRed );
        chkRed.addItemListener( this::radioButtons_itemStateChanged );

        pnlLeft.add( chkGreen );
        chkGreen.addItemListener( this::radioButtons_itemStateChanged );

        pnlLeft.add( chkBlue );
        chkBlue.addItemListener( this::radioButtons_itemStateChanged );

        contentPane.add( pnlLeft, BorderLayout.WEST );

        pnlPreview.setBackground(Color.red);
        contentPane.add( pnlPreview, BorderLayout.CENTER );

        btnQuit.addActionListener( (e) -> dispose() );
        contentPane.add( btnQuit, BorderLayout.SOUTH );

        this.setSize(300,160);
        this.setLocationRelativeTo( null );
    }

    void radioButtons_itemStateChanged(ItemEvent e) {
        int red   = chkRed.isSelected() ? 255 : 0;
        int green = chkGreen.isSelected() ? 255 : 0;
        int blue  = chkBlue.isSelected() ? 255 : 0;
        this.pnlPreview.setBackground(new Color(red, green, blue));
    }

    public static void main(String[] args) throws Exception {
        JCheckBoxes frame = new JCheckBoxes();
        frame.setVisible( true );
    }

}