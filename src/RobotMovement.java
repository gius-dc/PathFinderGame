import java.util.Random;
/**
 * Classe che gestisce il movimento del robot.
 * Qui è stato applicato il design pattern Strategy.
 */
public class RobotMovement {
    private MovementStrategy strategy; // Variabile che rappresenta la strategia di movimento.

    /**
     * Imposta la strategia di movimento del robot.
     *
     * @param strategy La strategia da impostare
     */
    public void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Fa muovere il robot utilizzando la strategia impostata.
     *
     * @param robot L'entità del robot
     * @param matrix La matrice che rappresenta l'ambiente
     * @param sizeMatrix La dimensione della matrice
     * @param exitN Il numero identificativo dell'uscita
     */
    public void move(RobotEntity robot, char[][] matrix, int sizeMatrix, int exitN) {
        strategy.move(robot, matrix, sizeMatrix, exitN);
    }

    /**
     * Interfaccia che rappresenta una strategia di movimento.
     */
    interface MovementStrategy {
        void move(RobotEntity robot, char[][] matrix, int sizeMatrix, int exitN);
    }

    /**
     * Classe che implementa una strategia di movimento casuale.
     */
    static class RandomMovement implements MovementStrategy {
        private Random random = new Random();

        @Override
        public void move(RobotEntity robot, char[][] matrix, int sizeMatrix, int exitN) {
            int min = random.nextInt(8);
            int x = robot.getX();
            int y = robot.getY();

            if (min == 0) {
                if (matrix[x - 1][y] != '#') {
                    robot.setX(x - 1);
                    robot.setY(y);
                }
            } else if (min == 1) {
                if (matrix[x + 1][y] != '#') {
                    robot.setX(x + 1);
                    robot.setY(y);
                }
            } else if (min == 2) {
                if (matrix[x][y - 1] != '#') {
                    robot.setX(x);
                    robot.setY(y - 1);
                }
            } else if (min == 3) {
                if (matrix[x][y + 1] != '#') {
                    robot.setX(x);
                    robot.setY(y + 1);
                }
            } else if (min == 4) {
                if (matrix[x + 1][y - 1] != '#') {
                    robot.setX(x + 1);
                    robot.setY(y - 1);
                }
            } else if (min == 5) {
                if (matrix[x + 1][y + 1] != '#') {
                    robot.setX(x + 1);
                    robot.setY(y + 1);
                }
            } else if (min == 6) {
                if (matrix[x - 1][y - 1] != '#') {
                    robot.setX(x - 1);
                    robot.setY(y - 1);
                }
            } else {
                if (matrix[x - 1][y + 1] != '#') {
                    robot.setX(x - 1);
                    robot.setY(y + 1);
                }
            }
        }
    }

    /**
     * Classe che implementa una strategia di movimento ottimale.
     * Viene utilizzato l'algoritmo Dijkstra per determinare la direzione che consente al robot di effettuare
     * meno passi possibili per raggiungere l'uscita
     */

    static class OptimalMovement implements MovementStrategy {
        @Override
        public void move(RobotEntity robot, char[][] matrix, int sizeMatrix, int exitN) {
            int x = robot.getX(), y = robot.getY();
            Dijkstra p = new Dijkstra();

            int[][] graph = p.generateGraph(16, matrix);
            int[] dist;

            int[] confr = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};

            if (matrix[x - 1][y] != '#' && x > 0 && x < sizeMatrix && y < sizeMatrix) {
                dist = p.run(graph, ((x - 1) * sizeMatrix) + y, sizeMatrix);
                confr[0] = dist[exitN];
            }
            if (matrix[x + 1][y] != '#' && x < sizeMatrix && y < sizeMatrix) {
                dist = p.run(graph, ((x + 1) * sizeMatrix) + y, sizeMatrix);
                confr[1] = dist[exitN];
            }
            if (matrix[x][y - 1] != '#' && x < sizeMatrix && y > 0 && y < sizeMatrix) {
                dist = p.run(graph, (x * sizeMatrix) + (y - 1), sizeMatrix);
                confr[2] = dist[exitN];
            }
            if (matrix[x][y + 1] != '#' && x < sizeMatrix && y < sizeMatrix) {
                dist = p.run(graph, (x * sizeMatrix) + (y + 1), sizeMatrix);
                confr[3] = dist[exitN];
            }
            if (matrix[x + 1][y - 1] != '#' && x < sizeMatrix && y > 0 && y < sizeMatrix) {
                dist = p.run(graph, ((x + 1) * sizeMatrix) + (y - 1), sizeMatrix);
                confr[4] = dist[exitN];
            }
            if (matrix[x + 1][y + 1] != '#' && x < sizeMatrix && y < sizeMatrix) {
                dist = p.run(graph, ((x + 1) * sizeMatrix) + (y + 1), sizeMatrix);
                confr[5] = dist[exitN];
            }
            if (matrix[x - 1][y - 1] != '#' && x > 0 && x < sizeMatrix && y > 0 && y < sizeMatrix) {
                dist = p.run(graph, ((x - 1) * sizeMatrix) + (y - 1), sizeMatrix);
                confr[6] = dist[exitN];
            }
            if (matrix[x - 1][y + 1] != '#' && x > 0 && x < sizeMatrix && y < sizeMatrix) {
                dist = p.run(graph, ((x - 1) * sizeMatrix) + (y + 1), sizeMatrix);
                confr[7] = dist[exitN];
            }

            int min = 0;

            for (int i = 0; i < 8; i++) {
                if (confr[i] < confr[min]) {
                    min = i;
                }
            }


            switch (min) {
                case 0 -> {
                    if (matrix[x - 1][y] != '#') {
                        robot.setX(x - 1);
                        robot.setY(y);
                    }
                }
                case 1 -> {
                    if (matrix[x + 1][y] != '#') {
                        robot.setX(x + 1);
                        robot.setY(y);
                    }
                }
                case 2 -> {
                    if (matrix[x][y - 1] != '#') {
                        robot.setX(x);
                        robot.setY(y - 1);
                    }
                }
                case 3 -> {
                    if (matrix[x][y + 1] != '#') {
                        robot.setX(x);
                        robot.setY(y + 1);
                    }
                }
                case 4 -> {
                    if (matrix[x + 1][y - 1] != '#') {
                        robot.setX(x + 1);
                        robot.setY(y - 1);
                    }
                }
                case 5 -> {
                    if (matrix[x + 1][y + 1] != '#') {
                        robot.setX(x + 1);
                        robot.setY(y + 1);
                    }
                }
                case 6 -> {
                    if (matrix[x - 1][y - 1] != '#') {
                        robot.setX(x - 1);
                        robot.setY(y - 1);
                    }
                }
                case 7 -> {
                    if (matrix[x - 1][y + 1] != '#') {
                        robot.setX(x - 1);
                        robot.setY(y + 1);
                    }
                }
            }


        }
    }
}
