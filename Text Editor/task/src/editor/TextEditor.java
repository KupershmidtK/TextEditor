package editor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;


public class TextEditor extends JFrame {
    private JFileChooser fileChooser;
    private JCheckBoxMenuItem useRegexMenu;
    private JCheckBox useRegex;
    private JTextArea textArea;
    JTextField patternField;

    private final SearchText searchText = new SearchText();

    public TextEditor() {
        super("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        initMenu();
        initButtonsPanel();

        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setName("FileChooser");
        add(fileChooser);

        textArea = new JTextArea();
        textArea.setName("TextArea");
        searchText.setTextArea(textArea);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setName( "ScrollPane");
        setMargin(scrollPane, 0, 10, 10, 10);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        // File -------------------------------------------------
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.setName("MenuFile");

        JMenuItem loadMenu = new JMenuItem("Load");
        loadMenu.setMnemonic(KeyEvent.VK_L);
        loadMenu.setName("MenuOpen");
        loadMenu.addActionListener(e -> { loadText(); });

        JMenuItem saveMenu = new JMenuItem("Save");
        saveMenu.setMnemonic(KeyEvent.VK_V);
        saveMenu.setName("MenuSave");
        saveMenu.addActionListener(e -> { saveText(); });

        JMenuItem exitMenu = new JMenuItem("Exit");
        exitMenu.setMnemonic(KeyEvent.VK_E);
        exitMenu.setName("MenuExit");
        exitMenu.addActionListener(e -> { dispose(); });
        // Search ------------------------------------------------
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setMnemonic(KeyEvent.VK_S);
        searchMenu.setName("MenuSearch");

        JMenuItem startSearchMenu = new JMenuItem("Start search");
        startSearchMenu.setName("MenuStartSearch");
        startSearchMenu.addActionListener(event -> { startSearch(); });

        JMenuItem prevMatchMenu = new JMenuItem("Previous search");
        prevMatchMenu.setName("MenuPreviousMatch");
        prevMatchMenu.addActionListener(e -> { prevSearch(); });

        JMenuItem nextMatchMenu = new JMenuItem("Next match");
        nextMatchMenu.setName("MenuNextMatch");
        nextMatchMenu.addActionListener(e -> { nextSearch(); });

        useRegexMenu = new JCheckBoxMenuItem("Use regular expression");
        useRegexMenu.setName("MenuUseRegExp");
        useRegexMenu.addActionListener(e -> {
            useRegex.setSelected(useRegexMenu.isSelected());
            searchText.setUseRegex(useRegexMenu.isSelected());
        });

        menu.add(loadMenu);
        menu.add(saveMenu);
        menu.addSeparator();
        menu.add(exitMenu);
        searchMenu.add(startSearchMenu);
        searchMenu.add(prevMatchMenu);
        searchMenu.add(nextMatchMenu);
        searchMenu.add(useRegexMenu);
        menuBar.add(menu);
        menuBar.add(searchMenu);

        setJMenuBar(menuBar);
    }

    private void initButtonsPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

        JButton loadButton = new JButton(new ImageIcon("icons8-opened-folder-20.png"));
        loadButton.setPreferredSize(new Dimension(30, 30));
        loadButton.setName("OpenButton");
        loadButton.addActionListener(e -> { loadText(); });
        buttonPanel.add(loadButton);

        JButton saveButton = new JButton(new ImageIcon("icons8-save-20.png"));
        saveButton.setPreferredSize(new Dimension(30, 30));
        saveButton.setName("SaveButton");
        saveButton.addActionListener(e -> { saveText(); });
        buttonPanel.add(saveButton);

        patternField = new JTextField();
        patternField.setPreferredSize(new Dimension(200, 30));
        patternField.setName("SearchField");
        buttonPanel.add(patternField);

        JButton searchButton = new JButton(new ImageIcon("icons8-search-20.png"));
        searchButton.setPreferredSize(new Dimension(30, 30));
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(e -> { startSearch(); });
        buttonPanel.add(searchButton);

        JButton prevMatchButton = new JButton(new ImageIcon("icons8-back-20.png"));
        prevMatchButton.setPreferredSize(new Dimension(30, 30));
        prevMatchButton.setName("PreviousMatchButton");
        prevMatchButton.addActionListener(e -> { prevSearch(); });
        buttonPanel.add(prevMatchButton);

        JButton nextMatchButton = new JButton(new ImageIcon("icons8-forward-20.png"));
        nextMatchButton.setPreferredSize(new Dimension(30, 30));
        nextMatchButton.setName("NextMatchButton");
        nextMatchButton.addActionListener(e -> { nextSearch(); });
        buttonPanel.add(nextMatchButton);

        useRegex = new JCheckBox("Use regex");
        useRegex.setName("UseRegExCheckbox");
        useRegex.addActionListener(e -> {
            useRegexMenu.setState(useRegex.isSelected());
            searchText.setUseRegex(useRegex.isSelected());
        });
        buttonPanel.add(useRegex);

        add(buttonPanel, BorderLayout.PAGE_START);
    }

    private void saveText() {
        File file = null;

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        } else {
            return;
        }
        BufferedWriter outFile = null;
        try {
            outFile = new BufferedWriter(new FileWriter(file));
            textArea.write(outFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (outFile != null) {
                try {
                    outFile.close();
                } catch (IOException ignore) {}
            }
        }
    }

    private void loadText() {
        File file = null;

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        } else {
            return;
        }

        BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(file));
            textArea.read(inFile, null);
        } catch (IOException ex) {
            textArea.setText(null);
            ex.printStackTrace();
        } finally {
            if (inFile != null) {
                try {
                    inFile.close();
                } catch (IOException ignore) {}
            }
        }
    }

    private void startSearch() {
        SwingWorker<Object, Object> searcher = new SwingWorker<>() {
            @Override
            public Object doInBackground() {
                searchText.search(patternField.getText());
                return new Object();
            }
        };
        searcher.execute();
    }

    private void nextSearch() {
        SwingWorker<Object, Object> searcher = new SwingWorker<>() {
            @Override
            public Object doInBackground() {
                searchText.next();
                return new Object();
            }
        };
        searcher.execute();
    }

    private void prevSearch() {
        SwingWorker<Object, Object> searcher = new SwingWorker<>() {
            @Override
            public Object doInBackground() {
                searchText.prev();
                return new Object();
            }
        };
        searcher.execute();
    }

    private void setMargin(JComponent aComponent, int aTop, int aRight, int aBottom, int aLeft) {
        Border border = aComponent.getBorder();
        Border marginBorder = new EmptyBorder(new Insets(aTop, aLeft, aBottom, aRight));
        aComponent.setBorder(border == null ? marginBorder : new CompoundBorder(marginBorder, border));
    }
}
