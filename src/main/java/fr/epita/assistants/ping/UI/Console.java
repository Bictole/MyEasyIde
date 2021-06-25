package fr.epita.assistants.ping.UI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

class Console extends Panel {
    final JFrame frame;
    private JTextArea textArea;
    public JScrollPane scrollPane;

    public Console(JFrame frame) {
        this.frame = frame;
        textArea = new JTextArea(10, 100);
        textArea.setEditable(false);
        textArea.setBackground(Color.DARK_GRAY);
        textArea.setForeground(Color.WHITE);

        scrollPane = new JScrollPane(textArea);

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                textArea.append(String.valueOf((char) b));
            }
        }));
        frame.add(textArea, BorderLayout.PAGE_END);
    }

    public void init() {
        frame.pack();
        frame.setVisible(true);
    }
    public JFrame getFrame() {
        return frame;
    }

    public void writeInConsole(String msg)
    {
        textArea.append(msg);
        return;
    }
}
