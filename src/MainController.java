import com.formdev.flatlaf.FlatLightLaf;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Questa classe è responsabile dell'avvio del programma e della gestione dell'interfaccia grafica
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */
public class MainController {

    private GUIView view;
    private LabyrinthGame labGame;
    private final int row = 16;
    private final int col = 16; // Dimensioni labirinto
    private Boolean firstRun = true;
    private CustomTableModel fullRankModel;
    private CustomTableModel modelLevelRank;
    private int currentLevel = 0;
    private final int maxLevels = 5;
    private int[] scores = new int[maxLevels];
    private Level level;
    private Caretaker caretaker = null;
    private File fileClassifica;
    private String inputName, inputSurname;
    private RankGUI rankGUI = null;
    private Mediator mediator;
    private FloatControl volume;

    /**
     * Costruttore che inizializza la GUI del gioco {@link GUIView} e prepara il file (motivo per il quale può generare un eccezione).
     */

    public MainController() {
        modelLevelRank = new CustomTableModel(new String[]{"Nome", "Cognome", "Punteggio"});
        prepareModelFile();
        if (!fileClassifica.exists()) {
            try {
                fileClassifica.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        view = new GUIView();
        prepareLabyrinth();
        updateRank();
        firstRun = false;
        view.getNextLevelButton().addActionListener(new NextLevelButtonHandler());
        view.getMuteButton().addMouseListener(new MuteButtonHandler());
        view.getStartButton().addActionListener(new StartButtonHandler());
        view.getShowRankButton().addActionListener(new ShowRankButtonHandler());
        view.getNewGameButton().addActionListener(new NewGameButtonHandler());
        startMusic();
        view.getVolumeSlider().addChangeListener(new VolumeSliderHandler());
        view.setSize(900,600);
        view.setLocationRelativeTo(null);
    }

    /**
     * Le classi interne sottostanti implementano la gestione dei pulsanti presenti nell'interfaccia.
     */

    private class NextLevelButtonHandler implements ActionListener {
        /**
         * Metodo che viene eseguito quando viene premuto il pulsante "Prossimo livello".
         * Aumenta il livello corrente e prepara il labirinto per il prossimo livello.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            currentLevel++;
            if (currentLevel < maxLevels) {
                prepareLabyrinth();
                view.getNextLevelButton().setEnabled(false);
                view.getStartButton().setEnabled(true);
                view.getLabelImg().setIcon(null);
                view.getStateLabel().setText("Livello " + (currentLevel + 1) + " - Avvia per iniziare");
            } else {
                view.getNextLevelButton().setEnabled(false);
                view.getStartButton().setEnabled(false);
                view.getNewGameButton().setEnabled(true);
                view.getStateLabel().setText("Complimenti, hai finito tutti i livelli! ☺");
                updateRank();
                prepareLabyrinth();
            }
        }
    }


    private class MuteButtonHandler extends MouseAdapter {
        /**
         * Metodo che viene eseguito quando viene fatto clic sul pulsante del volume.
         * Imposta il volume su 0 (muto) o su 50 in base allo stato corrente.
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (view.getVolumeSlider().getValue() != 0) {
                /* Quando viene cambiato il valore dello slider, il thread che gestisce l'audio in background
                   va a leggere il valore e cambia di conseguenza il volume */
                view.getVolumeSlider().setValue(0);
            } else {
                view.getVolumeSlider().setValue(50);
            }
        }
    }

    private class StartButtonHandler implements ActionListener {
        /**
         * Metodo che viene eseguito quando viene premuto il pulsante "Avvia".
         * Viene avviato un thread separato che esegue il metodo startLabyrinth.
         * Il metodo startLabyrinth() termina quando il labirinto termina la sua esecuzione (cioè quando il robot è arrivato a destinazione),
         * quindi è necessario eseguirlo in un thread separato per permettere al thread principale (che gestisce l'interfaccia grafica) di continuare
         * la sua esecuzione.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Executor executor = Executors.newSingleThreadExecutor();
            /* Poiché Runnable è un'interfaccia funzionale (ha un solo metodo astratto, "run"), l'implementazione di questa
               può essere semplificata con una funzione lambda. */
            executor.execute(() -> {
                view.getStartButton().setEnabled(false);
                startLabyrinth();
            });
        }
    }

    private class ShowRankButtonHandler implements ActionListener {
        /**
         * Metodo che viene eseguito quando viene premuto il pulsante "Classifica".
         * Avvia una finestra di dialogo dove il suo contenuto è definito nel form RankGUI.
         */
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
    }

    private class NewGameButtonHandler implements ActionListener {
        /**
         * Classe per gestire l'evento del pulsante Nuovo gioco.
         * Chiede all'utente di inserire il nome e il cognome del robot tramite finestra di dialogo e inizializza una nuova partita.
         * In caso entrambi i campi siano vuoti, verrà visualizzato un messaggio di errore.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean showMessage = false;
            do {
                if (showMessage) {
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor((Component) e.getSource());
                    JOptionPane.showMessageDialog(currentFrame, "I campi non possono essere vuoti, riprova",
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
                inputName = JOptionPane.showInputDialog("Inserisci il nome del robot:");
                if (inputName == null) {
                    return;
                }
                inputSurname = JOptionPane.showInputDialog("Inserisci il cognome del robot:");
                if (inputSurname == null) {
                    return;
                }
                showMessage = true;
            } while (inputName == null || inputName.equals("") || inputName.equals(" ") || inputSurname.equals("") || inputSurname.equals(" "));

            newGame();
        }
    }

    private class VolumeSliderHandler implements ChangeListener {
        /**
         * Questo metodo viene chiamato ogni volta che lo stato del volume cambia.
         * Il volume viene regolato in base alla posizione del cursore del volumeSlider e l'immagine del bottone mute viene cambiata
         * in base allo stato del volume (muto o non).
         */
        @Override
        public void stateChanged(ChangeEvent e) {
            float gain = (float) view.getVolumeSlider().getValue() / 100;
            float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
            volume.setValue(dB);

            if (view.getVolumeSlider().getValue() != 0) {
                view.getMuteButton().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/volume.png")).toString().substring(5)));
            } else {
                view.getMuteButton().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/volume_mute.png")).toString().substring(5)));
            }
        }
    }

    /**
     * Questo metodo viene utilizzato per riprodurre l'audio del gioco. La riproduzione viene eseguita in un thread separato
     * per poter implementare l'uso della lettura di un buffer in un ciclo che itera sempre.
     * Questo è stato fatto per rendere più responsivo il cambio volume.
     */
    private void startMusic() {
        String filePath = Objects.requireNonNull(getClass().getResource("/sounds/pathfinderTrack.wav")).getPath();

        try {
            File audioFile = new File(filePath);
            final AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, AudioSystem.NOT_SPECIFIED);
            final SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format);
            audioLine.start();

            volume = (FloatControl) audioLine.getControl(FloatControl.Type.MASTER_GAIN);

            /* Poiché Runnable è un'interfaccia funzionale (ha un solo metodo astratto, "run"), l'implementazione di questa
               può essere semplificata con una funzione lambda. */
            new Thread(() -> {
                int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                while (true) {
                    try (AudioInputStream audioStream1 = AudioSystem.getAudioInputStream(audioFile)) {
                        while ((bytesRead = audioStream1.read(buffer, 0, buffer.length)) != -1) {
                            audioLine.write(buffer, 0, bytesRead);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Questa funzione viene eseguita quando viene cliccato il pulsante "Nuova partita".
     * Si occupa di avviare una nuova partita, andando a verificare se il nome e cognome specificato dall'utente
     * esiste già in classifica e, in tal caso, gestire informando l'utente presentandogli alcune opzioni
     * per proseguire.
     */
    private void newGame() {
        int[] exists = checkIfGameAlreadyExists();
        if (exists[0] == 0) {
            Object[] row = new Object[maxLevels + 3];
            row[0] = inputName;
            row[1] = inputSurname;
            row[2] = '?';
            row[3] = '?';
            row[4] = '?';
            row[5] = '?';
            row[6] = '?';
            row[7] = '?';
            fullRankModel.addRow(row);
            currentLevel = 0;
        } else if (exists[0] == 1) {
            Object[] options = {"Elimina e inizia da zero", "Aggiungi 'nuovo' al nome", "Specifica nome"};
            int result = JOptionPane.showOptionDialog(view, ("Il robot " + inputName + " " + inputSurname + " già esiste ed ha completato tutti i livelli. Come desideri procedere? Puoi eliminare la partita attualmente salvata e iniziare da zero, creare una nuova partita aggiungendo '(nuovo)' alla fine del nome oppure creare una nuova partita specificando il nome."), "Scegli",
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
                view.getNewGameButton().doClick();
                return;
            }
        } else if (exists[0] == 2) {
            int result = JOptionPane.showConfirmDialog(view, ("Il robot " + inputName + " " + inputSurname + " già esiste ed ha una partita in sospeso fino al livello " + (exists[2] - 1) + ". Desideri riprendere quella partita?"), "Partita in sospeso già esistente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                currentLevel = (exists[2] - 2);
                //nextLevelButton.setVisible(false);
                view.getStartButton().setVisible(true);
                prepareLabyrinth();
            } else if (result == JOptionPane.NO_OPTION) {

                Object[] options = {"Elimina e inizia da zero", "Aggiungi 'nuovo' al nome", "Specifica nome"};
                result = JOptionPane.showOptionDialog(view, ("Se non vuoi riprendere la partita, scegli in che modo vuoi iniziare una nuova partita. Puoi avviare una nuova partita di " + inputName + " " + inputSurname + " eliminando quella corrente, creare una nuova partita aggiungendo '(nuovo)' al nome, oppure creare una nuova partita specificando il nome"), "Scegli",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, null);

                if (result == JOptionPane.YES_OPTION) {
                    fullRankModel.removeRow(exists[1]);
                    Object[] row = new Object[maxLevels + 3];
                    row[0] = inputName;
                    row[1] = inputSurname;
                    row[2] = '?';
                    row[3] = '?';
                    row[4] = '?';
                    row[5] = '?';
                    row[6] = '?';
                    row[7] = '?';
                    fullRankModel.addRow(row);
                    currentLevel = 0;
                } else if (result == JOptionPane.NO_OPTION) {
                    inputName = inputName + " (nuovo)";
                    newGame();
                    return;
                } else {
                    view.getNewGameButton().doClick();
                    return;
                }
            }
        }

        prepareLabyrinth();
        view.getStartButton().setEnabled(true);
        JOptionPane.showMessageDialog(view, "Puoi avviare la partita cliccando 'Avvia' in ogni livello",
                "Informazione", JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Questo metodo verifica se un gioco con il nome e il cognome specificati esiste già nella lista dei giochi.
     *
     * @return Un array di interi che indica se il gioco esiste (1), se esiste ma non è stato completato (2), o se non esiste (0).
     * Inoltre, fornisce l'indice della riga del gioco nella delle partite e, se necessario, l'indice della colonna che rappresenta il livello incompleto.
     */

    private int[] checkIfGameAlreadyExists() {
        int rowIndex = fullRankModel.searchRow(inputName, inputSurname, 0, 1);
        int[] result = new int[maxLevels];
        result[0] = 0;
        if (rowIndex != -1) {
            int columnIndex = fullRankModel.searchColumn(rowIndex, "?");
            if (columnIndex != -1) {
                result[1] = rowIndex;
                if (!(columnIndex == maxLevels + 2)) {
                    result[0] = 2;
                    result[2] = columnIndex;
                    return result;
                } else {
                    result[0] = 1;
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


    /**
     * Questo metodo prepara il labirinto, ovvero crea il livello (oggetto di tipo Level) che verrà poi utilizzato dalla classe Labirinto.
     * Il livello viene costruito andando a chiamare i metodi della classe Builder (design pattern) che consente facilmente di creare pareti,
     * impostare la via d'uscita e la posizione iniziale del robot.
     */
    public void prepareLabyrinth() {
        Level.Builder builder = new Level.Builder(16, 16);
        if (currentLevel == maxLevels) { // Non è un vero e proprio livello, serve solo per visualizzare "END" alla fine della partita
            builder.addVerticalWall(0, 3, 11);
            builder.addHorizontalWall(3, 1, 4);
            builder.addHorizontalWall(7, 1, 3);
            builder.addHorizontalWall(11, 1, 4);
            builder.addVerticalWall(6, 3, 11);
            builder.addVerticalWall(7, 4, 5);
            builder.addVerticalWall(8, 6, 7);
            builder.addVerticalWall(9, 8, 9);
            builder.addVerticalWall(10, 3, 11);
            builder.addVerticalWall(12, 3, 11);
            builder.addHorizontalWall(3, 13, 14);
            builder.addHorizontalWall(11, 13, 14);
            builder.addVerticalWall(15, 4, 10);
            builder.setRobotStartXY(13, 8);
            drawLabyrinth(builder.build());
        } else {
            if (currentLevel == 0) { // Primo livello
                builder.addWalls();
                builder.addVerticalWall(3, 2, 9);
                builder.addVerticalWall(7, 8, 14);
                builder.addHorizontalWall(3, 7, 14);
                builder.addHorizontalWall(10, 11, 13);
                builder.setExit(12, 0);
                builder.setRobotStartXY(1, 14);
            } else if (currentLevel == 1) { // Secondo livello
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
            } else if (currentLevel == 2) { // Terzo livello
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
            } else if (currentLevel == 3) { // Quarto livello
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
            } else if (currentLevel == 4) { // Quinto livello
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


            level = builder.build(); // Costruisci il livello

            // Inizializza il labirinto con il livello appena costruito
            if (labGame == null) {
                try {
                    labGame = new LabyrinthGame(level);
                } catch (
                        Exception e) { // Genera un eccezione se il livello creato non è rappresentato da una matrice quadrata
                    throw new RuntimeException(e);
                }
            } else {

                labGame = labGame.clone();
                try {
                    labGame.setLabyrinth(level);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }


            Mediator mediator = new Mediator(labGame, this);
            this.setMediator(mediator); // Imposta il mediatore tra MainController e LabyrinthGame per poter utilizzare mediante esso i metodi di Labyrinth
            drawLabyrinth(level);
            mediator.addObserver(view);

            updateRank(); // Aggiorna la classifica su schermo in base al livello corrente
            view.getStateLabel().setText("Livello " + (currentLevel + 1) + " - Avvia per iniziare");
            scores[currentLevel] = 0;
        }


    }


    /**
     * Questo metodo si occupa di aggiornare su schermo la classifica.
     * In base al livello corrente, va a filtrare i dati presenti nel modello della tabella per visualizzare solo i punteggi
     * di tutte le partite effettuate di quel livello.
     */
    private void updateRank() {
        // Elimina tutte le righe presenti nella tabella
        while (modelLevelRank.getRowCount() > 0) {
            modelLevelRank.removeRow(0);
        }

        if (currentLevel == maxLevels) {
            view.getRankLabel().setText("Classifica finale");
        } else {
            view.getRankLabel().setText("Classifica - Livello " + (currentLevel + 1));
        }

        for (int i = 0; i < fullRankModel.getRowCount(); i++) { // Per ogni riga nel modello
            if (!fullRankModel.getValueAt(i, currentLevel + 2).toString().equals("?")) { // Se nella colonna corrispondente al livello corrente è diverso da '?' (che indica partita non effettuata)
                modelLevelRank.addRow(new Object[]{fullRankModel.getValueAt(i, 0), fullRankModel.getValueAt(i, 1), fullRankModel.getValueAt(i, currentLevel + 2)}); // Allora aggiungi nel modello filtrato il nome, il cognome e il punteggio per quel livello
            }
        }

        // La terza colonna rappresenta il punteggio, quindi viene ordinato in senso crescente tutte le righe in base a questa colonna
        modelLevelRank.sortByColumn(2);
        view.getRankTable().setModel(modelLevelRank);
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    /**
     *  Questo metodo si occupa di disegnare il labirinto nell'interfaccia grafica.
     *  @param l Il livello che contiene le informazioni sul labirinto
    */
    public void drawLabyrinth(Level l) {

        char[][] labirinto = l.getLabyrinth();
        if (firstRun) { // Se è la prima volta che viene disegnato il labirinto
            Color checker;
            view.getLabyrinthPanel().setLayout(new GridLayout(row, col));
            for (int x = 0; x < 16; x++) { // Popola il pannello in piccoli sottopannelli disposti in matrice e imposta le immagini corrispondenti alla cella vuota o alla parete
                for (int y = 0; y < 16; y++) {
                    if (labirinto[x][y] == '#') {
                        checker = Color.BLACK;
                    } else {
                        checker = Color.WHITE;
                    }

                    ImagePanel panel = new ImagePanel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/sand.png")).toString().substring(5)).getImage());
                    if (checker == Color.BLACK) {
                        panel.setImage(new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/wall.png")).toString().substring(5)).getImage());
                    }
                    panel.setMaximumSize(new Dimension(30, 30));
                    panel.setBackground(Color.WHITE);
                    view.getLabyrinthPanel().add(panel);
                }
            }

            firstRun = false;
        } else { // Altrimenti non crea i sottopannelli, cambia solo la loro immagine
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    if (labirinto[x][y] == '#') {
                        view.setImagePanelXY("/img/wall.png", x, y);
                    } else {
                        view.setImagePanelXY("/img/sand.png", x, y);
                    }
                }
            }
        }
        view.setImagePanelXY("/img/robot.png", l.getRobotX(), l.getRobotY());
    }


    /**
     * Il metodo startLabyrinth inizia il gioco del labirinto. Utilizza il metodo iterate() della classe LabyrinthGame per eseguire le iterazioni e ad ogni ciclo,
     * aggiorna graficamente lo stato attuale del gioco (posizione del robot, stato del robot e posizione degli oggetti).
     * Il metodo imposta anche l'immagine e il testo per visualizzare lo stato del robot durante il gioco. Alla fine del gioco, il labirinto viene pulito e viene
     * visualizzato il percorso effettuato dal robot.
     */
    public void startLabyrinth() {
        int differenceSizeMemento = 0;
        char[][] lab = mediator.getLabyrinthGame();
        view.getNewGameButton().setEnabled(false);
        view.getLabyrinthPanel().setBackground(Color.WHITE);


        Icon imgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/loading.gif")).toString().substring(5));
        view.getLabelImg().setIcon(imgIcon);
        view.getLabelImg().setVisible(true);
        view.getStateLabel().setText("Labirinto in esecuzione...");



        // Pulisce il labirinto graficamente e disegna il livello
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (level.getLabyrinth()[i][j] != '#') {
                    view.setImagePanelXY("/img/sand.png", i, j); // Disegna cella vuota
                } else {
                    view.setImagePanelXY("/img/wall.png", i, j); // Altrimenti disegna la parete
                }
            }
        }

        while (!labGame.iterate()) { // Itera fino a quando il metodo iterate() segnala che il robot ancora non ha raggiunto la destinazione
            if (caretaker != null) { // Gestisce la stampa alla prima iterazione andando a controllare se ci sono stati precedenti del robot
                if (caretaker.sizeMemento() > 1) {
                    view.setImagePanelXY("/img/sand.png", caretaker.getMemento(caretaker.sizeMemento() - 2).getX(), caretaker.getMemento(caretaker.sizeMemento() - 2).getY());
                    if (caretaker.sizeMemento() > 3 && (caretaker.sizeMemento() - differenceSizeMemento == 2)) {
                        view.setImagePanelXY("/img/sand.png", caretaker.getMemento(caretaker.sizeMemento() - 3).getX(), caretaker.getMemento(caretaker.sizeMemento() - 3).getY());
                    }
                }
                differenceSizeMemento = caretaker.sizeMemento();
            }


            RobotState state = ((RobotEntity) mediator.getRobot()).getState(); // Attraverso il Mediator, chiama il metodo di LabyrinthGame che restituisce l'istanza State del robot
            // In base allo stato del robot imposta il testo della label che segnala graficamente lo stato attuale del robot
            if (state instanceof PursuitState) {
                view.getStateRobotLabel().setText("Stato robot: pursuit");
            } else if (state instanceof SeekState) {
                view.getStateRobotLabel().setText("Stato robot: seek");
            } else if (state instanceof FleeState) {
                view.getStateRobotLabel().setText("Stato robot: flee");
            } else if (state instanceof EvadeState) {
                view.getStateRobotLabel().setText("Stato robot: evade");
            }

            caretaker = mediator.getCaretaker();
            view.setImagePanelXY("/img/robot.png", labGame.getRobotX(), labGame.getRobotY());

            try {
                TimeUnit.MILLISECONDS.sleep(300); // Per rallentare, dare una pausa tra un iterazione e un'altra
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        // Finita la partita
        imgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/img/finish.png")).toString().substring(5));
        view.getLabelImg().setIcon(imgIcon);
        view.getStateRobotLabel().setText("");

        // Pulisci il labirinto
        for (int x = 0; x < row; x++) {
            for (int y = 0; y < col; y++) {
                if (lab[x][y] == '#') {
                    view.setImagePanelXY("/img/wall.png", x, y);
                } else {
                    view.setImagePanelXY("/img/sand.png", x, y);
                }
            }
        }


        // Visualizza il percorso effettuato dal robot
        for (int i = 0; i < caretaker.sizeMemento(); i++) {
            Memento memento = caretaker.getMemento(i);
            ((RobotEntity) labGame.getPlayer()).restoreFromMemento(memento); // Viene ricavando andando a scorrere tutti gli stati precedenti del robot
            view.setImagePanelXY("/img/footprints.png", labGame.getPlayer().getX(), labGame.getPlayer().getY()); // Imposta l'immagine del passo sul percorso
        }

        // Viene calcolato il punteggio, ovvero andando a contare il numero di passi effettuati dal robot (con lo stesso metodo)
        for (int i = 0; i < caretaker.sizeMemento(); i++) {
            if (i > 0 && ((caretaker.getMemento(i).getX() != caretaker.getMemento(i - 1).getX()) || (caretaker.getMemento(i).getY() != caretaker.getMemento(i - 1).getY()))) {
                scores[currentLevel]++;
            }
        }

        view.getStateLabel().setText("Il robot ha raggiunto la destinazione con " + scores[currentLevel] + " passi!");
        if (currentLevel < maxLevels) {
            view.getNextLevelButton().setEnabled(true);
        }


        // Alla fine della partita, viene aggiornato il file con la nuova partita
        for (int i = 0; i < fullRankModel.getRowCount(); i++) {
            if (fullRankModel.getValueAt(i, 0).equals(inputName) && fullRankModel.getValueAt(i, 1).equals(inputSurname)) { // Trova la riga dove è presente la partita attuale (nome e cognome devono corrispondere)
                fullRankModel.setValueAt(Integer.toString(scores[currentLevel]), i, currentLevel + 2); // Imposta la colonna del corrispettivo livello con il punteggio ottenuto dal robot

                if (currentLevel == maxLevels - 1) { // Se si tratta dell'ultimo livello
                    int totalScore = 0;
                    for (int j = 2; j < fullRankModel.getColumnCount() - 1; j++) {
                        totalScore = totalScore + Integer.parseInt(fullRankModel.getValueAt(i, j).toString()); // Somma i punteggi ottenuti in tutti i livelli
                    }
                    fullRankModel.setValueAt(Integer.toString(totalScore), i, maxLevels + 2); // E salvalo come punteggio finale
                }
                try {
                    fullRankModel.saveToFile(fileClassifica); // Salva il modello aggiornato sul file
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        updateRank(); // Aggiorna la classifica su schermo
        view.getNewGameButton().setEnabled(true); // Abilita il pulsante per avviare una nuova partita
    }


    public static void main(String[] args) {
        FlatLightLaf.setup();
        new MainController(); // Avvia il costruttore di questa classe che estende JForm, quindi avvia l'interfaccia grafica
    }


    /**
     * Questo metodo crea un file "classifica.csv" nella directory dove è presente il file della classe in esecuzione.
     *
     * @return un valore booleano che indica se il file è stato creato o meno
     */
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

    /**
     * Metodo che prepara il modello da utilizzare per conservare tutte le partite nel file
     * Il metodo cerca di creare il file se non esiste, in caso contrario tenta di leggere il file esistente.
     * Nel caso in cui il file non possa essere letto correttamente, viene mostrata una finestra di dialogo che chiede
     * all'utente se eliminare il file e crearne uno nuovo oppure se non farlo.
     */
    private void prepareModelFile() {
        boolean fileCreatedOrRead = false;

        modelLevelRank = new CustomTableModel(new String[]{"Nome", "Cognome", "Punteggio"});
        while (!fileCreatedOrRead) {
            if (createFile()) {
                String[] columnsText = new String[maxLevels + 3];
                columnsText[0] = "Nome";
                columnsText[1] = "Cognome";
                for (int i = 2; i < maxLevels + 2; i++) {
                    columnsText[i] = ("Passi LV." + (i - 1));
                }
                columnsText[maxLevels + 2] = "Totale passi";
                fullRankModel = new CustomTableModel(columnsText);
                fileCreatedOrRead = true;
            } else {
                String[] columnsText = new String[maxLevels + 3];
                for (int i = 2; i < maxLevels + 2; i++) {
                    columnsText[i] = ("Passi LV." + i);
                }
                columnsText[maxLevels + 2] = "Totale passi";
                fullRankModel = new CustomTableModel(columnsText);
                try {
                    fullRankModel.loadFromFile(fileClassifica);
                    // Testa il file per verificare se tutti i dati vengono letti correttamente
                    for(int i = 0; i < fullRankModel.getRowCount(); i++) {
                        for(int j = 0; j < fullRankModel.getColumnCount(); j++) {
                            fullRankModel.getValueAt(i,j);
                        }
                    }
                    fileCreatedOrRead = true;
                } catch (IOException | IndexOutOfBoundsException e) {
                    JOptionPane.showMessageDialog(view, "Errore nella lettura del file 'classifica.csv', la classifica non è stata caricata.",
                            "Errore file", JOptionPane.ERROR_MESSAGE);

                    int result = JOptionPane.showConfirmDialog(view, "Si consiglia di eliminare il file 'classifica.csv', procedere?", "Eliminare il file?",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        fileClassifica.delete();
                        JOptionPane.showMessageDialog(view, "Il file è stato eliminato e ne verrà creato uno nuovo.",
                                "Avviso", JOptionPane.INFORMATION_MESSAGE);
                    } else if (result == JOptionPane.NO_OPTION) {
                        JOptionPane.showMessageDialog(view, "Il file non è stato eliminato, potrebbero sorgere problemi durante l'esecuzione del programma.",
                                "Avviso", JOptionPane.WARNING_MESSAGE);
                        fileCreatedOrRead = true;
                    }
                }
            }
        }
    }





}
