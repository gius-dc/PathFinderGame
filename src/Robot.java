import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// In questa classe viene implementato i design pattern 'State' e 'Memento'
public class Robot extends Entita {
    /* La classe Robot estende la classe Entita e mantiene un riferimento allo stato attuale del robot utilizzando
    una variabile di istanza della classe RobotState. Inizialmente, lo stato del robot è impostato su PursuitState.
    Il metodo updateState viene chiamato per cambiare lo stato del robot in base all'oggetto più vicino e la classe
    setState viene utilizzata per impostare il nuovo stato. */

    private RobotState state;

    public Robot(int x, int y) {
        setX(x);
        setY(y);
        state = new PursuitState();
    }

    public void updateState(Oggetto nearestObj) {
        state.updateState(this, nearestObj);
    }

    public void setState(RobotState newState) {
        state = newState;
    }

    public RobotState getState() {
        return state;
    }

    public Memento saveToMemento() {
        return new Memento(getX(), getY());
    }

    public void restoreFromMemento(Memento memento) {
        setX(memento.getX());
        setY(memento.getY());
    }
}

abstract class RobotState {
    /* La classe astratta RobotState è la classe base per tutti gli stati del robot e ha un metodo
    astratto updateState che deve essere implementato da tutte le sue classi estese. */
    public abstract void updateState(Robot robot, Oggetto nearestObj);
}

/* Il design pattern State consente di alterare il comportamento dei metodi della classe in base al suo stato interno.
In questo caso nella classe Robot il metodo updateState() deve variare in base allo stato in cui si trova il Robot.
Il metodo è astratto e la sua implementazione viene effettuata nelle classi PursuitState, SeekState, FleeState e EvadeState che estendono la classe RobotState.
Nei metodi updateState() vengono utilizzati i switch case per controllare il tipo dell'oggetto più vicino e impostare lo stato corretto
del robot in base a questo. Ad esempio, se l'oggetto più vicino è di tipo 'R', il robot passerà allo stato SeekState. */

class PursuitState extends RobotState {
    public void updateState(Robot robot, Oggetto nearestObj) {
        if (nearestObj != null) {
            switch (nearestObj.getColor()) {
                case 'R':
                    robot.setState(new SeekState());
                    break;
                case 'Y':
                    robot.setState(new FleeState());
                    break;
                case 'C':
                    robot.setState(new EvadeState());
                    break;
            }
        }
    }
}

class SeekState extends RobotState {
    public void updateState(Robot robot, Oggetto nearestObj) {
        if (nearestObj != null) {
            switch (nearestObj.getColor()) {
                case 'Y':
                    robot.setState(new FleeState());
                    break;
                case 'C':
                    robot.setState(new EvadeState());
                    break;
                case 'G':
                    robot.setState(new PursuitState());
                    break;
            }
        }
    }
}

class FleeState extends RobotState {
    public void updateState(Robot robot, Oggetto nearestObj) {
        if (nearestObj != null) {
            switch (nearestObj.getColor()) {
                case 'C':
                    robot.setState(new EvadeState());
                    break;
                case 'G':
                    robot.setState(new PursuitState());
                    break;
                case 'R':
                    robot.setState(new SeekState());
                    break;
            }
        }
    }
}

class EvadeState extends RobotState {
    public void updateState(Robot robot, Oggetto nearestObj) {
        if (nearestObj != null) {
            switch (nearestObj.getColor()) {
                case 'G':
                    robot.setState(new PursuitState());
                    break;
                case 'R':
                    robot.setState(new SeekState());
                    break;
                case 'Y':
                    robot.setState(new FleeState());
                    break;
            }
        }
    }
}


// Memento
class Memento {
    private int x;
    private int y;

    public Memento(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Caretaker {
    private List<Memento> mementos = new ArrayList<>();

    public void addMemento(Memento memento) {
        mementos.add(memento);
    }

    public Memento getMemento(int index) {
        return mementos.get(index);
    }

    public int sizeMemento() {
        return mementos.size();
    }
}