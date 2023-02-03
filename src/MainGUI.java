import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
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
    int level = 0, maxLevels = 3;
    int[] scores = new int[3];
    Level livello;

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
                        if (level < maxLevels) {
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

                if (rankGUI == null) {
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
                Boolean showMessage = false;
                do {
                    if (showMessage) {
                        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                        JOptionPane.showMessageDialog(currentFrame, "Entrambi i campi non possono essere vuoti, riprova",
                                "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                    inputName = JOptionPane.showInputDialog("Inserisci il nome del robot:");
                    inputSurname = JOptionPane.showInputDialog("Inserisci il cognome del robot:");
                    showMessage = true;
                } while (inputName == null || inputName.equals("") || inputName.equals(" ") || inputSurname == null || inputSurname.equals("") || inputSurname.equals(" "));

                newGame();
            }
        });


    }

    public void newGame() {


        int exists[] = checkIfGameAlreadyExists(inputName, inputSurname);
        if (exists[0] == 0) {
            Object[] row = new Object[6];
            row[0] = inputName;
            row[1] = inputSurname;
            row[2] = '?';
            row[3] = '?';
            row[4] = '?';
            row[5] = '?';
            fullRankModel.addRow(row);
            level = 0;
        } else if (exists[0] == 1) {
            Object[] options = {"Elimina e inizia da zero", "Aggiungi 'nuovo' al nome", "Specifica nome"};
//
            int result = JOptionPane.showOptionDialog(this, ("Il robot " + inputName + " " + inputSurname + " già esiste ed ha completato tutti i livelli. Come desideri procedere? Puoi eliminare la partita attualmente salvata e iniziare da zero, creare una nuova partita aggiungendo '(nuovo)' alla fine del nome oppure creare una nuova partita specificando il nome."), "Scegli",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, null);


            if (result == JOptionPane.YES_OPTION) {
                fullRankModel.removeRow(exists[1]);
                newGame();
                return;
            } else if (result == JOptionPane.NO_OPTION) {

                inputName = inputName + " (nuovo)";
                newGame();
                return;
                /*
                Object[] row = new Object[5];
                row[0] = inputName;
                row[1] = inputSurname;
                row[2] = '?';
                row[3] = '?';
                row[4] = '?';
                fullRankModel.addRow(row);
                level = 0;
                */
            } else {
                newGameButton.doClick();
                return;
            }
        } else if (exists[0] == 2) {
            int result = JOptionPane.showConfirmDialog(this, ("Il robot " + inputName + " " + inputSurname + " già esiste ed ha una partita in sospeso fino al livello " + (exists[2] - 1) + ". Desideri riprendere quella partita?"), "Partita in sospeso già esistente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                level = (exists[2] - 2);
                //nextLevelButton.setVisible(false);
                avviaButton.setVisible(true);
                prepareLabyrinth();
            } else if (result == JOptionPane.NO_OPTION) {

                Object[] options = {"Elimina e inizia da zero", "Aggiungi 'nuovo' al nome", "Specifica nome"};
//
                result = JOptionPane.showOptionDialog(this, ("Se non vuoi riprendere la partita, scegli in che modo vuoi iniziare una nuova partita. Puoi avviare una nuova partita di " + inputName + " " + inputSurname + " eliminando quella corrente, creare una nuova partita aggiungendo '(nuovo)' al nome, oppure creare una nuova partita specificando il nome"), "Scegli",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, null);

                if (result == JOptionPane.YES_OPTION) {
                    fullRankModel.removeRow(exists[1]);
                    Object[] row = new Object[6];
                    row[0] = inputName;
                    row[1] = inputSurname;
                    row[2] = '?';
                    row[3] = '?';
                    row[4] = '?';
                    row[5] = '?';
                    fullRankModel.addRow(row);
                    level = 0;
                } else if (result == JOptionPane.NO_OPTION) {
                    inputName = inputName + " (nuovo)";
                    newGame();
                    return;
                } else {
                    newGameButton.doClick();
                    return;
                }
            }
        }

        prepareLabyrinth();
        avviaButton.setEnabled(true);
        JOptionPane.showMessageDialog(this, "Puoi avviare la partita cliccando 'Avvia' in ogni livello",
                "Informazione", JOptionPane.INFORMATION_MESSAGE);
    }

    /*public void newGameBAK() {
        inputName = JOptionPane.showInputDialog("Inserisci il nome del robot:");

        inputSurname = JOptionPane.showInputDialog("Inserisci il cognome del robot:");

        int rowIndex = fullRankModel.searchRow(inputName, inputSurname);
        System.out.println(rowIndex);
        if (rowIndex != -1) {

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

                    Object[] options = {"Elimina e inizia da zero", "Aggiungi 'nuovo' al nome", "Specifica nome"};
//
                    result = JOptionPane.showOptionDialog(this, ("Se non vuoi riprendere la partita, scegli in che modo vuoi iniziare una nuova partita. Puoi avviare una nuova partita di " + inputName + " " + inputSurname + " eliminando quella corrente, creare una nuova partita aggiungendo '(nuovo)' al nome, oppure creare una nuova partita specificando il nome"), "Scegli",
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
                        inputName = JOptionPane.showInputDialog("Inserisci il nome del robot:");
                        inputSurname = JOptionPane.showInputDialog("Inserisci il cognome del robot:");
                        newGame(inputName, inputSurname);
                        return;
                    }
                }
            } else {
                Object[] options = {"Elimina e inizia da zero", "Aggiungi 'nuovo' al nome", "Specifica nome"};
//
                int result = JOptionPane.showOptionDialog(this, ("Il robot " + inputName + " " + inputSurname + " già esiste ed ha completato tutti i livelli. Come desideri procedere? Puoi eliminare la partita attualmente salvata e iniziare da zero, creare una nuova partita aggiungendo '(nuovo)' alla fine del nome oppure creare una nuova partita specificando il nome."), "Scegli",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
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
                    inputName = inputName + " (nuovo)";
                    Object[] row = new Object[5];
                    row[0] = inputName;
                    row[1] = inputSurname;
                    row[2] = '?';
                    row[3] = '?';
                    row[4] = '?';
                    fullRankModel.addRow(row);
                    level = 0;
                } else {
                    inputName = JOptionPane.showInputDialog("Inserisci il nome del robot:");
                    inputSurname = JOptionPane.showInputDialog("Inserisci il cognome del robot:");
                    newGame(inputName, inputSurname);
                    return;
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

*/
    private int[] checkIfGameAlreadyExists(String name, String surname) {
        int rowIndex = fullRankModel.searchRow(inputName, inputSurname);
        int result[] = new int[maxLevels];
        result[0] = 0;
        if (rowIndex != -1) {
            int columnIndex = fullRankModel.searchColumn(rowIndex, "?");
            if (columnIndex != -1) {
                if (!(columnIndex == maxLevels + 2)) {
                    result[0] = 2;
                    result[1] = rowIndex;
                    result[2] = columnIndex;
                    return result;
                } else {
                    result[0] = 1;
                    result[1] = rowIndex;
                    return result;
                }

            } else {
                result[0] = 1;
                result[1] = rowIndex;
                return result;
            }
        } else {
            return result;
        }
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
            builder.aggiungiPareteOrizzontale(11, 1, 11);
            builder.aggiungiPareteOrizzontale(8, 1, 3);
            builder.aggiungiPareteVerticale(3, 3, 8);
            builder.aggiungiPareteVerticale(6, 1, 4);
            builder.aggiungiPareteVerticale(6, 7, 8);
            builder.aggiungiPareteVerticale(8, 2, 8);
            builder.aggiungiPareteOrizzontale(8, 6, 12);
            builder.aggiungiPareteVerticale(12, 3, 8);
            builder.aggiungiPareteVerticale(10, 1, 6);
            //builder.aggiungiPareteVerticale(, 2, 6);
            builder.impostaPosizionePortaUscita(0, 0);
            builder.setRobotStartXY(14, 1);
        }


        livello = builder.build();
        drawLabyrinth(livello);
        try {
            l = new Labirinto(livello);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        l.addObserver(this);

        updateTop3Rank();
        if (avviaButton.isEnabled()) {
            stateLabel.setText("Livello " + (level + 1) + " - Avvia per iniziare");
        }
        scores[level] = 0;
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
        rankLabel.setText("Classifica - Livello " + (level + 1));
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
                        setImagePanelXY("/img/square_black.png", x, y);
                    } else {
                        setImagePanelXY("/img/square_white.png", x, y);

                    }
                }
            }
        }
        setImagePanelXY("/img/circle_gray.png", l.getRobotX(), l.getRobotY());
    }


    public void startLabyrinth() {
        newGameButton.setEnabled(false);
        int differenceSizeMemento = 0;
        labirintoPanel.setBackground(Color.WHITE);
        char lab[][] = l.getLabyrinth();


        Icon imgIcon = new ImageIcon(getClass().getResource("/img/loading30x30.gif").toString().substring(5));
        labelImg.setIcon(imgIcon);
        labelImg.setVisible(true);
        stateLabel.setText("Labirinto in esecuzione...");

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (livello.getLabyrinth()[i][j] != '#') {
                    setImagePanelXY("/img/square_white.png", i, j);
                } else {
                    setImagePanelXY("/img/square_black.png", i, j);
                }
            }
        }

        while (l.iterate()) {
            if (caretaker != null) {
                if (caretaker.sizeMemento() > 1) {
                    setImagePanelXY("/img/square_white.png", caretaker.getMemento(caretaker.sizeMemento() - 2).getX(), caretaker.getMemento(caretaker.sizeMemento() - 2).getY());


                    if(caretaker.sizeMemento() > 3 && (caretaker.sizeMemento() - differenceSizeMemento == 2))
                    {
                        setImagePanelXY("/img/square_white.png", caretaker.getMemento(caretaker.sizeMemento() - 3).getX(), caretaker.getMemento(caretaker.sizeMemento() - 3).getY());

                    }

                }
                differenceSizeMemento = caretaker.sizeMemento();
            }



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


            //if (caretaker.sizeMemento() > 0) {
            //    Memento memento = caretaker.getMemento(caretaker.sizeMemento() - 1);
            //    setImagePanelXY("/img/square_white.png", memento.getX(), memento.getY());
            //}


            caretaker = l.getRobotCaretaker();
            System.out.println("DOPO " + l.getRobotX() + " " + l.getRobotY());
            setImagePanelXY("/img/circle_gray.png", l.getRobotX(), l.getRobotY());

            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            //caretaker.addMemento(l.getRobot().saveToMemento()); // salva il nuovo stato del robot (design pattern Memento)
        }


        imgIcon = new ImageIcon(getClass().getResource("/img/finish.png").toString().substring(5));
        labelImg.setIcon(imgIcon);
        stateRobotLabel.setText("");


        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (lab[x][y] == '#') {
                    setImagePanelXY("/img/square_black.png", x, y);
                } else {
                    setImagePanelXY("/img/square_white.png", x, y);
                }
            }
        }


        // Il programma visualizza il percorso che il robot ha effettuato (Memento)
        for (int i = 0; i < caretaker.sizeMemento(); i++) {
            Memento memento = caretaker.getMemento(i);
            l.getRobot().restoreFromMemento(memento);
            setImagePanelXY("/img/square_gray.png", l.getRobot().getX(), l.getRobot().getY());
        }


        for (int i = 0; i < caretaker.sizeMemento(); i++) {
            if (i > 0 && ((caretaker.getMemento(i).getX() != caretaker.getMemento(i - 1).getX()) || (caretaker.getMemento(i).getY() != caretaker.getMemento(i - 1).getY()))) {
                scores[level]++;
            }
        }
        //scores[level] = caretaker.sizeMemento();
        stateLabel.setText("Il robot ha raggiunto la destinazione con " + scores[level] + " passi!");
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
        newGameButton.setEnabled(true);
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
                fullRankModel = new MyTableModel(new String[]{"Nome", "Cognome", "Passi LV.1", "Passi LV.2", "Passi LV.3", "Totale passi"});
                //table1.setModel(modelTop3Rank);
                fileCreatedOrRead = true;
            } else {
                fullRankModel = new MyTableModel(new String[]{"Nome", "Cognome", "Passi LV.1", "Passi LV.2", "Passi LV.3", "Totale passi"});
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

    private void setImagePanelXY(String imagePath, int x, int y) {
        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource(imagePath).getPath()).getImage());
    }

    @Override
    public void update(ObjectEntity entita, int eventType) {
        if (eventType == Labirinto.OGGETTO_AGGIUNTO) {
            // Aggiungi graficamente l'oggetto al labirinto
            if (entita.getColor() == 'R') {
                setImagePanelXY("/img/circle_red.png", entita.getX(), entita.getY());
            } else if (entita.getColor() == 'Y') {
                setImagePanelXY("/img/circle_yellow.png", entita.getX(), entita.getY());
            } else if (entita.getColor() == 'C') {
                setImagePanelXY("/img/circle_cyan.png", entita.getX(), entita.getY());
            } else if (entita.getColor() == 'G') {
                setImagePanelXY("/img/circle_green.png", entita.getX(), entita.getY());
            }

        } else if (eventType == Labirinto.OGGETTO_RIMOSSO) {
            // Rimuovi graficamente l'oggetto dal labirinto
            setImagePanelXY("/img/square_white.png", entita.getX(), entita.getY());
        }
    }


}
