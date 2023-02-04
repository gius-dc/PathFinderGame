public class Mediator {
    private Labirinto labirinto;
    private MainGUI mainGUI;

    // Costruttore che inizializza le due classi
    public Mediator(Labirinto labirinto, MainGUI mainGUI) {
        this.labirinto = labirinto;
        this.mainGUI = mainGUI;
    }

    // Metodo che gestisce la richiesta di MainGui per ottenere il Caretaker
    public Caretaker getCaretaker() {
        return labirinto.getRobotCaretaker();
    }

    public char[][] getLabyrinth() {
        return labirinto.getLabyrinth();
    }

    public void addObserver(Observer observer) {
        labirinto.addObserver(observer);
    }

    public RobotEntity getRobot() {
        return labirinto.getRobot();
    }
}
