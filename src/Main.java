package com.elegantbird.csv;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main implements ActionListener {

    static JButton mergerButton = new JButton("Merge Files");
    static JButton duplicateButton = new JButton("Remove Duplicates");
    static JFrame frame = new JFrame("CSV Manipulator");

    /***
     * Create an empty controller for reference within static methods
     */
    public Main() { }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mergerButton) {
            createAndShowFileMerger();
        } else if (e.getSource() == duplicateButton) {
            createAndShowDuplicateRemover();
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be invoked
     * from the event dispatch thread.
     */
    private void createAndShowFileMerger() {
        // Create and set up the window.
        JFrame frame = new JFrame("File Merger");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Add content to the window.
        frame.add(new FileMerger());

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private void createAndShowDuplicateRemover() {
        // Create and set up the window.
        JFrame frame = new JFrame("Duplicate Remover");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Add content to the window.
        frame.add(new DuplicateRemover());

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private static void createAndShowMainMenu(Main instance) {
        mergerButton.addActionListener(instance);
        mergerButton.setSize(500, 300);
        duplicateButton.addActionListener(instance);

        // For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); // use FlowLayout
        buttonPanel.add(mergerButton);
        buttonPanel.add(duplicateButton);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add content to the window.
        frame.add(buttonPanel);

        // Display the window.
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowMainMenu(new Main());
            }
        });
    }
}
