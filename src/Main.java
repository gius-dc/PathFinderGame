public class Main {
    public static void main(String[] args) {
        char[][] matrice = null;
        int exitN = 0;
        Robot robot = new Robot(3, 6);
        System.out.println("Coordinate prima: (" + robot.getX() + "," + robot.getY() + ")");

        RobotMovement strategy = new RobotMovement();
        strategy.setStrategy(new RobotMovement.RandomMovement());

        strategy.move(robot, matrice, 16, exitN);

        System.out.println("Coordinate dopo: (" + robot.getX() + "," + robot.getY() + ")");

    }
}
