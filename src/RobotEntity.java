/**
 * Classe che rappresenta un robot che estende l'entità e mantiene un riferimento allo stato attuale del robot (pursuit, seek, flee, evade).
 * Implementa anche i design pattern State e Memento.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */


public class RobotEntity implements Entity {
    private RobotState state;
    private int x;
    private int y;

    public RobotEntity(int x, int y) {
        setX(x);
        setY(y);
        state = new PursuitState();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    public void updateState(ObjectEntity nearestObj) {
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


/** Il design pattern State consente di alterare il comportamento dei metodi della classe in base al suo stato interno.
In questo caso nella classe Robot il metodo updateState() deve variare in base allo stato in cui si trova il Robot.
Il metodo è astratto e la sua implementazione viene effettuata nelle classi PursuitState, SeekState, FleeState ed EvadeState che estendono la classe RobotState.
Nei metodi updateState() vengono utilizzati i switch case per controllare il tipo dell'oggetto più vicino e impostare lo stato corretto
del robot in base a questo. Ad esempio, se l'oggetto più vicino è di tipo 'R', il robot passerà allo stato SeekState. */

class PursuitState implements RobotState {
    public void updateState(RobotEntity robot, ObjectEntity nearestObj) {
        if (nearestObj != null) {
            switch (nearestObj.getColor()) {
                case 'R' -> robot.setState(new SeekState());
                case 'Y' -> robot.setState(new FleeState());
                case 'C' -> robot.setState(new EvadeState());
            }
        }
    }
}

class SeekState implements RobotState {
    public void updateState(RobotEntity robot, ObjectEntity nearestObj) {
        if (nearestObj != null) {
            switch (nearestObj.getColor()) {
                case 'Y' -> robot.setState(new FleeState());
                case 'C' -> robot.setState(new EvadeState());
                case 'G' -> robot.setState(new PursuitState());
            }
        }
    }
}

class FleeState implements RobotState {
    public void updateState(RobotEntity robot, ObjectEntity nearestObj) {
        if (nearestObj != null) {
            switch (nearestObj.getColor()) {
                case 'C' -> robot.setState(new EvadeState());
                case 'G' -> robot.setState(new PursuitState());
                case 'R' -> robot.setState(new SeekState());
            }
        }
    }
}

class EvadeState implements RobotState {
    public void updateState(RobotEntity robot, ObjectEntity nearestObj) {
        if (nearestObj != null) {
            switch (nearestObj.getColor()) {
                case 'G' -> robot.setState(new PursuitState());
                case 'R' -> robot.setState(new SeekState());
                case 'Y' -> robot.setState(new FleeState());
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

