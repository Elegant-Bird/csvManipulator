package com.elegantbird.csv;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class FileMerger extends JPanel implements ActionListener {
    static private final String newline = "\n";
    private JButton openButton, folderButton;
    private JTextArea log;
    private JFileChooser fc;
    private List<LinkedHashMap<String, String>> compiledFileData = new ArrayList<>();
    private Set<String> masterHeaders = new LinkedHashSet<>();

    public void ResetValues() {

    }

    protected FileMerger() {
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

        // Create the save button. We use the image from the JLF
        // Graphics Repository (but we extracted it from the jar).
        folderButton = new JButton("Open a Folder...");
        folderButton.addActionListener(this);

        // For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); // use FlowLayout
        buttonPanel.add(folderButton);

        // Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);

        log.append("Instructions" + newline);
        log.append("This application assumes that all files to be merged are in one folder, or directory." + newline);
        log.append("Each file in the directory MUST BE a .csv file." + newline);
        log.append("The application will collect all of the files in the directory, merge them, and output a new file." + newline);
        log.append("When the process is complete (please be patient, it may take a few seconds), you will see the filename output here." + newline);
        log.append("Click the button 'Open a folder...' above to begin." + newline + newline);

        log.append("Note: Your new file will be created in the same directory. Please remove it if you wish to start the process again." + newline + newline);
    }

    public void actionPerformed(ActionEvent e) {
        List<File> allFiles = new ArrayList<>();
        boolean printFile = false;

        if (e.getSource() == folderButton) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnVal = fc.showOpenDialog(FileMerger.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File folder = fc.getSelectedFile();
                fc.setCurrentDirectory(folder);
                File[] files = fc.getCurrentDirectory().listFiles();
                for (int i = 0; i < files.length; i++) {
                    log.append("Opening: " + files[i].getName() + "." + newline);
                    allFiles.add(files[i]);
                }
                printFile = true;
            } else {
                log.append("Save command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
        }

        if(printFile) {

            for (File file : allFiles) {
                log.append("Reading file: " + file.getName() + newline);
                List<LinkedHashMap<String, String>> fileData = readFile(file);
                for (LinkedHashMap<String, String> line : fileData) {
                    compiledFileData.add(line);
                }
            }

            List<LinkedHashMap<String, String>> normalizedOutput = normalizeData(compiledFileData);

            try {
                log.append(newline + "Creating your new merged file." + newline);
                GenerateCSVFile(normalizedOutput);
            } catch (IOException errorFound) {
                errorFound.printStackTrace();
            }
        }
    }

    public void GenerateCSVFile(List<LinkedHashMap<String, String>> normalizedOutput) throws IOException {

        List<String> csvLines = new ArrayList<>();
        String collectionLine = "";

        //First, add in all of the headers
        collectionLine = masterHeaders.stream().collect(Collectors.joining(","));
        csvLines.add(collectionLine);

        //Next, add in each line of data
        for(LinkedHashMap<String, String> lineItem : normalizedOutput) {
            collectionLine = "";
            for (String key : lineItem.keySet()) {
                collectionLine += lineItem.get(key) + ",";
            }
            csvLines.add(collectionLine.substring(0, collectionLine.length() - 1));
        }

        //Finally, write the file
        String directoryPath = fc.getCurrentDirectory().getPath();
        File createdFile = new File(directoryPath + "/" + "mergedDocs_" + (new Date()) + ".csv");
        createdFile.createNewFile();

        FileWriter writer = new FileWriter(createdFile);
        for(String lineItem : csvLines) {
            writer.write(lineItem);
            writer.write("\n");
        }
        writer.flush();
        writer.close();
        log.append("File creation successful. New file located at: " + createdFile.getAbsolutePath());
    }


    public List<LinkedHashMap<String, String>> normalizeData(List<LinkedHashMap<String, String>> allDataRows) {
        List<LinkedHashMap<String, String>> normalizedList = new ArrayList<>();
        LinkedHashMap<String, String> normalizedRow = new LinkedHashMap<>();

        for( LinkedHashMap<String, String> dataRow : allDataRows) {

            for(String header : masterHeaders) {
                String dataValue = dataRow.get(header);
                if(dataValue != null) {
                    normalizedRow.put(header, dataValue);
                } else {
                    normalizedRow.put(header, "");
                }
            }

            normalizedList.add((LinkedHashMap<String, String>)normalizedRow.clone());
            normalizedRow.clear();
        }

        return normalizedList;
    }

    public List<LinkedHashMap<String, String>> readFile(File csvFile) {
        String line;
        boolean isHeaderLine = true;
        List<String> headers = new ArrayList<>();
        LinkedHashMap<String, String> lineMap = new LinkedHashMap<>();
        List<LinkedHashMap<String, String>> allLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                Object [] items = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                for (int i = 0; i < items.length; i++) {
                    String item = items[i].toString();

                    if(isHeaderLine){
                        if(item.length() == 0) item = "Header_" + (i+1);
                        headers.add(item);
                        masterHeaders.add(item);
                    }
                    else {
                        lineMap.put(headers.get(i), "" + item);
                    }
                }

                if(isHeaderLine) {
                    isHeaderLine = false;
                }
                else {
                    allLines.add(lineMap);
                    lineMap.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return allLines;
        }
    }

}