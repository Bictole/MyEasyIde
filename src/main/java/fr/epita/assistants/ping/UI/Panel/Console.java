package fr.epita.assistants.ping.UI.Panel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Console extends Panel
{

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

        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                textArea.append(String.valueOf((char) b));
            }
        }));
    }
}
