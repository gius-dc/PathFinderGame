import com.formdev.flatlaf.FlatLightLaf;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;

/**
 * Questa classe è responsabile dell'avvio del programma e della gestione dell'interfaccia grafica
 */

public class GUIView extends JFrame implements Observer {
    private JPanel mainPanel;
    private JPanel labyrinthPanel;
    private JButton startButton;
    private JLabel stateLabel;
    private JTable rankTable;
    private JLabel labelImg;
    private JLabel stateRobotLabel;
    private JButton nextLevelButton;
    private JButton showRankButton;
    private JButton newGameButton;
    private JLabel rankLabel;
    private JSlider volumeSlider;
    private JLabel muteButton;
    private JScrollPane JScrollPanel1;
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

    public GUIView() {
        setContentPane(mainPanel);
        setTitle("Labirinto");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);

        // Imposta l'icona dell'applicazione su Windows e Linux
        setIconImage(Toolkit.getDefaultToolkit().getImage(Objects.requireNonNull(getClass().getResource("/img/robot.png")).getPath()));
        // Imposta l'icona dell'applicazione su MacOS (ed altri sistemi operativi che supportano questo metodo)
        try {
            // Imposta l'icona dell'applicazione su MacOS (ed altri sistemi operativi che supportano questo metodo)
            final Taskbar taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(Toolkit.getDefaultToolkit().getImage(Objects.requireNonNull(getClass().getResource("/img/robot.png")).getPath()));
        } catch (final UnsupportedOperationException | SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Questo metodo si occupa di impostare l'immagine del pannello nella posizione specificata.
     *
     * @param imagePath Il percorso dell'immagine da impostare.
     * @param x         La posizione x nella quale impostare l'immagine.
     * @param y         La posizione y nella quale impostare l'immagine.
     */
    public void setImagePanelXY(String imagePath, int x, int y) {
        ((ImagePanel) labyrinthPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(Objects.requireNonNull(getClass().getResource(imagePath)).getPath()).getImage());
    }

    public JButton getNewGameButton() {
        return newGameButton;
    }

    public JButton getNextLevelButton() {
        return nextLevelButton;
    }

    public JLabel getMuteButton() {
        return muteButton;
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getShowRankButton() {
        return showRankButton;
    }

    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    public JPanel getLabyrinthPanel() {
        return labyrinthPanel;
    }

    public JLabel getLabelImg() {
        return labelImg;
    }

    public JLabel getStateLabel() {
        return stateLabel;
    }

    public JTable getRankTable() {
        return rankTable;
    }

    public JLabel getStateRobotLabel() {
        return stateRobotLabel;
    }

    public JLabel getRankLabel() {
        return rankLabel;
    }

    /**
     * Questo metodo viene utilizzato per gestire gli eventi di aggiunta e rimozione degli oggetti nel labirinto.
     * È stato utilizzato il design pattern Observer, poiché l'oggetto viene notificato dalla classe LabyrinthGame
     * e viene eseguita l'azione appropriata in base all'evento (OBJECT_ADDED o OBJECT_REMOVED).
     *
     * @param obj       rappresenta l'oggetto che è stato modificato
     * @param eventType rappresenta il tipo di evento legato all'oggetto (aggiunto o rimosso)
     */
    @Override
    public void update(ObjectEntity obj, int eventType) {
        if (eventType == LabyrinthGame.OBJECT_ADDED) {
            // Aggiungi graficamente l'oggetto al labirinto
            if (obj.getColor() == 'R') {
                setImagePanelXY("/img/red_stone.png", obj.getX(), obj.getY());
            } else if (obj.getColor() == 'Y') {
                setImagePanelXY("/img/yellow_lemon.png", obj.getX(), obj.getY());
            } else if (obj.getColor() == 'C') {
                setImagePanelXY("/img/cyan_bucket.png", obj.getX(), obj.getY());
            } else if (obj.getColor() == 'G') {
                setImagePanelXY("/img/green_cactus.png", obj.getX(), obj.getY());
            }

        } else if (eventType == LabyrinthGame.OBJECT_REMOVED) {
            // Rimuovi graficamente l'oggetto dal labirinto
            setImagePanelXY("/img/sand.png", obj.getX(), obj.getY());
        }
    }
}
