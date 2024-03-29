/**
 * La classe Mediator serve come intermediario tra la classe {@link LabyrinthGame} e la classe {@link MainController}.
 * Questa classe contiene i metodi per gestire le richieste di MainController per ottenere il Caretaker e altri metodi
 * presenti in Labyrinth che possono essere invocati attraverso il Mediator.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */
public class Mediator {
    private LabyrinthGame labyrinthGame;
    private MainController mainController;

    /**
     * Costruttore che inizializza le due classi.
     * @param labyrinthGame La classe {@link LabyrinthGame} che rappresenta il labirinto.
     * @param mainController La classe {@link MainController} che rappresenta l'interfaccia grafica principale.
     */
    public Mediator(LabyrinthGame labyrinthGame, MainController mainController) {
        this.labyrinthGame = labyrinthGame;
        this.mainController = mainController;
    }

    /**
     * Di seguito sono presenti metodi di Labyrinth che possono essere invocati
     * attraverso il mediatore.
     */

    public Caretaker getCaretaker() {
        return labyrinthGame.getRobotCaretaker();
    }

    public char[][] getLabyrinthGame() {
        return labyrinthGame.getLabyrinth();
    }

    public void addObserver(Observer observer) {
        labyrinthGame.addObserver(observer);
    }

    public Entity getRobot() {
        return labyrinthGame.getPlayer();
    }
}
