import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class Labirinto extends Observable {
    // Dimensione del labirinto
    private static int DIMENSIONE = 0;
    private Robot robot;
    private String nome;
    private String stato;
    private List<Oggetto> oggetti;
    private int passi;
    private Random random;
    private char[][] labirinto;
    RobotState state;
    int exitN;


    private List<Observer> observers = new ArrayList<>();
    public static final int OGGETTO_AGGIUNTO = 1;
    public static final int OGGETTO_RIMOSSO = 2;

    private boolean[][] pathRobot = new boolean[DIMENSIONE][DIMENSIONE];

    // Costruttore
    public Labirinto(Level l) {
        robot = new Robot(0, 0);
        oggetti = new ArrayList<>();
        passi = 0;
        random = new Random();
        labirinto = l.getLabyrinth();
        exitN = l.getExit();

        DIMENSIONE = (int) sqrt(getSizeMatrix(labirinto));
        // Posiziono il robot in un punto casuale all'interno del labirinto (che non coincida con la parete)
        do {
            robot.setX(random.nextInt(DIMENSIONE - 2) + 1);
            robot.setY(random.nextInt(DIMENSIONE - 2) + 1);
        } while (labirinto[robot.getX()][robot.getY()] == '#');




        // Aggiungi alcuni oggetti nel labirinto
        // (in posizione casuale e che non coincida con la parete)


        int ox, oy;
        // red
        do {
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        } while (labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('R', ox, oy));
        // green
        do {
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        } while (labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('G', ox, oy));
        // yellow
        do {
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        } while (labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('Y', ox, oy));
        // cyan
        do {
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        } while (labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('C', ox, oy));
    }

    public Boolean iterate() {
        state = robot.getState();
        char c = ' ';
        int r = 0, ox = 0, oy = 0, rx = 0, ry = 0;
        Boolean flag = false;
        if (random.nextInt(100) % 2 == 0) {

            // Aggiungi qualche oggetto
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

            for (int i = 0; i < 1; i++) {
                do {
                    ox = random.nextInt(DIMENSIONE - 2) + 1;
                    oy = random.nextInt(DIMENSIONE - 2) + 1;
                } while (labirinto[ox][oy] == '#');

                addOggetto(c, ox, oy);
            }

        } else {
            // Fai scomparire qualche oggetto

            if (oggetti.size() > 5) {
                try {
                    removeOggetto(oggetti.get(random.nextInt(oggetti.size())));
                } catch (Exception e) {
                }
            }
        }

        // Prossimo passo robot
        //if (!(robot.getX() == 12 && robot.getY() == 0) && !(robot.getX() == 11 && robot.getY() == 0) && !(robot.getX() == 13 && robot.getY() == 0)) {
        System.out.println(checkIfRobotGoal());
        if(!checkIfRobotGoal())
        {
            state = robot.getState();
            if (state instanceof PursuitState) {
                // esegui l'azione per lo stato di inseguimento
                doStepRobot(false);
            } else if (state instanceof SeekState) {
                // esegui l'azione per lo stato di ricerca
                doStepRobot(false);
            } else if (state instanceof FleeState) {
                // esegui l'azione per lo stato di fuga
                if (!checkIfRobotGoal()) {
                    doStepRobot(false);
                }
            } else if (state instanceof EvadeState) {
                // esegui l'azione per lo stato di evitamento
                doStepRobot(true);
            }
            robot.updateState(getNearestObject(oggetti, robot));
            return true;
        }
         return false;
    }

    private Boolean checkIfRobotGoal()
    {
        int exitX = exitN / DIMENSIONE;
        int exitY = exitN % DIMENSIONE;
        System.out.println("Exit (" + exitX + "," + exitY + ")");
        System.out.println("Robot (" + robot.getX() + "," + robot.getY() + ")");
        if(robot.getX() == exitX && robot.getY() == exitY)
        {
            return true;
        }
        else if ((exitX == robot.getX()) && (exitX == 0 || exitX == DIMENSIONE-1))
        {
            if(robot.getY() == exitY || robot.getY() == exitY+1 || robot.getY() == exitY-1)
            {
                return true;
            }
        }
        else if ((exitY == robot.getY()) && (exitY == 0 || exitY == DIMENSIONE-1))
        {
            if(robot.getX() == exitX || robot.getX() == exitX+1 || robot.getX() == exitX-1)
            {
                return true;
            }
        }

        return false;
    }

    public void doStepRobot(Boolean casual) {
        int x = robot.getX(), y = robot.getY();
        ShortestPath p = new ShortestPath();
        char matrice[][] = new char[DIMENSIONE][DIMENSIONE];
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                matrice[i][j] = labirinto[i][j];
            }
        }

        for (int i = 0; i < oggetti.size(); i++) {
            matrice[oggetti.get(i).getX()][oggetti.get(i).getY()] = '#';
        }

        int graph[][] = p.generateGraph(DIMENSIONE, matrice);

        int source = 30;
        int dist[];

        int confr[] = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};

        if (matrice[x - 1][y] != '#' && x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE) {
            dist = p.dijkstra(graph, ((x - 1) * DIMENSIONE) + y, DIMENSIONE);
            confr[0] = dist[exitN];
        }
        if (matrice[x + 1][y] != '#' && x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE) {
            dist = p.dijkstra(graph, ((x + 1) * DIMENSIONE) + y, DIMENSIONE);
            confr[1] = dist[exitN];
        }
        if (matrice[x][y - 1] != '#' && x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE) {
            dist = p.dijkstra(graph, (x * DIMENSIONE) + (y - 1), DIMENSIONE);
            confr[2] = dist[exitN];
        }
        if (matrice[x][y + 1] != '#' && x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE) {
            dist = p.dijkstra(graph, (x * DIMENSIONE) + (y + 1), DIMENSIONE);
            confr[3] = dist[exitN];
        }
        if (matrice[x + 1][y - 1] != '#' && x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE) {
            dist = p.dijkstra(graph, ((x + 1) * DIMENSIONE) + (y - 1), DIMENSIONE);
            confr[4] = dist[exitN];
        }
        if (matrice[x + 1][y + 1] != '#' && x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE) {
            dist = p.dijkstra(graph, ((x + 1) * DIMENSIONE) + (y + 1), DIMENSIONE);
            confr[5] = dist[exitN];
        }
        if (matrice[x - 1][y - 1] != '#' && x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE) {
            dist = p.dijkstra(graph, ((x - 1) * DIMENSIONE) + (y - 1), DIMENSIONE);
            confr[6] = dist[exitN];
        }
        if (matrice[x - 1][y + 1] != '#' && x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE) {
            dist = p.dijkstra(graph, ((x - 1) * DIMENSIONE) + (y + 1), DIMENSIONE);
            confr[7] = dist[exitN];
        }


        int min = 0;
        if (casual) {
            do {
                min = random.nextInt(8);
            } while (confr[min] == Integer.MAX_VALUE);
        } else {
            for (int i = 0; i < 8; i++) {
                if (confr[i] < confr[min]) {
                    min = i;
                }
            }
        }


        if (min == 0) {
            if (matrice[x - 1][y] != '#') {
                robot.setX(x - 1);
                robot.setY(y);
            }
        } else if (min == 1) {
            if (matrice[x + 1][y] != '#') {
                robot.setX(x + 1);
                robot.setY(y);
            }
        } else if (min == 2) {
            if (matrice[x][y - 1] != '#') {
                robot.setX(x);
                robot.setY(y - 1);
            }
        } else if (min == 3) {
            if (matrice[x][y + 1] != '#') {
                robot.setX(x);
                robot.setY(y + 1);
            }
        } else if (min == 4) {
            if (matrice[x + 1][y - 1] != '#') {
                robot.setX(x + 1);
                robot.setY(y - 1);
            }
        } else if (min == 5) {
            if (matrice[x + 1][y + 1] != '#') {
                robot.setX(x + 1);
                robot.setY(y + 1);
            }
        } else if (min == 6) {
            if (matrice[x - 1][y - 1] != '#') {
                robot.setX(x - 1);
                robot.setY(y - 1);
            }
        } else if (min == 7) {
            if (matrice[x - 1][y + 1] != '#') {
                robot.setX(x - 1);
                robot.setY(y + 1);
            }
        }
    }

    private int getSizeMatrix(char[][] matrix) {
        int size = 0;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                size++;
            }
        }

        return size;
    }

    private Boolean checkIfMatrixIsSquare(char[][] matrix) {
        double sqrt = Math.sqrt(getSizeMatrix(matrix));
        return ((sqrt - Math.floor(sqrt)) == 0);
    }

    public Oggetto getNearestObject(List<Oggetto> oggetti, Robot robot) {
        ArrayList<Oggetto> oggettiInProssimita = getObjectsInProximity(oggetti,robot);
        if(oggettiInProssimita.size() > 0)
        {
            double iDistance;
            double distanceNearestObject = sqrt((pow(oggettiInProssimita.get(0).getX() - robot.getX(), 2) + pow(oggettiInProssimita.get(0).getY() - robot.getY(), 2)));
            Oggetto nearestObject = oggetti.get(0);

            for (int i = 1; i < oggettiInProssimita.size(); i++) {
                iDistance = sqrt((pow(oggettiInProssimita.get(i).getX() - robot.getX(), 2) + pow(oggettiInProssimita.get(i).getY() - robot.getY(), 2)));
                if (iDistance < distanceNearestObject) {
                    nearestObject = oggettiInProssimita.get(i);
                }
            }

            return nearestObject;
        }


        return null;

    }

        ArrayList<Oggetto> getObjectsInProximity(List<Oggetto> oggetti, Robot robot) {
            ArrayList<Oggetto> result = new ArrayList<>();
            for (Oggetto object : oggetti) {
                if (distance(robot.getX(), robot.getY(), object.getX(), object.getY()) <= 2) {
                    result.add(object);
                }
            }
            return result;
    }

    public double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

/*
    public void aggiungiOggetto(char color) {
        OggettoFactory factory = null;
        if (color == 'R') {
            factory = new OggettoRossoFactory();
        } else if (color == 'G') {
            factory = new OggettoVerdeFactory();
        } else if (color == 'C') {
        factory = new OggettoCianoFactory();
    } else if (color == 'G') {
        factory = new OggettoGialloFactory();
        }
        oggetti.add(factory.creaOggetto());
    }
*/

    public boolean[][] getPathRobot() {
        return pathRobot;
    }

    public char[][] getLabyrinth() {
        return labirinto;
    }

    public int getRobotX() {
        return robot.getX();
    }

    public int getRobotY() {
        return robot.getY();
    }

    public List<Oggetto> getObjects() {
        return oggetti;
    }

    /*public int getStateRobot() {
        return robot.state;
    }*/

    public Robot getRobot() {
        return robot;
    }


    public void addOggetto(char color, int x, int y) {
        OggettoFactory factory = null;
        if (color == 'R') {
            factory = new OggettoRossoFactory();
        } else if (color == 'G') {
            factory = new OggettoVerdeFactory();
        } else if (color == 'C') {
            factory = new OggettoCianoFactory();
        } else if (color == 'Y') {
            factory = new OggettoGialloFactory();
        }
        Oggetto nuovoOggetto = factory.creaOggetto(x, y);
        oggetti.add(nuovoOggetto);
        notifyObservers(nuovoOggetto, OGGETTO_AGGIUNTO);
    }

    public void removeOggetto(Oggetto oggetto) {
        oggetti.remove(oggetto);
        notifyObservers(oggetto, OGGETTO_RIMOSSO);
    }


    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(Entita entita, int eventType) {
        for (Observer observer : observers) {
            observer.update(entita, eventType);
        }
    }
}