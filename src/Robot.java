// In questa classe viene implementato il design pattern 'State'
public class Robot extends Entita {
    /* La classe Robot estende la classe Entita e mantiene un riferimento allo stato attuale del robot utilizzando
    una variabile di istanza della classe RobotState. Inizialmente, lo stato del robot è impostato su PursuitState.
    Il metodo updateState viene chiamato per cambiare lo stato del robot in base all'oggetto più vicino e la classe
    setState viene utilizzata per impostare il nuovo stato. */

    private RobotState state;

    public Robot(int x, int y) {
        setTipo('@');
        setX(x);
        setY(y);
        state = new PursuitState();
    }

    public void updateState(Oggetto nearestObj) {
        state.updateState(this, nearestObj);
        //System.out.println("La classe attuale è: " + state.getClass().getName());
    }
    public void setState(RobotState newState) {
        state = newState;
    }

    public RobotState getState()
    {
        return state;
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
        switch(nearestObj.getTipo()) {
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

class SeekState extends RobotState {
    public void updateState(Robot robot, Oggetto nearestObj) {
        switch(nearestObj.getTipo()) {
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

class FleeState extends RobotState {
    public void updateState(Robot robot, Oggetto nearestObj) {
        switch(nearestObj.getTipo()) {
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

class EvadeState extends RobotState {
    public void updateState(Robot robot, Oggetto nearestObj) {
        switch(nearestObj.getTipo()) {
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
