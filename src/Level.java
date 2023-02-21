/**
 * La classe Level rappresenta il livello di un gioco a labirinto.
 * Questa classe fa parte dell'implementazione del design pattern Builder.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */

public class Level {
    private char[][] labyrinth;
    private int exit;

    private int robotX, robotY;

    /**
     * Costruttore privato della classe Level. Viene utilizzato dalla classe Builder per costruire un oggetto Level.
     *
     * @param builder Builder utilizzato per costruire l'oggetto Level.
     */
    private Level(Builder builder) {
        this.labyrinth = builder.labyrinth;
        this.exit = builder.exit;
        this.robotX = builder.robotX;
        this.robotY = builder.robotY;
    }

    /**
     * Restituisce il labirinto rappresentato da un array di caratteri.
     *
     * @return char[][] Il labirinto rappresentato da un array di caratteri.
     */
    public char[][] getLabyrinth() {
        return labyrinth;
    }

    /**
     * Restituisce la posizione di uscita del labirinto.
     *
     * @return int La posizione di uscita del labirinto.
     */
    public int getExit() {
        return exit;
    }

    /**
     * Restituisce la coordinata X di partenza del robot.
     *
     * @return int La coordinata X di partenza del robot.
     */
    public int getRobotX() {
        return robotX;
    }

    /**
     * Restituisce la coordinata Y di partenza del robot.
     *
     * @return int La coordinata Y di partenza del robot.
     */
    public int getRobotY() {
        return robotY;
    }

    public static class Builder {
        private char[][] labyrinth;
        private int exit = -1;
        private int height;
        private int width;
        private int robotX, robotY;

        public Builder(int width, int height) {
            this.labyrinth = new char[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    this.labyrinth[i][j] = ' ';
                }
            }
            this.height = height;
            this.width = width;
        }

        /**
         * Aggiunge una parete orizzontale al labirinto.
         *
         * @param row            Riga in cui viene posizionata la parete orizzontale.
         * @param startingColumn Colonna di inizio della parete orizzontale.
         * @param endingColumn   Colonna di fine della parete orizzontale.
         */
        public void addHorizontalWall(int row, int startingColumn, int endingColumn) {
            for (int i = startingColumn; i <= endingColumn; i++) {
                this.labyrinth[row][i] = '#';
            }
        }

        /**
         * Aggiunge una parete verticale al labirinto.
         *
         * @param column      Colonna in cui viene posizionata la parete verticale.
         * @param startingRow Riga di inizio della parete verticale.
         * @param endingRow   Riga di fine della parete verticale.
         */
        public void addVerticalWall(int column, int startingRow, int endingRow) {
            for (int i = startingRow; i <= endingRow; i++) {
                this.labyrinth[i][column] = '#';
            }
        }

        /**
         * Aggiunge un singolo punto che rappresenta una parete al labirinto.
         *
         * @param x Posizione x del punto che rappresenta la parete.
         * @param y Posizione y del punto che rappresenta la parete.
         */
        public void addDotWall(int x, int y) {
            this.labyrinth[x][y] = '#';
        }


        /**
         * Aggiunge le pareti esterne al labirinto.
         */
        public void addWalls() {
            // Inizializzo il labirinto con le pareti esterne
            for (int i = 0; i < labyrinth.length; i++) {
                for (int j = 0; j < labyrinth[i].length; j++) {
                    if (i == 0 || i == labyrinth.length - 1 || j == 0 || j == labyrinth[i].length - 1) {
                        labyrinth[i][j] = '#';
                    }
                }
            }
        }

        /**
         * Imposta la posizione dell'uscita dal labirinto.
         *
         * @param row    Riga in cui viene posizionata l'uscita.
         * @param column Colonna in cui viene posizionata l'uscita.
         */
        public void setExit(int row, int column) {
            addWalls();

            if (row == 0 && column == 0) {
                labyrinth[0][0] = ' ';
                labyrinth[1][0] = ' ';
                labyrinth[0][1] = ' ';
            } else if (row == 0 && column == width - 1) {
                labyrinth[0][width - 1] = ' ';
                labyrinth[0][width - 2] = ' ';
                labyrinth[1][width - 1] = ' ';
            } else if (row == height - 1 && column == 0) {
                labyrinth[height - 1][0] = ' ';
                labyrinth[height - 1][1] = ' ';
                labyrinth[height - 2][0] = ' ';
            } else if (row == height - 1 && column == width - 1) {
                labyrinth[height - 1][width - 1] = ' ';
                labyrinth[height - 1][width - 2] = ' ';
                labyrinth[height - 2][width - 1] = ' ';
            } else if (row == 0 || row == height - 1) {
                labyrinth[row][column] = ' ';
                labyrinth[row][column + 1] = ' ';
                labyrinth[row][column - 1] = ' ';
            } else if (column == 0 || column == width - 1) {
                labyrinth[row][column] = ' ';
                labyrinth[row + 1][column] = ' ';
                labyrinth[row - 1][column] = ' ';
            }
            this.exit = row * labyrinth[0].length + column;
        }

        /**
         * Imposta la posizione iniziale del robot nel labirinto.
         *
         * @param x Coordinata x della posizione iniziale del robot.
         * @param y Coordinata y della posizione iniziale del robot.
         */
        public void setRobotStartXY(int x, int y) {
            if (labyrinth[x][y] != '#') {
                this.robotX = x;
                this.robotY = y;
            }
        }

        /**
         * Costruisce e restituisce un oggetto Level con le proprietà specificate nel Builder corrente.
         * @return Oggetto Level con le proprietà specificate nel Builder corrente.
         */
        public Level build() {
            return new Level(this);
        }
    }
}
