public class Level {
    private char[][] labirinto;
    private int altezza;
    private int larghezza;
    private int portaUscita = -1;

    private int robotX, robotY;

    private Level(Builder builder) {
        this.labirinto = builder.labirinto;
        this.portaUscita = builder.portaUscita;
        this.altezza = builder.altezza;
        this.larghezza = builder.larghezza;
        this.robotX = builder.robotX;
        this.robotY = builder.robotY;
    }

    public char[][] getLabyrinth()
    {
        return labirinto;
    }
    public int getExit()
    {
        return portaUscita;
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
        private char[][] labirinto;
        private int portaUscita = -1;
        private int altezza;
        private int larghezza;
        private int robotX, robotY;

        public Builder(int larghezza, int altezza) {
            this.labirinto = new char[altezza][larghezza];
            for (int i = 0; i < altezza; i++) {
                for (int j = 0; j < larghezza; j++) {
                    this.labirinto[i][j] = ' ';
                }
            }
            this.altezza = altezza;
            this.larghezza = larghezza;
        }

        public Builder aggiungiPareteOrizzontale(int riga, int colonnaInizio, int colonnaFine) {
            for (int i = colonnaInizio; i <= colonnaFine; i++) {
                this.labirinto[riga][i] = '#';
            }
            return this;
        }

        public Builder aggiungiPareteVerticale(int colonna, int rigaInizio, int rigaFine) {
            for (int i = rigaInizio; i <= rigaFine; i++) {
                this.labirinto[i][colonna] = '#';
            }
            return this;
        }

        public Builder aggiungiPuntoParete(int x, int y) {
            this.labirinto[x][y] = '#';
            return this;
        }


        public Builder aggiungiPareti()
        {
            // Inizializzo il labirinto con le pareti esterne
            for (int i = 0; i < labirinto.length; i++) {
                for (int j = 0; j < labirinto[i].length; j++) {
                    if (i == 0 || i == labirinto.length - 1 || j == 0 || j == labirinto[i].length - 1) {
                        labirinto[i][j] = '#';
                    }
                }
            }
            return this;
        }

        public Builder impostaPosizionePortaUscita(int riga, int colonna) {
            aggiungiPareti();

            if(riga == 0 && colonna == 0)
            {
                labirinto[0][0] = ' ';
                labirinto[1][0] = ' ';
                labirinto[0][1] = ' ';
            }
            else if (riga == 0 && colonna == larghezza-1){
                labirinto[0][larghezza-1] = ' ';
                labirinto[0][larghezza-2] = ' ';
                labirinto[1][larghezza-1] = ' ';
            }
            else if(riga == altezza-1 && colonna == 0) {
                labirinto[altezza-1][0] = ' ';
                labirinto[altezza-1][1] = ' ';
                labirinto[altezza-2][0] = ' ';
            }
            else if(riga == altezza-1 && colonna == larghezza-1) {
                labirinto[altezza-1][larghezza-1] = ' ';
                labirinto[altezza-1][larghezza-2] = ' ';
                labirinto[altezza-2][larghezza-1] = ' ';
            }
            else if(riga == 0 || riga == altezza-1)
            {
                labirinto[riga][colonna] = ' ';
                labirinto[riga][colonna+1] = ' ';
                labirinto[riga][colonna-1] = ' ';
            }
            else if(colonna == 0 || colonna == larghezza-1)
            {
                labirinto[riga][colonna] = ' ';
                labirinto[riga+1][colonna] = ' ';
                labirinto[riga-1][colonna] = ' ';
            }
            this.portaUscita = riga * labirinto[0].length + colonna;
            return this;
        }

        public Builder setRobotStartXY (int x,int y)
        {
            if(labirinto[x][y] != '#')
            {
                this.robotX = x;
                this.robotY = y;
            }
            return this;
        }

        public Builder setRobotStartX(int x)
        {
            if(labirinto[x][robotY] != '#')
            {
                robotX = x;
            }
            return this;
        }

        public Builder setRobotStartY(int y)
        {
            if(labirinto[robotX][y] != '#')
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
