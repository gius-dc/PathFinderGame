/**
 * La classe Mediator serve come intermediario tra la classe {@link Labyrinth} e la classe {@link MainGUI}.
 * Questa classe contiene i metodi per gestire le richieste di MainGUI per ottenere il Caretaker e altri metodi
 * presenti in Labyrinth che possono essere invocati attraverso il Mediator.
 */
public class Mediator {
    private Labyrinth labyrinth;
    private MainGUI mainGUI;

    /**
     * Costruttore che inizializza le due classi.
     * @param labyrinth La classe {@link Labyrinth} che rappresenta il labirinto.
     * @param mainGUI La classe {@link MainGUI} che rappresenta l'interfaccia grafica principale.
     */
    public Mediator(Labyrinth labyrinth, MainGUI mainGUI) {
        this.labyrinth = labyrinth;
        this.mainGUI = mainGUI;
    }

    /**
     * Di seguito sono presenti metodi di Labyrinth che possono essere invocati
     * attraverso il mediatore.
     */

    public Caretaker getCaretaker() {
        return labyrinth.getRobotCaretaker();
    }

    public char[][] getLabyrinth() {
        return labyrinth.getLabyrinth();
    }

    public void addObserver(Observer observer) {
        labyrinth.addObserver(observer);
    }

    public RobotEntity getRobot() {
        return labyrinth.getRobot();
    }
}
