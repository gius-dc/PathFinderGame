import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class App extends JFrame implements Observer {
    private JPanel mainPanel;
    private JPanel labirintoPanel;

    private JButton avviaButton;
    private JLabel stateLabel;
    private JTable table1;
    private JScrollPane jScrollPane1;
    private JPanel topPanel;
    private JLabel labelImg;
    private JLabel stateRobotLabel;
    private JButton nextLevelButton;
    Labirinto l;
    int row = 16, col = 16;
    Boolean firstRun = true;
    RobotState state;
    MyTableModel model;
    int level = 0;
    int[] scores = new int[3];

    Caretaker caretaker = null;
    File fileClassifica;
    TableRowSorter<TableModel> sorter;

    public App() {
        setContentPane(mainPanel);
        setTitle("Labirinto");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 700);
        setVisible(true);
        nextLevelButton.setVisible(false);
        stateLabel.setText("Livello " + (level + 1) + " - Avvia  per iniziare");

        //File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "classifica.csv");
        if (!fileClassifica.exists()) {
            try {
                fileClassifica.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        prepareLabyrinth();
        //char lab[][] = l.getLabyrinth();
        Color checker;

        firstRun = false;
        setSize(900, 600);

        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(level < 1)
                        {
                            level++;
                        }
                        else if(level >= 1){
                            level = 0;
                        }
                        prepareLabyrinth();
                        nextLevelButton.setVisible(false);
                        avviaButton.setVisible(true);
                        labelImg.setIcon(null);
                        stateLabel.setText("Livello " + (level + 1) + " - Avvia  per iniziare");
                    }
                });
            }
        });

        avviaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        nextLevelButton.setVisible(true);
                        avviaButton.setVisible(false);
                        startLabyrinth();
                    }
                });
            }
        });
    }

    public void prepareLabyrinth() {
        Level.Builder builder = new Level.Builder(16, 16);
        if (level == 0) {
            builder.aggiungiPareti();
            builder.aggiungiPareteVerticale(3, 2, 9);
            builder.aggiungiPareteVerticale(7, 8, 14);
            builder.aggiungiPareteOrizzontale(3, 7, 14);
            builder.aggiungiPareteOrizzontale(10, 11, 13);
            builder.impostaPosizionePortaUscita(12, 0);
        } else if (level == 1) {
            builder.aggiungiPareti();
            builder.aggiungiPareteVerticale(6,1,7);
            builder.aggiungiPareteVerticale(9,2,10);
            builder.aggiungiPareteVerticale(9,2,10);
            builder.aggiungiPareteOrizzontale(9,1, 5);
            builder.aggiungiPareteOrizzontale(7,11, 14);
            builder.aggiungiPareteOrizzontale(4,2, 4);
            builder.aggiungiPareteVerticale(3,11,14);
            builder.aggiungiPareteOrizzontale(13,7, 12);
            builder.impostaPosizionePortaUscita(13, 15);
        }

        Level livello = builder.build();
        drawLabyrinth(livello);
        l = new Labirinto(livello);
        l.addObserver(this);
    }

    public void drawLabyrinth(Level l) {
        char[][] labirinto = l.getLabyrinth();
        if (firstRun) {
            Color checker;
            labirintoPanel.setLayout(new GridLayout(row, col));
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    if (labirinto[x][y] == '#') {
                        checker = Color.BLACK;
                    } else {
                        checker = Color.WHITE;
                    }
                    //JPanel panel = new JPanel();
                    ImagePanel panel = new ImagePanel(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                    if (checker == Color.BLACK) {
                        panel.setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                    }
                    panel.setPreferredSize(new Dimension(400 / row, 400 / col));
                    panel.setBackground(Color.WHITE);
                    labirintoPanel.add(panel);
                }
            }

            firstRun = false;
        } else {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    if (labirinto[x][y] == '#') {
                        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                    } else {
                        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                    }
                }
            }


        }

    }

    public void startLabyrinth() {
        labirintoPanel.setBackground(Color.WHITE);
        caretaker = new Caretaker();
        Color checker;
        char lab[][] = l.getLabyrinth();


        Icon imgIcon = new ImageIcon(getClass().getResource("/img/loading30x30.gif").toString().substring(5));
        labelImg.setIcon(imgIcon);
        stateLabel.setText("Labirinto in esecuzione.");
        while (l.iterate()) {
            state = l.getRobot().getState();
            if (state instanceof PursuitState) {
                // esegui l'azione per lo stato pursuit
                stateRobotLabel.setText("Stato robot: pursuit");
            } else if (state instanceof SeekState) {
                // esegui l'azione per lo stato seek
                stateRobotLabel.setText("Stato robot: seek");
            } else if (state instanceof FleeState) {
                // esegui l'azione per lo stato flee
                stateRobotLabel.setText("Stato robot: flee");
            } else if (state instanceof EvadeState) {
                // esegui l'azione per lo stato evade
                stateRobotLabel.setText("Stato robot: evade");
            }

            if (caretaker.sizeMemento() > 0) {
                Memento memento = caretaker.getMemento(caretaker.sizeMemento() - 1);

                ((ImagePanel) labirintoPanel.getComponent((memento.getX() * 16) + memento.getY())).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());

            }
            ((ImagePanel) labirintoPanel.getComponent((l.getRobotX() * 16) + l.getRobotY())).setImage(new ImageIcon(getClass().getResource("/img/circle_gray.png").toString().substring(5)).getImage());

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            caretaker.addMemento(l.getRobot().saveToMemento()); // salva il nuovo stato del robot (design pattern Memento)
        }


        imgIcon = new ImageIcon(getClass().getResource("/img/finish.png").toString().substring(5));
        labelImg.setIcon(imgIcon);
        stateRobotLabel.setText("");
        stateLabel.setText("Il robot ha raggiunto la destinazione.");

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (lab[x][y] == '#') {
                    ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                } else {
                    ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                }
            }
        }


        // Il programma visualizza il percorso che il robot ha effettuato (Memento)
        for (int i = 0; i < caretaker.sizeMemento(); i++) {
            Memento memento = caretaker.getMemento(i);
            l.getRobot().restoreFromMemento(memento);

            ((ImagePanel) labirintoPanel.getComponent((l.getRobot().getX() * 16) + l.getRobot().getY())).setImage(new ImageIcon(getClass().getResource("/img/square_gray.png").toString().substring(5)).getImage());
        }


        scores[level] = caretaker.sizeMemento();

        nextLevelButton.setEnabled(true);


        if (level >= 1) {
            Object[] row = new Object[5];
            String inputName = JOptionPane.showInputDialog("Inserisci il nome del robot:");
            if (inputName == "" || inputName == null) {
                row[0] = "(senza nome)";
            } else {
                row[0] = inputName;
            }
            String inputSurname = JOptionPane.showInputDialog("Inserisci il cognome del robot:");
            if (inputSurname == "" || inputSurname == null) {
                row[1] = "(senza conome)";
            } else {
                row[1] = inputSurname;
            }
            row[2] = scores[0];
            row[3] = scores[1];
            row[4] = (scores[0] + scores[1]);

            model.addRow(row);
            sorter.sort();

            try {
                model.saveToFile(fileClassifica);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        App myApp = new App();
    }

    private Boolean createFile() {
        fileClassifica = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "classifica.csv");
        if (!fileClassifica.exists()) {
            try {
                fileClassifica.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void createUIComponents() throws URISyntaxException, MalformedURLException {
        Boolean fileCreatedOrRead = false;
        while(!fileCreatedOrRead)
        {
            if(createFile()){
                model = new MyTableModel();
                table1 = new JTable(model);
                fileCreatedOrRead = true;
            }
            else{
                model = new MyTableModel();
                try {
                    model.loadFromFile(fileClassifica);
                    table1 = new JTable(model);
                    fileCreatedOrRead = true;
                } catch (IOException | IndexOutOfBoundsException e) {
                    JOptionPane.showMessageDialog(this, "Errore nella lettura del file 'classifica.csv', la classifica non è stata caricata.",
                            "Errore file", JOptionPane.ERROR_MESSAGE);

                    int result = JOptionPane.showConfirmDialog(this, "Si consiglia di eliminare il file 'classifica.csv', procedere?", "Eliminare il file?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        fileClassifica.delete();
                        JOptionPane.showMessageDialog(this, "Il file è stato eliminato e ne verrà creato uno nuovo.",
                                "Avviso", JOptionPane.INFORMATION_MESSAGE);
                    } else if (result == JOptionPane.NO_OPTION) {
                        JOptionPane.showMessageDialog(this, "Il file non è stato eliminato, potrebbero sorgere problemi durante l'esecuzione del programma.",
                                "Avviso", JOptionPane.WARNING_MESSAGE);
                                fileCreatedOrRead = true;
                    }
                }
            }
        }

        sorter = new TableRowSorter<>(table1.getModel());
        table1.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    public class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"Nome", "Cognome", "Passi LV.1", "Passi LV.2", "Totale passi"};
        private Object[][] data = {};

        public void addRow(Object[] rowData) {
            int rowCount = getRowCount();
            Object[][] newData = new Object[rowCount + 1][getColumnCount()];
            System.arraycopy(data, 0, newData, 0, rowCount);
            newData[rowCount] = rowData;
            data = newData;
            fireTableRowsInserted(rowCount, rowCount);
        }

        public void addColumn(String columnName, Object[] columnData) {
            int columnCount = getColumnCount();
            String[] newColumnNames = new String[columnCount + 1];
            System.arraycopy(columnNames, 0, newColumnNames, 0, columnCount);
            newColumnNames[columnCount] = columnName;
            columnNames = newColumnNames;

            Object[][] newData = new Object[getRowCount()][columnCount + 1];
            for (int i = 0; i < getRowCount(); i++) {
                System.arraycopy(data[i], 0, newData[i], 0, columnCount);
                newData[i][columnCount] = columnData[i];
            }
            data = newData;
            fireTableStructureChanged();
        }

        public void saveToFile(File file) throws IOException {
            try (FileWriter writer = new FileWriter(file)) {
                for (String columnName : columnNames) {
                    writer.append(columnName).append(",");
                }
                writer.append("\n");
                for (Object[] row : data) {
                    for (Object value : row) {
                        writer.append(String.valueOf(value)).append(",");
                    }
                    writer.append("\n");
                }
            }
        }

        public void loadFromFile(File file) throws IOException {
            List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
            columnNames = lines.get(0).split(",");
            List<Object[]> rows = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(",");
                Object[] row = new Object[values.length];
                for (int j = 0; j < values.length; j++) {
                    row[j] = values[j];
                }
                rows.add(row);
            }
            data = rows.toArray(new Object[0][]);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }
    }

    @Override
    public void update(Entita entita, int eventType) {
        if (eventType == Labirinto.OGGETTO_AGGIUNTO) {
            // Aggiungi graficamente l'oggetto al labirinto
            if (entita.getTipo() == 'R') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_red.png").toString().substring(5)).getImage());
            } else if (entita.getTipo() == 'Y') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_yellow.png").toString().substring(5)).getImage());
            } else if (entita.getTipo() == 'C') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_cyan.png").toString().substring(5)).getImage());
            } else if (entita.getTipo() == 'G') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_green.png").toString().substring(5)).getImage());
            }

        } else if (eventType == Labirinto.OGGETTO_RIMOSSO) {
            // Rimuovi graficamente l'oggetto dal labirinto
            ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
        }


    }


}
