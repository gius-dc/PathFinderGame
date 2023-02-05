import com.formdev.flatlaf.FlatLightLaf;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JSlider volumeSlider;
    private JLabel muteButton;
    private JPanel topPanel;
    MyTableModel modelLevelRank;
    Labyrinth l;
    int row = 16, col = 16;
    Boolean firstRun = true;
    RobotState state;
    MyTableModel fullRankModel;
    int level = 0, maxLevels = 5;
    int[] scores = new int[maxLevels];
    Level livello;

    Caretaker caretaker = null;
    File fileClassifica;
    String inputName, inputSurname;
    RankGUI rankGUI = null;

    private Mediator mediator;

    public MainGUI() throws IllegalAccessException {
        setContentPane(mainPanel);
        setTitle("Labirinto");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);


        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/robot.png").getPath()));
        modelLevelRank = new MyTableModel(new String[]{"Nome", "Cognome", "Punteggio"});

        try {
            prepareFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        if (!fileClassifica.exists()) {
            try {
                fileClassifica.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        prepareLabyrinth();
        updateRank();
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
                        level++;
                        if (level < maxLevels) {
                            try {
                                prepareLabyrinth();
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                            nextLevelButton.setEnabled(false);
                            avviaButton.setEnabled(true);
                            Icon imgIcon = new ImageIcon(getClass().getResource("/img/blank.png").toString().substring(5));
                            labelImg.setIcon(imgIcon);
                            labelImg.setIcon(null);
                            stateLabel.setText("Livello " + (level + 1) + " - Avvia  per iniziare");
                        } else {
                            nextLevelButton.setEnabled(false);
                            avviaButton.setEnabled(false);
                            newGameButton.setEnabled(true);
                            stateLabel.setText("Complimenti, hai finito tutti i livelli! ☺");
                            updateRank();
                            try {
                                prepareLabyrinth();
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }


                        }

                    }
                });
            }
        });

        muteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (volumeSlider.getValue() != 0) {
                    volumeSlider.setValue(0);
                } else {
                    volumeSlider.setValue(50);
                }

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
                rankGUI.showRank(fullRankModel, maxLevels);

                JDialog frame2 = new JDialog(rankGUI, "Classifica", true);
                frame2.setContentPane(rankGUI.getContentPane());
                frame2.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                frame2.setSize(500, 400);
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

                try {
                    newGame();
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        String filePath = getClass().getResource("/sounds/pathfinderTrack.wav").getPath();

        try {
            File audioFile = new File(filePath);
            final AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, AudioSystem.NOT_SPECIFIED);
            final SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);
            audioLine.start();

            final FloatControl volume = (FloatControl) audioLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    float gain = (float) volumeSlider.getValue() / 100;
                    float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
                    volume.setValue(dB);

                    if (volumeSlider.getValue() != 0) {
                        muteButton.setIcon(new ImageIcon(getClass().getResource("/img/volume.png").toString().substring(5)));
                    } else {
                        muteButton.setIcon(new ImageIcon(getClass().getResource("/img/volume_mute.png").toString().substring(5)));
                    }
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead = 0;
                    while (true) {
                        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {
                            while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
                                audioLine.write(buffer, 0, bytesRead);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void newGame() throws IllegalAccessException {
        int exists[] = checkIfGameAlreadyExists(inputName, inputSurname);
        if (exists[0] == 0) {
            Object[] row = new Object[8];
            row[0] = inputName;
            row[1] = inputSurname;
            row[2] = '?';
            row[3] = '?';
            row[4] = '?';
            row[5] = '?';
            row[6] = '?';
            row[7] = '?';
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
                    Object[] row = new Object[8];
                    row[0] = inputName;
                    row[1] = inputSurname;
                    row[2] = '?';
                    row[3] = '?';
                    row[4] = '?';
                    row[5] = '?';
                    row[6] = '?';
                    row[7] = '?';
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


    public void prepareLabyrinth() throws IllegalAccessException {
        Level.Builder builder = new Level.Builder(16, 16);
        System.out.println(level);
        if(level == maxLevels)
        {
            builder.addVerticalWall(0,3,11);
            builder.addHorizontalWall(3,1,4);
            builder.addHorizontalWall(7,1,3);
            builder.addHorizontalWall(11,1,4);
            builder.addVerticalWall(6,3,11);
            builder.addVerticalWall(7,4,5);
            builder.addVerticalWall(8,6,7);
            builder.addVerticalWall(9,8,9);
            builder.addVerticalWall(10,3,11);
            builder.addVerticalWall(12,3,11);
            builder.addHorizontalWall(3,13,14);
            builder.addHorizontalWall(11,13,14);
            builder.addVerticalWall(15,4,10);
            builder.setRobotStartXY(13,8);
            drawLabyrinth(builder.build());
        }
        else {
            if (level == 0) {
                builder.addWalls();
                builder.addVerticalWall(3, 2, 9);
                builder.addVerticalWall(7, 8, 14);
                builder.addHorizontalWall(3, 7, 14);
                builder.addHorizontalWall(10, 11, 13);
                builder.setExit(12, 0);
                builder.setRobotStartXY(1, 14);
            } else if (level == 1) {
                builder.addWalls();
                builder.addVerticalWall(6, 1, 7);
                builder.addVerticalWall(9, 2, 10);
                builder.addVerticalWall(9, 2, 10);
                builder.addHorizontalWall(9, 1, 5);
                builder.addHorizontalWall(7, 11, 14);
                builder.addHorizontalWall(4, 2, 4);
                builder.addVerticalWall(3, 11, 14);
                builder.addHorizontalWall(13, 7, 12);
                builder.setExit(13, 15);
                builder.setRobotStartXY(1, 1);
            } else if (level == 2) {
                builder.addWalls();
                builder.setExit(0, 8);
                builder.addVerticalWall(8, 2, 12);
                builder.addHorizontalWall(12, 4, 12);
                builder.addHorizontalWall(14, 3, 4);
                builder.addHorizontalWall(13, 13, 14);
                builder.addVerticalWall(3, 10, 12);
                builder.addDotWall(2, 9);
                builder.addHorizontalWall(7, 1, 4);
                builder.addDotWall(7, 7);
                builder.addVerticalWall(6, 5, 6);
                builder.addDotWall(5, 5);
                builder.addDotWall(10, 13);
                builder.addDotWall(9, 12);
                builder.addDotWall(9, 9);
                builder.addHorizontalWall(11, 5, 6);
                builder.addDotWall(4, 13);
                builder.addDotWall(4, 10);
                builder.addHorizontalWall(3, 11, 12);
                builder.addHorizontalWall(1, 10, 13);
                builder.addVerticalWall(14, 3, 9);
                builder.addDotWall(7, 13);
                builder.addVerticalWall(4, 1, 4);
                builder.addDotWall(1, 2);
                builder.addDotWall(2, 3);
                builder.addDotWall(3, 4);
                builder.addVerticalWall(9, 5, 7);
                builder.addHorizontalWall(5, 10, 12);
                builder.addHorizontalWall(6, 10, 12);
                builder.addDotWall(3, 13);
                builder.setRobotStartXY(14, 8);
            } else if (level == 3) {
                builder.addWalls();
                builder.addHorizontalWall(11, 1, 11);
                builder.addHorizontalWall(8, 1, 3);
                builder.addVerticalWall(3, 3, 8);
                builder.addVerticalWall(6, 1, 4);
                builder.addVerticalWall(6, 7, 8);
                builder.addVerticalWall(8, 2, 8);
                builder.addHorizontalWall(8, 6, 12);
                builder.addVerticalWall(12, 3, 8);
                builder.addVerticalWall(10, 1, 6);
                builder.setExit(0, 0);
                builder.setRobotStartXY(14, 1);
            } else if (level == 4) {
                builder.addWalls();
                builder.addVerticalWall(11, 1, 10);
                builder.addDotWall(1, 12);
                builder.addDotWall(2, 14);
                builder.addDotWall(3, 12);
                builder.addDotWall(4, 14);
                builder.addDotWall(5, 12);
                builder.addDotWall(6, 14);
                builder.addDotWall(7, 12);
                builder.addDotWall(8, 14);
                builder.addDotWall(9, 12);
                builder.addDotWall(10, 14);
                builder.addHorizontalWall(10, 3, 10);
                builder.addVerticalWall(8, 1, 2);
                builder.addVerticalWall(8, 4, 5);
                builder.addVerticalWall(8, 7, 8);
                builder.addVerticalWall(6, 2, 9);
                builder.addVerticalWall(3, 2, 8);
                builder.addVerticalWall(3, 10, 13);
                builder.addVerticalWall(6, 12, 14);
                builder.addVerticalWall(9, 10, 13);
                builder.addVerticalWall(12, 12, 14);
                builder.addHorizontalWall(2, 2, 4);
                builder.setExit(0, 15);
                builder.setRobotStartXY(1, 10);

            }


            livello = builder.build();
            if (l == null) {
                try {
                    l = new Labyrinth(livello);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                l = l.clone();
                l.resetLabyrinth(livello);
            }


            Mediator mediator = new Mediator(l, this);
            this.setMediator(mediator);

            drawLabyrinth(livello);

            mediator.addObserver(this);

            updateRank();
            //if (avviaButton.isEnabled()) {
                stateLabel.setText("Livello " + (level + 1) + " - Avvia per iniziare");
            //}
            scores[level] = 0;

        }


    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    private void updateRank() {
        while (modelLevelRank.getRowCount() > 0) {
            modelLevelRank.removeRow(0);
        }

        if (level == maxLevels) {
            rankLabel.setText("Classifica finale");
        } else {
            rankLabel.setText("Classifica - Livello " + (level + 1));
        }

        for (int i = 0; i < fullRankModel.getRowCount(); i++) {
            if (!fullRankModel.getValueAt(i, level + 2).toString().equals("?")) {
                modelLevelRank.addRow(new Object[]{fullRankModel.getValueAt(i, 0), fullRankModel.getValueAt(i, 1), fullRankModel.getValueAt(i, level + 2)});
            }
        }

        modelLevelRank.sortByColumn(2);
        table1.setModel(modelLevelRank);
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
                    ImagePanel panel = new ImagePanel(new ImageIcon(getClass().getResource("/img/sand.png").toString().substring(5)).getImage());
                    if (checker == Color.BLACK) {
                        panel.setImage(new ImageIcon(getClass().getResource("/img/wall.png").toString().substring(5)).getImage());
                    }
                    panel.setMaximumSize(new Dimension(30, 30));
                    panel.setBackground(Color.WHITE);
                    labirintoPanel.add(panel);
                }
            }

            firstRun = false;
        } else {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    if (labirinto[x][y] == '#') {
                        setImagePanelXY("/img/wall.png", x, y);
                    } else {
                        setImagePanelXY("/img/sand.png", x, y);

                    }
                }
            }
        }
        setImagePanelXY("/img/robot.png", l.getRobotX(), l.getRobotY());
    }

    public void startLabyrinth() {
        newGameButton.setEnabled(false);
        int differenceSizeMemento = 0;
        labirintoPanel.setBackground(Color.WHITE);
        char lab[][] = mediator.getLabyrinth();


        Icon imgIcon = new ImageIcon(getClass().getResource("/img/loading30x30.gif").toString().substring(5));
        labelImg.setIcon(imgIcon);
        labelImg.setVisible(true);
        stateLabel.setText("Labirinto in esecuzione...");

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (livello.getLabyrinth()[i][j] != '#') {
                    setImagePanelXY("/img/sand.png", i, j);
                } else {
                    setImagePanelXY("/img/wall.png", i, j);
                }
            }
        }

        while (!l.iterate()) {
            if (caretaker != null) {
                if (caretaker.sizeMemento() > 1) {
                    setImagePanelXY("/img/sand.png", caretaker.getMemento(caretaker.sizeMemento() - 2).getX(), caretaker.getMemento(caretaker.sizeMemento() - 2).getY());


                    if (caretaker.sizeMemento() > 3 && (caretaker.sizeMemento() - differenceSizeMemento == 2)) {
                        setImagePanelXY("/img/sand.png", caretaker.getMemento(caretaker.sizeMemento() - 3).getX(), caretaker.getMemento(caretaker.sizeMemento() - 3).getY());

                    }

                }
                differenceSizeMemento = caretaker.sizeMemento();
            }


            state = mediator.getRobot().getState();
            System.out.println(state);
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
            //    setImagePanelXY("/img/sand.png", memento.getX(), memento.getY());
            //}

            caretaker = mediator.getCaretaker();
            setImagePanelXY("/img/robot.png", l.getRobotX(), l.getRobotY());

            try {
                Thread.sleep(150);
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
                    setImagePanelXY("/img/wall.png", x, y);
                } else {
                    setImagePanelXY("/img/sand.png", x, y);
                }
            }
        }


        // Il programma visualizza il percorso che il robot ha effettuato (Memento)
        for (int i = 0; i < caretaker.sizeMemento(); i++) {
            Memento memento = caretaker.getMemento(i);
            l.getRobot().restoreFromMemento(memento);
            setImagePanelXY("/img/footprints.png", l.getRobot().getX(), l.getRobot().getY());
        }


        for (int i = 0; i < caretaker.sizeMemento(); i++) {
            if (i > 0 && ((caretaker.getMemento(i).getX() != caretaker.getMemento(i - 1).getX()) || (caretaker.getMemento(i).getY() != caretaker.getMemento(i - 1).getY()))) {
                scores[level]++;
            }
        }
        //scores[level] = caretaker.sizeMemento();
        stateLabel.setText("Il robot ha raggiunto la destinazione con " + scores[level] + " passi!");
        if (level < maxLevels) {
            nextLevelButton.setEnabled(true);
        }


        for (int i = 0; i < fullRankModel.getRowCount(); i++) {
            if (fullRankModel.getValueAt(i, 0).equals(inputName) && fullRankModel.getValueAt(i, 1).equals(inputSurname)) {
                fullRankModel.setValueAt(Integer.toString(scores[level]), i, level + 2);
                    /*if (level == 1) {
                        fullRankModel.setValueAt(Integer.toString((scores[0] + scores[1])), i, level + 3);
                    }*/
                //sorter.sort();

                if (level == maxLevels - 1) {
                    int totalScore = 0;
                    for (int j = 2; j < fullRankModel.getColumnCount() - 1; j++) {
                        totalScore = totalScore + Integer.parseInt(fullRankModel.getValueAt(i, j).toString());
                    }

                    fullRankModel.setValueAt(Integer.toString(totalScore), i, maxLevels + 2);
                }
                try {
                    fullRankModel.saveToFile(fileClassifica);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        updateRank();
        newGameButton.setEnabled(true);
    }


    public static void main(String[] args) throws IllegalAccessException {


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

        modelLevelRank = new MyTableModel(new String[]{"Nome", "Cognome", "Punteggio"});
        //table1 = new JTable();
        while (!fileCreatedOrRead) {
            if (createFile()) {
                String[] columnsText = new String[maxLevels + 3];
                columnsText[0] = "Nome";
                columnsText[1] = "Cognome";
                for (int i = 2; i < maxLevels + 2; i++) {
                    columnsText[i] = ("Passi LV." + (i - 1));
                }
                columnsText[maxLevels + 2] = "Totale passi";
                fullRankModel = new MyTableModel(columnsText);
                //table1.setModel(modelLevelRank);
                fileCreatedOrRead = true;
            } else {
                String[] columnsText = new String[maxLevels + 3];
                for (int i = 2; i < maxLevels + 2; i++) {
                    columnsText[i] = ("Passi LV." + i);
                }
                columnsText[maxLevels + 2] = "Totale passi";
                fullRankModel = new MyTableModel(columnsText);
                try {
                    fullRankModel.loadFromFile(fileClassifica);
                    //table1.setModel(modelLevelRank);
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
        if (eventType == Labyrinth.OBJECT_ADDED) {
            // Aggiungi graficamente l'oggetto al labirinto
            if (entita.getColor() == 'R') {
                setImagePanelXY("/img/red_stone.png", entita.getX(), entita.getY());
            } else if (entita.getColor() == 'Y') {
                setImagePanelXY("/img/yellow_lemon.png", entita.getX(), entita.getY());
            } else if (entita.getColor() == 'C') {
                setImagePanelXY("/img/cyan_bucket.png", entita.getX(), entita.getY());
            } else if (entita.getColor() == 'G') {
                setImagePanelXY("/img/green_cactus.png", entita.getX(), entita.getY());
            }

        } else if (eventType == Labyrinth.OBJECT_REMOVED) {
            // Rimuovi graficamente l'oggetto dal labirinto
            setImagePanelXY("/img/sand.png", entita.getX(), entita.getY());
        }
    }

}