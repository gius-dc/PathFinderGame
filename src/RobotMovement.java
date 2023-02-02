import java.util.Random;

public class RobotMovement {
    private MovementStrategy strategy;

    public void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    public void move(RobotEntity robot, char[][] matrice, int sizeMatrix, int exitN) {
        strategy.move(robot, matrice, sizeMatrix, exitN);
    }

    interface MovementStrategy {
        void move(RobotEntity robot, char[][] matrice, int sizeMatrix, int exitN);
    }

    static class RandomMovement implements MovementStrategy {
        private Random random = new Random();

        @Override
        public void move(RobotEntity robot, char[][] matrice, int sizeMatrix, int exitN) {
            int min = random.nextInt(8);
            int x = robot.getX();
            int y = robot.getY();

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
    }

    static class OptimalMovement implements MovementStrategy {
        @Override
        public void move(RobotEntity robot, char[][] matrice, int sizeMatrix, int exitN) {
            int x = robot.getX(), y = robot.getY();
            ShortestPath p = new ShortestPath();

            int graph[][] = p.generateGraph(16, matrice);
            int dist[];

            int confr[] = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};

            if (matrice[x - 1][y] != '#' && x >= 0 && x < sizeMatrix && y >= 0 && y < sizeMatrix) {
                dist = p.dijkstra(graph, ((x - 1) * sizeMatrix) + y, sizeMatrix);
                confr[0] = dist[exitN];
            }
            if (matrice[x + 1][y] != '#' && x >= 0 && x < sizeMatrix && y >= 0 && y < sizeMatrix) {
                dist = p.dijkstra(graph, ((x + 1) * sizeMatrix) + y, sizeMatrix);
                confr[1] = dist[exitN];
            }
            if (matrice[x][y - 1] != '#' && x >= 0 && x < sizeMatrix && y >= 0 && y < sizeMatrix) {
                dist = p.dijkstra(graph, (x * sizeMatrix) + (y - 1), sizeMatrix);
                confr[2] = dist[exitN];
            }
            if (matrice[x][y + 1] != '#' && x >= 0 && x < sizeMatrix && y >= 0 && y < sizeMatrix) {
                dist = p.dijkstra(graph, (x * sizeMatrix) + (y + 1), sizeMatrix);
                confr[3] = dist[exitN];
            }
            if (matrice[x + 1][y - 1] != '#' && x >= 0 && x < sizeMatrix && y >= 0 && y < sizeMatrix) {
                dist = p.dijkstra(graph, ((x + 1) * sizeMatrix) + (y - 1), sizeMatrix);
                confr[4] = dist[exitN];
            }
            if (matrice[x + 1][y + 1] != '#' && x >= 0 && x < sizeMatrix && y >= 0 && y < sizeMatrix) {
                dist = p.dijkstra(graph, ((x + 1) * sizeMatrix) + (y + 1), sizeMatrix);
                confr[5] = dist[exitN];
            }
            if (matrice[x - 1][y - 1] != '#' && x >= 0 && x < sizeMatrix && y >= 0 && y < sizeMatrix) {
                dist = p.dijkstra(graph, ((x - 1) * sizeMatrix) + (y - 1), sizeMatrix);
                confr[6] = dist[exitN];
            }
            if (matrice[x - 1][y + 1] != '#' && x >= 0 && x < sizeMatrix && y >= 0 && y < sizeMatrix) {
                dist = p.dijkstra(graph, ((x - 1) * sizeMatrix) + (y + 1), sizeMatrix);
                confr[7] = dist[exitN];
            }

            int min = 0;

            for (int i = 0; i < 8; i++) {
                if (confr[i] < confr[min]) {
                    min = i;
                }
            }

            System.out.println("Passi necessari:");
            System.out.println("Sinistra: " + confr[0]);
            System.out.println("Destra: " + confr[1]);
            System.out.println("Su: " + confr[2]);
            System.out.println("Giu: " + confr[3]);

            System.out.println("Su-destra: " + confr[4]);
            System.out.println("Giu-destra: " + confr[5]);
            System.out.println("Su-sinistra: " + confr[6]);
            System.out.println("Giu-sinistra: " + confr[7]);

            System.out.println("Il passo minore richiesto è: " + min);

            if (min == 0) {
                if (matrice[x - 1][y] != '#') {
                    System.out.println("Vado su");
                    robot.setX(x - 1);
                    robot.setY(y);
                }
            } else if (min == 1) {
                if (matrice[x + 1][y] != '#') {
                    System.out.println("Vado giu");
                    robot.setX(x + 1);
                    robot.setY(y);
                }
            } else if (min == 2) {
                if (matrice[x][y - 1] != '#') {
                    System.out.println("Vado sinistra");
                    robot.setX(x);
                    robot.setY(y - 1);
                }
            } else if (min == 3) {
                if (matrice[x][y + 1] != '#') {
                    System.out.println("Vado destra");
                    robot.setX(x);
                    robot.setY(y + 1);
                }
            } else if (min == 4) {
                if (matrice[x + 1][y - 1] != '#') {
                    System.out.println("Vado giu-sinistra");
                    robot.setX(x + 1);
                    robot.setY(y - 1);
                }
            } else if (min == 5) {
                if (matrice[x + 1][y + 1] != '#') {
                    System.out.println("Vado giu-destra");
                    robot.setX(x + 1);
                    robot.setY(y + 1);
                }
            } else if (min == 6) {
                if (matrice[x - 1][y - 1] != '#') {
                    System.out.println("Vado su-sinistra");
                    robot.setX(x - 1);
                    robot.setY(y - 1);
                }
            } else if (min == 7) {
                if (matrice[x - 1][y + 1] != '#') {
                    System.out.println("Vado su-destra");
                    robot.setX(x - 1);
                    robot.setY(y + 1);
                }
            }
        }


    }
}
