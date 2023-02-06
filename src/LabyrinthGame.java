import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static java.lang.Math.sqrt;
/**
 *    Questa classe LabyrinthGame gestisce la logica di gioco.
 */

public class LabyrinthGame extends Labyrinth implements Cloneable {
    private Level l;
    private List<ObjectEntity> objects; // lista di oggetti (del labirinto)
    // State
    private RobotState state;
    private Caretaker caretaker;


    // Observer
    private List<Observer> observers = new ArrayList<>();
    public static final int OBJECT_ADDED = 1;
    public static final int OBJECT_REMOVED = 2;
    private Random random;

    // Metodo per clonare il labirinto (design pattern Prototype)
    @Override
    public LabyrinthGame clone() {
        try {
            LabyrinthGame clone = (LabyrinthGame) super.clone();
            clone.objects = new ArrayList<>(this.objects.size());
            for (ObjectEntity object : this.objects) {
                clone.objects.add(object);
            }
            clone.state = this.state;
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    // Costruttore
    public LabyrinthGame(Level l) {
        this.l = l;

        try {
            setLabyrinth(l);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLabyrinth(Level l) throws Exception {
        caretaker = Caretaker.getInstance();
        objects = new ArrayList<>();
        random = new Random();
        caretaker.resetMemento();
        player = new RobotEntity(l.getRobotX(), l.getRobotY());
        labyrinth = l.getLabyrinth();
        ((RobotEntity) player).setX(l.getRobotX());
        ((RobotEntity) player).setY(l.getRobotY());
        exitN = l.getExit();
        if (!checkIfMatrixIsSquare(labyrinth)) {
            throw new Exception("Il livello contiene una matrice non quadrata.");
        }
        DIMENSION = (int) sqrt(getSizeMatrix(labyrinth));

    }

    // Metodo per ottenere il Caretaker del Robot. Serve alla classe MainGUI() per ricostruire il percorso effettuato dal robot, andando a visitare tutti gli stati precedenti
    public Caretaker getRobotCaretaker() {
        return caretaker;
    }

    /**
     *  Il metodo iterate gestisce un'iterazione dell'ambiente di gioco, ciò consiste in:
     *  - Aggiungere o rimuovere un oggetto nel labirinto
     *  - Se il robot non ha già raggiunto l'uscita, effettuare un passo (o due)
     *  - Aggiornare lo stato del robot in base agli oggetti in prossimità
     *
     *  @return Boolean: true se il robot ha raggiunto la destinazione, false se non l'ha ancora raggiunta.
     */
    public Boolean iterate() {
        // Questo oggetto è stato dichiarato con un casting esplicito per rispettare il dependency inversion principle
        state = ((RobotEntity) player).getState();
        char c;
        int r, ox, oy;

        // A ogni iterazione viene scelto casualmente se aggiungere dei oggetti oppure rimuovere
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
                } else {
                    c = 'C';
                }

                do {
                    /* Genera delle cordinate fino a quando risultano valide, ovvero quando avviene contemporaneamente che
                       le coordinate non coincide con la parete, non coincide con il robot e non esiste un altro oggetto con le stesse coordinate */
                    ox = random.nextInt(DIMENSION - 2) + 1;
                    oy = random.nextInt(DIMENSION - 2) + 1;
                }
                while (checkIfObjectXYExists(objects, ox, oy) != -1 || ox == player.getX() && oy == player.getY() || labyrinth[ox][oy] == '#');

                // Aggiungi l'oggetto con le coordinate generate
                addObject(c, ox, oy);

            }

        } else {
            // Rimuove oggetti, il numero di oggetti da rimuovere viene scelto casualmente da 0 a 5
            int n = random.nextInt(6);
            for (int i = 0; i < n; i++) {

                if (objects.size() > 3) { // elimina un oggetto solo se ce ne sono almeno tre
                    try {
                        removeObject(objects.get(random.nextInt(objects.size())));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            }


        }

        if (!checkIfRobotGoal()) { // Se il robot non ha raggiunto ancora la destinazione effettua il prossimo passo


            state = ((RobotEntity) player).getState(); // Design pattern state, ottiene lo stato del robot (istanza di una sottoclasse di RobotState)
            if (state instanceof PursuitState) {
                doStepRobot(false); // Pursuit -> effettua un passo
                caretaker.addMemento(((RobotEntity) player).saveToMemento()); // dato che il robot ha cambiato coordinate, salva il suo nuovo stato (Memento)
            } else if (state instanceof SeekState) {
                doStepRobot(false); // Seek -> effettua un passo
                caretaker.addMemento(((RobotEntity) player).saveToMemento());
            } else if (state instanceof FleeState) {
                for (int i = 0; i < 2; i++) {
                    if (!checkIfRobotGoal()) {
                        doStepRobot(false); // Flee -> effettua due passi (ciclo for)
                        caretaker.addMemento(((RobotEntity) player).saveToMemento());
                    }
                }
            } else if (state instanceof EvadeState) {
                doStepRobot(true); // Evade -> effettua un passo casuale
                caretaker.addMemento(((RobotEntity) player).saveToMemento());
            }


            // Raccogli tutti gli oggetti adiacenti presenti vicino al robot
            int[][] adjacents = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
            List<ObjectEntity> adjacentObjects = new ArrayList<>();
            for (int[] adjacent : adjacents) {
                int x = player.getX() + adjacent[0];
                int y = player.getY() + adjacent[1];
                int indx = checkIfObjectXYExists(objects, x, y);
                if (indx != -1) {
                    adjacentObjects.add(objects.get(indx));
                }
            }
            for (ObjectEntity adjacentObject : adjacentObjects) {
                // Per ogni oggetto in prossimità aggiorna lo stato del robot
                ((RobotEntity) player).updateState(adjacentObject);
            }
            return false;
        }
        return true;
    }

    /**
     * Verifica se il robot ha raggiunto la sua destinazione finale.
     * La destinazione finale è definita dalla cella di uscita del labirinto.
     *
     * @return true se il robot ha raggiunto la sua destinazione finale, altrimenti false.
     */
    private Boolean checkIfRobotGoal() {
        int exitX = exitN / DIMENSION;
        int exitY = exitN % DIMENSION;
        if (player.getX() == exitX && player.getY() == exitY) {
            return true;
        } else if ((exitX == player.getX()) && (exitX == 0 || exitX == DIMENSION - 1)) {
            return player.getY() == exitY || player.getY() == exitY + 1 || player.getY() == exitY - 1;
        } else if ((exitY == player.getY()) && (exitY == 0 || exitY == DIMENSION - 1)) {
            return player.getX() == exitX || player.getX() == exitX + 1 || player.getX() == exitX - 1;
        }

        return false;
    }

    /**
     * Effettua il prossimo passo del robot.
     *
     * @param casual Se true, il robot effettua un movimento casuale. Se false, il robot effettua un movimento ottimale.
     */
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
            System.arraycopy(labyrinth[i], 0, matrix[i], 0, DIMENSION);
        }

        for (ObjectEntity object : objects) {
            matrix[object.getX()][object.getY()] = '#';
        }

        // In base alla strategia impostata, effettua il prossimo passo
        strategy.move(((RobotEntity) player), matrix, DIMENSION, exitN);

    }

    /**
     * Questo metodo verifica se esiste un oggetto nella posizione x, y specificata.
     * @param objects La lista di oggetti nell'ambiente
     * @param x Posizione x
     * @param y Posizione y
     * @return L'indice dell'oggetto nella lista se esiste, altrimenti -1.
     */
    private int checkIfObjectXYExists(List<ObjectEntity> objects, int x, int y) {
        return objects.stream()
                .filter(o -> o.getX() == x && o.getY() == y)
                .findFirst()
                .map(objects::indexOf)
                .orElse(-1);
    }

    /**
     * Aggiunge un oggetto alla lista degli oggetti.
     *
     * @param color Il colore dell'oggetto da creare
     * @param x La posizione X dell'oggetto
     * @param y La posizione Y dell'oggetto
     */
    private void addObject(char color, int x, int y) {
        // In questo metodo il factory method viene invocato per creare un'istanza dell'oggetto dal colore desiderato
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

        if (factory == null) {
            // Viene gestito il caso in cui il colore non dovesse essere supportato, lanciando un'eccezione
            throw new IllegalArgumentException("Il colore non è supportato: " + color);
        }

        ObjectEntity newObject = factory.createObject(x, y);
        objects.add(newObject);
        notifyObservers(newObject, OBJECT_ADDED);
    }


    /**
     * Rimuove un oggetto dalla lista degli oggetti presenti nel labirinto.
     *
     * @param oggetto l'oggetto da rimuovere
     */
    private void removeObject(ObjectEntity oggetto) {
        objects.remove(oggetto);
        notifyObservers(oggetto, OBJECT_REMOVED);
    }


    /**
     * Notifica gli observer (in questo caso MainGUI) che un oggetto è stato modificato.
     *
     * @param object    L'oggetto che è stato modificato
     * @param eventType Il tipo di evento che ha causato la modifica (OBJECT_ADDED od OBJECT_REMOVED)
     */
    private void notifyObservers(ObjectEntity object, int eventType) {
        for (Observer observer : observers) {
            observer.update(object, eventType);
        }
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // Metodi pubblici che servono per ottenere le coordinate del robot
    public int getRobotX() {
        return player.getX();
    }

    public int getRobotY() {
        return player.getY();
    }

    public Entity getPlayer() {
        return player;
    }

}
