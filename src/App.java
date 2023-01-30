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
    Labirinto l;
    int row = 16, col = 16;
    Boolean firstRun = true;
    RobotState state;
    MyTableModel model;

    Caretaker caretaker = null;
    File fileClassifica;
    TableRowSorter<TableModel> sorter;

    public App() {
        setContentPane(mainPanel);
        setTitle("Labirinto");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800,700);
        setVisible(true);






        //File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "classifica.csv");
        if (!fileClassifica.exists()) {
            try {
                fileClassifica.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        nuovoLab();
        char lab[][] = l.getLabyrinth();
        Color checker;
        labirintoPanel.setLayout(new GridLayout(row, col));
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (lab[x][y] == '#') {
                    checker = Color.BLACK;
                } else {
                    checker = Color.WHITE;
                }
                //JPanel panel = new JPanel();
                ImagePanel panel = new ImagePanel(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                if(checker == Color.BLACK)
                {
                    panel.setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                }
                panel.setPreferredSize(new Dimension(400 / row, 400 / col));
                panel.setBackground(Color.WHITE);
                labirintoPanel.add(panel);
            }
        }
        firstRun = false;
        setSize(900, 600);

        avviaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {

                        labirintoPanel.setBackground(Color.WHITE);
                        avviaButton.setEnabled(false);
                        caretaker = new Caretaker();

                        Color checker;
                        nuovoLab();
                        char lab[][] = l.getLabyrinth();

                        if(firstRun)
                        {
                            /*labirintoPanel.setLayout(new GridLayout(row, col));
                            for (int x = 0; x < 16; x++) {
                                for (int y = 0; y < 16; y++) {
                                    if (lab[x][y] == '#') {
                                        checker = Color.BLACK;
                                    } else {
                                        checker = Color.WHITE;
                                    }
                                    //JPanel panel = new JPanel();
                                    ImagePanel panel = new ImagePanel(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                                    if(checker == Color.BLACK)
                                    {
                                        panel.setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                                    }
                                    panel.setPreferredSize(new Dimension(400 / row, 400 / col));
                                    panel.setBackground(Color.WHITE);
                                    labirintoPanel.add(panel);
                                }
                            }
                            firstRun = false;*/
                        }
                        else
                        {
                            for (int x = 0; x < 16; x++) {
                                for (int y = 0; y < 16; y++) {
                                    if (lab[x][y] == '#') {
                                        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                                    } else {
                                        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                                    }
                                }
                            }
                        }

                        Icon imgIcon = new ImageIcon(getClass().getResource("/img/loading30x30.gif").toString().substring(5));
                        labelImg.setIcon(imgIcon);
                        stateLabel.setText("Labirinto in esecuzione.");
                        while (l.iterate())
                        {
                            state = l.getRobot().getState();
                            if (state instanceof PursuitState) {
                                // esegui l'azione per lo stato pursuit
                                stateRobotLabel.setText("Stato robot: pursuit");
                            }
                            else if (state instanceof SeekState) {
                                // esegui l'azione per lo stato seek
                                stateRobotLabel.setText("Stato robot: seek");
                            }
                            else if (state instanceof FleeState) {
                                // esegui l'azione per lo stato flee
                                stateRobotLabel.setText("Stato robot: flee");
                            }
                            else if (state instanceof EvadeState) {
                                // esegui l'azione per lo stato evade
                                stateRobotLabel.setText("Stato robot: evade");
                            }

                            if(caretaker.sizeMemento() > 0)
                            {
                                Memento memento = caretaker.getMemento(caretaker.sizeMemento()-1);

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

                        Object[] row = new Object[2];
                        String input = JOptionPane.showInputDialog("Inserisci il nome del robot da salvare in classifica");
                        if(input == "" || input == null)
                        {
                            row[0] = "(senza nome)";
                            row[1] = caretaker.sizeMemento();
                        }
                        else {
                            row[0] = input;
                            row[1] = caretaker.sizeMemento();
                        }

                        model.addRow(row);
                        //sorter.sort();



                        avviaButton.setEnabled(true);
                        try {
                            model.saveToFile(fileClassifica);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //saveTableData(table1,file);
                        //saveTableData(model,file);
                        // Aggiornare l'interfaccia grafica qui, utilizzando il risultato ottenuto
                        //updateGUI();
                    }
                });
            }
        });
    }
    /*
    public List<Oggetto> itera()
    {

    }
*/
    public void updateGUI()
    {

    }

    public void start()
    {





    }

    public void drawLabyrinth() {

    }

    public void nuovoLab()
    {
        l = new Labirinto("l");
        l.addObserver(this);
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        App myApp = new App();
    }

    private void createUIComponents() throws URISyntaxException, MalformedURLException {
        fileClassifica = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "classifica.csv");
        if (!fileClassifica.exists()) {
            try {
                fileClassifica.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            model = new MyTableModel();
            table1 = new JTable(model);
        }
        else {

            model = new MyTableModel();
            try {
                model.loadFromFile(fileClassifica);
            } catch (IOException e) {
                e.printStackTrace();
            }
            table1 = new JTable(model);

            //table1 = new JTable();
            //loadTableData(table1,file);
            //model = (MyTableModel) table1.getModel();
        }

        sorter = new TableRowSorter<>(table1.getModel());
        table1.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    public class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"Nome robot", "Numero passi"};
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
            } else if (entita.getTipo()  == 'G') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_green.png").toString().substring(5)).getImage());
            }

        } else if (eventType == Labirinto.OGGETTO_RIMOSSO) {
            // Rimuovi graficamente l'oggetto dal labirinto
            ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
        }


    }




}
