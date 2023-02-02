import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainGUI extends JFrame implements Observer {
    private JPanel mainPanel;
    private JPanel labirintoPanel;

    private JButton avviaButton;
    private JLabel stateLabel;
    private JTable table1;
    private JLabel labelImg;
    private JLabel stateRobotLabel;
    private JButton nextLevelButton;
    private JButton showRankButton;
    private JButton newGameButton;
    private JLabel rankLabel;
    private JPanel topPanel;
    MyTableModel modelTop3Rank;
    Labirinto l;
    int row = 16, col = 16;
    Boolean firstRun = true;
    RobotState state;
    MyTableModel fullRankModel;
    int level = 0;
    int[] scores = new int[3];

    Caretaker caretaker = null;
    File fileClassifica;
    TableRowSorter<TableModel> sorter;
    String inputName, inputSurname;
    RankGUI rankGUI = null;

    public MainGUI() {
        setContentPane(mainPanel);
        setTitle("Labirinto");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);


        //nextLevelButton.setVisible(false);
        stateLabel.setText("Livello " + (level + 1) + " - Avvia  per iniziare");
        modelTop3Rank = new MyTableModel(new String[]{"Nome", "Cognome", "Punteggio"});
        //toolBar1.add(avviaButton);
        try {
            prepareFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        //File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "classifica.csv");
        if (!fileClassifica.exists()) {
            try {
                fileClassifica.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        prepareLabyrinth();
        updateTop3Rank();
        //char lab[][] = l.getLabyrinth();
        Color checker;

        firstRun = false;
        setSize(900, 600);
        setLocationRelativeTo(null);
        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (level < 2) {
                            level++;
                            prepareLabyrinth();
                            nextLevelButton.setEnabled(false);
                            avviaButton.setEnabled(true);
                            Icon imgIcon = new ImageIcon(getClass().getResource("/img/blank.png").toString().substring(5));
                            labelImg.setIcon(imgIcon);
                            labelImg.setIcon(null);
                            stateLabel.setText("Livello " + (level + 1) + " - Avvia  per iniziare");

                        }

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

                        avviaButton.setEnabled(false);
                        startLabyrinth();
                    }
                });
            }
        });


            showRankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(rankGUI == null){
                    rankGUI = new RankGUI();
                }
                rankGUI.showRank(fullRankModel);

                JDialog frame2 = new JDialog(rankGUI, "Classifica", true);
                frame2.setContentPane(rankGUI.getContentPane());
                frame2.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                frame2.setSize(300, 400);
                frame2.setLocationRelativeTo(null);
                frame2.setVisible(true);

            }
        });

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGame();
            }
        });


    }

    public void newGame() {
        inputName = JOptionPane.showInputDialog("Inserisci il nome del robot:");

        inputSurname = JOptionPane.showInputDialog("Inserisci il cognome del robot:");

        int rowIndex = fullRankModel.searchRow(inputName, inputSurname);
        System.out.println(rowIndex);
        if (rowIndex != -1) {
            //TODO: gestire il caso in cui il robot ha lo stesso nome ma ha finito tutte le partite
            int columnIndex = fullRankModel.searchColumn(rowIndex, "?");
            if (columnIndex != -1) {
                int result = JOptionPane.showConfirmDialog(this, ("Il robot " + inputName + " " + inputSurname + " già esiste ed ha una partita in sospeso fino al livello " + (columnIndex - 1) + ". Desideri riprendere quella partita?"), "Partita in sospeso già esistente",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    level = (columnIndex - 2);
                    //nextLevelButton.setVisible(false);
                    avviaButton.setVisible(true);
                    prepareLabyrinth();
                } else if (result == JOptionPane.NO_OPTION) {

                    Object[] options = {"Elimina e avvia con lo stesso nome", "Avvia con un altro nome"};
//
                    result = JOptionPane.showOptionDialog(this, ("Se non vuoi riprendere la partita, scegli in che modo vuoi iniziare una nuova partita. Puoi avviare una nuova partita di " + inputName + " " + inputSurname + " eliminando quella corrente, oppure avviare con un altro nome"), "Scegli",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, options, null);

                    if (result == JOptionPane.YES_OPTION) {
                        fullRankModel.removeRow(rowIndex);
                        Object[] row = new Object[5];
                        if (inputName.equals("") || inputName.equals(null)) {
                            row[0] = "(senza nome)";
                        } else {
                            row[0] = inputName;
                        }
                        if (inputSurname.equals("") || inputSurname.equals(null)) {
                            row[1] = "(senza cognome)";
                        } else {
                            row[1] = inputSurname;
                        }
                        row[2] = '?';
                        row[3] = '?';
                        row[4] = '?';
                        fullRankModel.addRow(row);
                        level = 0;
                    } else if (result == JOptionPane.NO_OPTION) {
                        newGame();
                        return;
                    }
                }
            }
        } else {
            Object[] row = new Object[5];
            if (inputName.equals("") || inputName.equals(null)) {
                row[0] = "(senza nome)";
            } else {
                row[0] = inputName;
            }
            if (inputSurname.equals("") || inputSurname.equals(null)) {
                row[1] = "(senza cognome)";
            } else {
                row[1] = inputSurname;
            }
            row[2] = '?';
            row[3] = '?';
            row[4] = '?';
            fullRankModel.addRow(row);
            //sorter.sort();
        }

        avviaButton.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Puoi avviare la partita cliccando 'Avvia' in ogni livello",
                "Informazione", JOptionPane.INFORMATION_MESSAGE);
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
            builder.setRobotStartXY(1, 14);

        } else if (level == 1) {
            builder.aggiungiPareti();
            builder.aggiungiPareteVerticale(6, 1, 7);
            builder.aggiungiPareteVerticale(9, 2, 10);
            builder.aggiungiPareteVerticale(9, 2, 10);
            builder.aggiungiPareteOrizzontale(9, 1, 5);
            builder.aggiungiPareteOrizzontale(7, 11, 14);
            builder.aggiungiPareteOrizzontale(4, 2, 4);
            builder.aggiungiPareteVerticale(3, 11, 14);
            builder.aggiungiPareteOrizzontale(13, 7, 12);
            builder.impostaPosizionePortaUscita(13, 15);
            builder.setRobotStartXY(1, 1);
        } else if (level == 2) {
            builder.aggiungiPareti();
            // costruirò un livello
            builder.impostaPosizionePortaUscita(13, 15);
            builder.setRobotStartXY(1, 1);
        }


        Level livello = builder.build();
        drawLabyrinth(livello);
        try {
            l = new Labirinto(livello);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        l.addObserver(this);

        updateTop3Rank();
    }

    private void updateTop3Rank() {
        while (modelTop3Rank.getRowCount() > 0) {
            modelTop3Rank.removeRow(0);
        }
        for (int i = 0; i < fullRankModel.getRowCount(); i++) {
            if (!fullRankModel.getValueAt(i, level + 2).toString().equals("?")) {
                modelTop3Rank.addRow(new Object[]{fullRankModel.getValueAt(i, 0), fullRankModel.getValueAt(i, 1), fullRankModel.getValueAt(i, level + 2)});
            }
        }

        modelTop3Rank.sortByColumn(2);
/*
        for (int i = modelTop3Rank.getRowCount(); i >= 3; i--) {
            modelTop3Rank.removeRow(i);
        }
*/
        table1.setModel(modelTop3Rank);
        rankLabel.setText("Classifica livello " + (level + 1));
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
        ((ImagePanel) labirintoPanel.getComponent((l.getRobotX() * 16) + l.getRobotY())).setImage(new ImageIcon(getClass().getResource("/img/circle_gray.png").toString().substring(5)).getImage());
    }

    public void startLabyrinth() {
        ((ImagePanel) labirintoPanel.getComponent((l.getRobotX() * 16) + l.getRobotY())).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
        labirintoPanel.setBackground(Color.WHITE);
        caretaker = new Caretaker();
        Color checker;
        char lab[][] = l.getLabyrinth();


        Icon imgIcon = new ImageIcon(getClass().getResource("/img/loading30x30.gif").toString().substring(5));
        labelImg.setIcon(imgIcon);
        labelImg.setVisible(true);
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
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            caretaker.addMemento(l.getRobot().saveToMemento()); // salva il nuovo stato del robot (design pattern Memento)
        }


        imgIcon = new ImageIcon(getClass().getResource("/img/finish.png").toString().substring(5));
        labelImg.setIcon(imgIcon);
        stateRobotLabel.setText("");


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
        stateLabel.setText("Il robot ha raggiunto la destinazione.");
        if (level < 2) {
            nextLevelButton.setEnabled(true);
        }


        for (int i = 0; i < fullRankModel.getRowCount(); i++) {
            if (fullRankModel.getValueAt(i, 0).equals(inputName) && fullRankModel.getValueAt(i, 1).equals(inputSurname)) {
                fullRankModel.setValueAt(Integer.toString(scores[level]), i, level + 2);
                    /*if (level == 1) {
                        fullRankModel.setValueAt(Integer.toString((scores[0] + scores[1])), i, level + 3);
                    }*/
                //sorter.sort();
                try {
                    fullRankModel.saveToFile(fileClassifica);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        updateTop3Rank();

        for (int i = 0; i < fullRankModel.getRowCount(); i++) {
            System.out.println("--------------");
            for (int j = 0; j < fullRankModel.getColumnCount(); j++) {
                System.out.print(fullRankModel.getValueAt(i, j) + "   ");
            }
            System.out.println();
        }
    }


    public static void main(String[] args) {
        FlatLightLaf.setup();
        MainGUI myMainGUI = new MainGUI();
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

    private void prepareFile() throws URISyntaxException, MalformedURLException {
        Boolean fileCreatedOrRead = false;
        modelTop3Rank = new MyTableModel(new String[]{"Nome", "Cognome", "Punteggio"});
        //table1 = new JTable();
        while (!fileCreatedOrRead) {
            if (createFile()) {
                fullRankModel = new MyTableModel(new String[]{"Nome", "Cognome", "Passi LV.1", "Passi LV.2", "Totale passi"});
                //table1.setModel(modelTop3Rank);
                fileCreatedOrRead = true;
            } else {
                fullRankModel = new MyTableModel(new String[]{"Nome", "Cognome", "Passi LV.1", "Passi LV.2", "Totale passi"});
                try {
                    fullRankModel.loadFromFile(fileClassifica);
                    //table1.setModel(modelTop3Rank);
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
    }


    @Override
    public void update(Oggetto entita, int eventType) {
        if (eventType == Labirinto.OGGETTO_AGGIUNTO) {
            // Aggiungi graficamente l'oggetto al labirinto
            if (entita.getColor() == 'R') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_red.png").toString().substring(5)).getImage());
            } else if (entita.getColor() == 'Y') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_yellow.png").toString().substring(5)).getImage());
            } else if (entita.getColor() == 'C') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_cyan.png").toString().substring(5)).getImage());
            } else if (entita.getColor() == 'G') {
                ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_green.png").toString().substring(5)).getImage());
            }

        } else if (eventType == Labirinto.OGGETTO_RIMOSSO) {
            // Rimuovi graficamente l'oggetto dal labirinto
            ((ImagePanel) labirintoPanel.getComponent((entita.getX() * 16) + entita.getY())).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
        }


    }


}
