public class Level {
    private char[][] labyrinth;
    private int height;
    private int width;
    private int exit = -1;

    private int robotX, robotY;

    private Level(Builder builder) {
        this.labyrinth = builder.labyrinth;
        this.exit = builder.exit;
        this.height = builder.height;
        this.width = builder.width;
        this.robotX = builder.robotX;
        this.robotY = builder.robotY;
    }

    public char[][] getLabyrinth()
    {
        return labyrinth;
    }
    public int getExit()
    {
        return exit;
    }


    public int getRobotX()
    {
        return robotX;
    }

    public int getRobotY()
    {
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

        public Builder addHorizontalWall(int row, int startingColumn, int endingColumn) {
            for (int i = startingColumn; i <= endingColumn; i++) {
                this.labyrinth[row][i] = '#';
            }
            return this;
        }

        public Builder addVerticalWall(int column, int startingRow, int endingRow) {
            for (int i = startingRow; i <= endingRow; i++) {
                this.labyrinth[i][column] = '#';
            }
            return this;
        }

        public Builder addDotWall(int x, int y) {
            this.labyrinth[x][y] = '#';
            return this;
        }


        public Builder addWalls()
        {
            // Inizializzo il labirinto con le pareti esterne
            for (int i = 0; i < labyrinth.length; i++) {
                for (int j = 0; j < labyrinth[i].length; j++) {
                    if (i == 0 || i == labyrinth.length - 1 || j == 0 || j == labyrinth[i].length - 1) {
                        labyrinth[i][j] = '#';
                    }
                }
            }
            return this;
        }

        public Builder setExit(int row, int column) {
            addWalls();

            if(row == 0 && column == 0)
            {
                labyrinth[0][0] = ' ';
                labyrinth[1][0] = ' ';
                labyrinth[0][1] = ' ';
            }
            else if (row == 0 && column == width -1){
                labyrinth[0][width -1] = ' ';
                labyrinth[0][width -2] = ' ';
                labyrinth[1][width -1] = ' ';
            }
            else if(row == height -1 && column == 0) {
                labyrinth[height -1][0] = ' ';
                labyrinth[height -1][1] = ' ';
                labyrinth[height -2][0] = ' ';
            }
            else if(row == height -1 && column == width -1) {
                labyrinth[height -1][width -1] = ' ';
                labyrinth[height -1][width -2] = ' ';
                labyrinth[height -2][width -1] = ' ';
            }
            else if(row == 0 || row == height -1)
            {
                labyrinth[row][column] = ' ';
                labyrinth[row][column+1] = ' ';
                labyrinth[row][column-1] = ' ';
            }
            else if(column == 0 || column == width -1)
            {
                labyrinth[row][column] = ' ';
                labyrinth[row+1][column] = ' ';
                labyrinth[row-1][column] = ' ';
            }
            this.exit = row * labyrinth[0].length + column;
            return this;
        }

        public Builder setRobotStartXY (int x,int y)
        {
            if(labyrinth[x][y] != '#')
            {
                this.robotX = x;
                this.robotY = y;
            }
            return this;
        }

        public Builder setRobotStartX(int x)
        {
            if(labyrinth[x][robotY] != '#')
            {
                robotX = x;
            }
            return this;
        }

        public Builder setRobotStartY(int y)
        {
            if(labyrinth[robotX][y] != '#')
            {
                robotY = y;
            }
            return this;
        }


        public Level build() {
            return new Level(this);
        }
    }
}
