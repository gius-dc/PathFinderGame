import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import static java.lang.Math.*;

/*
    Questa classe Labirinto è il vero e proprio cuore del progetto:
    - Dato un oggetto di tipo Level (che nella classe MainGUI viene preparata attraverso il design pattern Builder) viene inizializzato il labirinto, ovvero
    dal livello viene ricavato la matrice del labirinto, la porta d'uscita e la posizione iniziale del robot. Questo viene effettuato dal metodo setLabyrinth().
    - Questa classe contiene molteplici metodi, tra i più importanti troviamo iterate() e doStepRobot() che implementano la logica di base del labirinto.
*/

public class Labyrinth extends Observable implements Cloneable {
    // Dimensione del labirinto
    private static int DIMENSION = 0;
    private RobotEntity robot;
    private List<ObjectEntity> objects;
    private Random random;
    private char[][] labyrinth;
    private RobotState state;
    private int exitN;
    private Caretaker caretaker = null;
    private List<Observer> observers = new ArrayList<>();
    public static final int OBJECT_ADDED = 1;
    public static final int OBJECT_REMOVED = 2;


    // Metodo per clonare il labirinto (design pattern Prototype)
    @Override
    public Labyrinth clone() {
        try {
            return (Labyrinth) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    // Costruttore
    public Labyrinth(Level l) throws IllegalAccessException {
        caretaker = Caretaker.getInstance();
        setLabyrinth(l);

    }

    private void setLabyrinth(Level l) throws IllegalAccessException {
        objects = new ArrayList<>();
        random = new Random();
        labyrinth = l.getLabyrinth();
        exitN = l.getExit();
        caretaker.resetMemento();

        if (!checkIfMatrixIsSquare(labyrinth)) {
            throw new IllegalAccessException("Il livello contiene una matrice non quadrata.");
        }

        DIMENSION = (int) sqrt(getSizeMatrix(labyrinth));


        robot = new RobotEntity(l.getRobotX(), l.getRobotY());
    }


    public void resetLabyrinth(Level l) throws IllegalAccessException {
        setLabyrinth(l);
    }

    // Metodo per ottenere il Caretaker del Robot. Serve alla classe MainGUI() per ricostruire il percorso effettuato dal robot, andando a visitare tutti gli stati precedenti
    public Caretaker getRobotCaretaker() {
        return caretaker;
    }

    /* Quando viene chiamato questo metodo viene effettuato un'iterazione, che consiste in:
       - Aggiungere o rimuovere un oggetto nel labirinto
       - Se il robot non ha già raggiunto l'uscita, effettuare un passo
       - Aggiornare lo stato del robot in base agli oggetti in prossimità

       Ritorna un valore booleano:
       - true -> il robot ha raggiunto la destinazione
       - false -> il robot non ha ancora raggiunto la destinazione
    */
    public Boolean iterate() {
        state = robot.getState();

        char c = '\u0000';
        int r, ox = 0, oy = 0;
        Boolean flag = true;

        // Ad ogni iterazione viene scelto casualmente se aggiungere dei oggetti oppure rimuovere
        if (random.nextInt(100) % 2 == 0) {
            // Aggiungi oggetti, il numero di oggetti da aggiungere viene scelto casualmente da 0 a 4
            int n = random.nextInt(5);
            for (int i = 0; i < n; i++) {
                // Viene selezionato randomicamente il colore
                r = random.nextInt(4);
                if (r == 0) {
                    c = 'R';
                } else if (r == 1) {
                    c = 'G';
                } else if (r == 2) {
                    c = 'Y';
                } else if (r == 3) {
                    c = 'C';
                }

                do {
                    /* Genera delle cordinate fino a quando risultano valide, ovvero quando avviene contemporaneamente che
                       le coordinate non coincide con la parete, non coincide con il robot e non esiste un altro oggetto con le stesse coordinate */
                    ox = random.nextInt(DIMENSION - 2) + 1;
                    oy = random.nextInt(DIMENSION - 2) + 1;
                }
                while (checkIfObjectXYExists(objects, ox, oy) != -1 || ox == robot.getX() && oy == robot.getY() || labyrinth[ox][oy] == '#');

                // Aggiungi l'oggetto con le coordinate generate
                addObject(c, ox, oy);

            }

        } else {
            // Rimuove oggetti, il numero di oggetti da rimuovere viene scelto casualmente da 0 a 5
            int n = random.nextInt(6);
            for (int i = 0; i < n; i++) {

                if (objects.size() > 3) { // elimina un oggetto solo se ce ne sono almeno tre
                    r = random.nextInt(objects.size()); // viene selezionato randomicamente un oggetto da rimuovere
                    try {
                        removeObject(objects.get(random.nextInt(objects.size())));
                    } catch (Exception e) {
                    }

                }
            }


        }

        if (!checkIfRobotGoal()) { // Se il robot non ha raggiunto ancora la destinazione effettua il prossimo passo


            state = robot.getState(); // Design pattern state, ottiene lo stato del robot (istanza di una sottoclasse di RobotState)
            if (state instanceof PursuitState) {
                doStepRobot(false); // Pursuit -> effettua un passo
                caretaker.addMemento(robot.saveToMemento()); // dato che il robot ha cambiato coordinate, salva il suo nuovo stato (Memento)
            } else if (state instanceof SeekState) {
                doStepRobot(false); // Seek -> effettua un passo
                caretaker.addMemento(robot.saveToMemento());
            } else if (state instanceof FleeState) {
                for (int i = 0; i < 2; i++) {
                    if (!checkIfRobotGoal()) {
                        doStepRobot(false); // Flee -> effettua due passi (ciclo for)
                        caretaker.addMemento(robot.saveToMemento());
                    }
                }
            } else if (state instanceof EvadeState) {
                doStepRobot(true); // Evade -> effettua un passo casuale
                caretaker.addMemento(robot.saveToMemento());
            }


            // Raccogli tutti gli oggetti adiacenti presenti vicino al robot
            int[][] adjacents = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
            List<ObjectEntity> adjacentObjects = new ArrayList<>();
            for (int[] adjacent : adjacents) {
                int x = robot.getX() + adjacent[0];
                int y = robot.getY() + adjacent[1];
                int indx = checkIfObjectXYExists(objects, x, y);
                if (indx != -1) {
                    adjacentObjects.add(objects.get(indx));
                }
            }
            for (ObjectEntity adjacentObject : adjacentObjects) {
                // Per ogni oggetto in prossimità aggiorna lo stato del robot
                robot.updateState(adjacentObject);
            }
            return false;
        }
        return true;
    }

    // Metodo per verificare se il robot attualmente si trova alla destinazione
    private Boolean checkIfRobotGoal() {
        int exitX = exitN / DIMENSION;
        int exitY = exitN % DIMENSION;
        return (robot.getX() == exitX && robot.getY() == exitY) ||
                (Math.abs(robot.getX() - exitX) <= 1 && Math.abs(robot.getY() - exitY) <= 1);
    }

    // Metodo che effettua un passo del robot
    public void doStepRobot(Boolean casual) {
        // Design pattern Strategy
        RobotMovement strategy = new RobotMovement();
        if (casual) {
            // Imposta la strategia del movimento casuale
            strategy.setStrategy(new RobotMovement.RandomMovement());
        } else {
            // Imposta la strategia del movimento ottimale, ovvero seguendo l'algoritmo di Dijkstra
            strategy.setStrategy(new RobotMovement.OptimalMovement());
        }


        char[][] matrix = new char[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                matrix[i][j] = labyrinth[i][j];
            }
        }

        for (int i = 0; i < objects.size(); i++) {
            matrix[objects.get(i).getX()][objects.get(i).getY()] = '#';
        }

        strategy.move(robot, matrix, DIMENSION, exitN);

    }

    private int getSizeMatrix(char[][] matrix) {
        return matrix.length * matrix[0].length;
    }


    private Boolean checkIfMatrixIsSquare(char[][] matrix) {
        double sqrt = Math.sqrt(getSizeMatrix(matrix));
        return ((sqrt - Math.floor(sqrt)) == 0);
    }

    private int checkIfObjectXYExists(List<ObjectEntity> objects, int x, int y) {
        return objects.stream()
                .filter(o -> o.getX() == x && o.getY() == y)
                .findFirst()
                .map(objects::indexOf)
                .orElse(-1);
    }

    public char[][] getLabyrinth() {
        return labyrinth;
    }

    public int getRobotX() {
        return robot.getX();
    }

    public int getRobotY() {
        return robot.getY();
    }


    public RobotEntity getRobot() {
        return robot;
    }


    public void addObject(char color, int x, int y) {
        ObjectFactory factory = null;
        if (color == 'R') {
            factory = new ObjectRedFactory();
        } else if (color == 'G') {
            factory = new ObjectGreenFactory();
        } else if (color == 'C') {
            factory = new ObjectCyanFactory();
        } else if (color == 'Y') {
            factory = new ObjectYellowFactory();
        }
        ObjectEntity newObject = factory.createObject(x, y);
        objects.add(newObject);
        notifyObservers(newObject, OBJECT_ADDED);
    }

    public void removeObject(ObjectEntity oggetto) {
        objects.remove(oggetto);
        notifyObservers(oggetto, OBJECT_REMOVED);
    }


    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(ObjectEntity object, int eventType) {
        for (Observer observer : observers) {
            observer.update(object, eventType);
        }
    }
}