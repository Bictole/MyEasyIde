package fr.epita.assistants.ping.UI.examples;

import javax.swing.*;

public class TestUI extends JFrame{
    private JPanel mainPanel;
    private JTextArea textArea1;
    private JTree tree1;

    public TestUI(String title)
    {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
    }

    public static void main(String[] args) {
        JFrame frame = new TestUI("Fenêtre test");
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
