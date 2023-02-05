public class Mediator {
    private Labyrinth labyrinth;
    private MainGUI mainGUI;

    // Costruttore che inizializza le due classi
    public Mediator(Labyrinth labyrinth, MainGUI mainGUI) {
        this.labyrinth = labyrinth;
        this.mainGUI = mainGUI;
    }

    // Metodo che gestisce la richiesta di MainGui per ottenere il Caretaker
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
