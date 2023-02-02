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
    public Labirinto(Level l) throws IllegalAccessException {

        oggetti = new ArrayList<>();
        passi = 0;
        random = new Random();
        labirinto = l.getLabyrinth();
        exitN = l.getExit();


        if (!checkIfMatrixIsSquare(labirinto)) {
            throw new IllegalAccessException("Il livello contiene una matrice non quadrata.");
        }

        DIMENSIONE = (int) sqrt(getSizeMatrix(labirinto));


        robot = new Robot(l.getRobotX(), l.getRobotY());


        // Posiziono il robot in un punto casuale all'interno del labirinto (che non coincida con la parete)
        /*
        do {
            robot.setX(random.nextInt(DIMENSIONE - 2) + 1);
            robot.setY(random.nextInt(DIMENSIONE - 2) + 1);
        } while (labirinto[robot.getX()][robot.getY()] == '#');
        */


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
        int r = 0, ox = 0, oy = 0, limit = 0;
        Boolean flag = true;

        for(int i = 0; i < oggetti.size(); i++)
        {
            if(oggetti.get(i).getX() == robot.getX() && oggetti.get(i).getY() == robot.getY())
            {
                removeOggetto(oggetti.get(i));
            }
        }

        if (random.nextInt(100) % 2 == 0) {
            for (int i = 0; i < 1; i++) {
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

                if (flag) {
                    do {
                        ox = random.nextInt(DIMENSIONE - 2) + 1;
                        oy = random.nextInt(DIMENSIONE - 2) + 1;
                        limit++;
                    } while ((ox == robot.getX() && oy == robot.getY()) || labirinto[ox][oy] == '#' || distance(robot.getX(), robot.getY(), ox, oy) > 5 || checkIfObjectXYExists(oggetti,ox,oy) && limit < 50);
                    flag = false;
                } else {
                    do {
                        ox = random.nextInt(DIMENSIONE - 2) + 1;
                        oy = random.nextInt(DIMENSIONE - 2) + 1;
                        limit++;
                    } while ((ox == robot.getX() && oy == robot.getY()) || labirinto[ox][oy] == '#' || checkIfObjectXYExists(oggetti,ox,oy) && limit < 50);
                    flag = true;
                }


                addOggetto(c, ox, oy);
            }

        } else {
            // Fai scomparire qualche oggetto
            if (oggetti.size() > 3) {
                if (flag) {
                    r = random.nextInt(oggetti.size());

                    if (distance(robot.getX(), robot.getY(), oggetti.get(r).getX(), oggetti.get(r).getX()) > 3) {
                        try {
                            removeOggetto(oggetti.get(random.nextInt(oggetti.size())));
                        } catch (Exception e) {
                        }
                    }
                    flag = false;
                } else {
                    r = random.nextInt(oggetti.size());
                    try {
                        removeOggetto(oggetti.get(random.nextInt(oggetti.size())));
                    } catch (Exception e) {
                    }
                    flag = true;
                }
            }


        }

        // Prossimo passo robot
        //if (!(robot.getX() == 12 && robot.getY() == 0) && !(robot.getX() == 11 && robot.getY() == 0) && !(robot.getX() == 13 && robot.getY() == 0)) {

        if (!checkIfRobotGoal()) {
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

    private Boolean checkIfRobotGoal() {
        int exitX = exitN / DIMENSIONE;
        int exitY = exitN % DIMENSIONE;
        if (robot.getX() == exitX && robot.getY() == exitY) {
            return true;
        } else if ((exitX == robot.getX()) && (exitX == 0 || exitX == DIMENSIONE - 1)) {
            if (robot.getY() == exitY || robot.getY() == exitY + 1 || robot.getY() == exitY - 1) {
                return true;
            }
        } else if ((exitY == robot.getY()) && (exitY == 0 || exitY == DIMENSIONE - 1)) {
            if (robot.getX() == exitX || robot.getX() == exitX + 1 || robot.getX() == exitX - 1) {
                return true;
            }
        }

        return false;
    }

    public void doStepRobot(Boolean casual) {
        RobotMovement strategy = new RobotMovement();
        if (casual) {
            strategy.setStrategy(new RobotMovement.RandomMovement());
        } else {
            strategy.setStrategy(new RobotMovement.OptimalMovement());
        }

        char matrice[][] = new char[DIMENSIONE][DIMENSIONE];
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                matrice[i][j] = labirinto[i][j];
            }
        }

        for (int i = 0; i < oggetti.size(); i++) {
            matrice[oggetti.get(i).getX()][oggetti.get(i).getY()] = '#';
        }

        strategy.move(robot, matrice, DIMENSIONE, exitN);

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

    private Boolean checkIfObjectXYExists(List<Oggetto> oggetti, int x, int y) {
        for (int i = 0; i < oggetti.size(); i++) {
            if (oggetti.get(i).getX() == x && oggetti.get(i).getY() == y) {
                return true;
            }
        }
        return false;
    }

    public Oggetto getNearestObject(List<Oggetto> oggetti, Robot robot) {
        ArrayList<Oggetto> oggettiInProssimita = getObjectsInProximity(oggetti, robot);
        if (oggettiInProssimita.size() > 0) {
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


    public char[][] getLabyrinth() {
        return labirinto;
    }

    public int getRobotX() {
        return robot.getX();
    }

    public int getRobotY() {
        return robot.getY();
    }


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

    private void notifyObservers(Oggetto oggetto, int eventType) {
        for (Observer observer : observers) {
            observer.update(oggetto, eventType);
        }
    }
}