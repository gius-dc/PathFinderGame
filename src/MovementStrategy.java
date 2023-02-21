/**
 * Interfaccia che rappresenta una strategia di movimento.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */
public interface MovementStrategy {
    void move(RobotEntity robot, char[][] matrix, int sizeMatrix, int exitN);
}