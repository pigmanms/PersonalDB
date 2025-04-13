import javax.swing.*;
import java.awt.*;

public class master {
    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Personal DB");
        frame.setSize(1120, 800); //golden ratio
        frame.setLocationRelativeTo(null);
        frame.requestFocusInWindow();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create the main panel with a border layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create the top panel for titles
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Personal DB", SwingConstants.CENTER);
        JLabel subtitleLabel = new JLabel("By. MyungSu Electronics", SwingConstants.CENTER);
        topPanel.add(titleLabel);
        topPanel.add(subtitleLabel);


        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton newTableButton = new JButton("New Table");
        JButton loadTableButton = new JButton("Load Table");
        JButton listTableButton = new JButton("List Table");
        centerPanel.add(newTableButton);
        centerPanel.add(loadTableButton);
        centerPanel.add(listTableButton);

        // Create the bottom panel for copyright notice
        JPanel bottomPanel = new JPanel();
        JLabel copyrightLabel = new JLabel("(C) MyungSu Electronics 2020-2024 All Rights Reserved.", SwingConstants.CENTER);
        bottomPanel.add(copyrightLabel);

        // Add panels to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        frame.add(mainPanel);

        // Make the frame visible

    }
}
