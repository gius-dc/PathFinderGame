/**
 * L'interfaccia RobotState è la classe base per tutti gli stati del robot e ha un metodo
 * astratto updateState che deve essere implementato da tutte le sue classi estese.
 *
 * @author Giuseppe Della Corte
 * @author Anna Greco
 * @author Sara Flauto
 */
public interface RobotState {
    void updateState(RobotEntity robot, ObjectEntity nearestObj);
}