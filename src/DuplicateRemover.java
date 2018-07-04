package com.elegantbird.csv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DuplicateRemover  extends JPanel implements ActionListener {
    static private final String newline = "\n";
    private JButton openButton, saveKeys;
    private JTextArea log;
    private JFileChooser fc;
    private JTextField primaryKeyInput;
    private String [] primaryKeys;

    protected DuplicateRemover() {
        super(new BorderLayout());

        // Create the log first, because the action listeners
        // need to refer to it.
        log = new JTextArea(25, 100);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);

        // Create a file chooser
        fc = new JFileChooser();

        // Create the open button. We use the image from the JLF
        // Graphics Repository (but we extracted it from the jar).
        openButton = new JButton("Open a File...");
        openButton.addActionListener(this);

        // Create the text input filed
        primaryKeyInput = new JTextField("", 25);
        JLabel primaryKeyLabel = new JLabel("Primary Keys");
        saveKeys = new JButton("Set primary keys");

        // For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); // use FlowLayout
        buttonPanel.add(openButton);

        JPanel textPanel = new JPanel();
        textPanel.add(primaryKeyLabel);
        textPanel.add(primaryKeyInput);
        textPanel.add(saveKeys);

        // Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(textPanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);

        log.append("Instructions" + newline + newline);
        log.append("Choose one file which contains duplicates that you would like removed." + newline);
        log.append("Enter into the text box the PRIMARY KEYS to search for duplicates on. Separate each key with a comma." + newline);
        log.append("The application will search all lines in your CSV file for duplicate keys." + newline);
        log.append("When the process is complete (please be patient, it may take a few seconds), you will see the " +
                        " number of duplicates found "  + newline);
        log.append(newline + "Click the button 'Open a file...' above to begin." + newline + newline);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setMultiSelectionEnabled(false);

            int returnVal = fc.showOpenDialog(DuplicateRemover.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                log.append("Selected file was: " + file.getName());
                searchForDuplicates(file);

            } else {
                log.append("Open command cancelled by user." + newline);
            }

            log.setCaretPosition(log.getDocument().getLength());
        }
        else if (e.getSource() == saveKeys) {
            String primaryKeysValue = primaryKeyInput.getText();
            primaryKeys = primaryKeysValue.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            log.append("Primary keys have been set to " + newline);

            for(String key : primaryKeys) {
                log.append(key + newline);
            }

            log.append(newline);

            log.setCaretPosition(log.getDocument().getLength());
        }
    }

    private int searchForDuplicates(File file) {
        return 0;
    }
}
