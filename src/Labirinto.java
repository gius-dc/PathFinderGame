import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Labirinto {
    // Dimensione del labirinto
    private static final int DIMENSIONE = 16;
    private Robot robot;
    private String nome;
    private String stato;
    private List<Oggetto> oggetti;
    private int passi;
    private Random random;
    private char[][] labirinto;

    private boolean[][] pathRobot = new boolean[DIMENSIONE][DIMENSIONE];
    // Costruttore
    public Labirinto(String nome) {
        nome = nome;
        robot = new Robot(0,0);
        oggetti = new ArrayList<>();
        passi = 0;
        random = new Random();
        labirinto = new char[DIMENSIONE][DIMENSIONE];

        // Inizializzo il labirinto con le pareti esterne
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                if (i == 0 || i == DIMENSIONE - 1 || j == 0 || j == DIMENSIONE - 1) {
                    labirinto[i][j] = '#';
                } else {
                    labirinto[i][j] = ' ';
                }
            }
        }


        // Posiziono il robot in un punto casuale all'interno del labirinto (che non coincida con la parete)
        do{
            robot.setX(random.nextInt(DIMENSIONE - 2) + 1);
            robot.setY(random.nextInt(DIMENSIONE - 2) + 1);
        }while(labirinto[robot.getX()][robot.getY()] == '#');


        robot.setX(9);
        robot.setY(1);
        // Inserisco le pareti e la via d'uscita
        // Il labirinto deve essere formato così:
        /*
            ################
            #              #
            #  #           #
            #  #   #########
            #  #           #
            #  #           #
            #  #           #
            #  #           #
            #  #   #       #
            #  #   #       #
            #      #   ### #
                   #       #
                   #       #
                   #       #
            #      #       #
            ################
         */

        // Pareti
        labirinto[2][3] = '#';
        labirinto[3][3] = '#';
        labirinto[4][3] = '#';
        labirinto[5][3] = '#';
        labirinto[6][3] = '#';
        labirinto[7][3] = '#';        nome = nome;
        robot = new Robot(0,0);
        oggetti = new ArrayList<>();
        passi = 0;
        random = new Random();
        labirinto = new char[DIMENSIONE][DIMENSIONE];

        // Inizializzo il labirinto con le pareti esterne
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                if (i == 0 || i == DIMENSIONE - 1 || j == 0 || j == DIMENSIONE - 1) {
                    labirinto[i][j] = '#';
                } else {
                    labirinto[i][j] = ' ';
                }
            }
        }


        // Posiziono il robot in un punto casuale all'interno del labirinto (che non coincida con la parete)
        do{
            robot.setX(random.nextInt(DIMENSIONE - 2) + 1);
            robot.setY(random.nextInt(DIMENSIONE - 2) + 1);
        }while(labirinto[robot.getX()][robot.getY()] == '#');

        // Inserisco le pareti e la via d'uscita
        // Il labirinto deve essere formato così:
        /*
            ################
            #              #
            #  #           #
            #  #   #########
            #  #           #
            #  #           #
            #  #           #
            #  #           #
            #  #   #       #
            #  #   #       #
            #      #   ### #
                   #       #
                   #       #
                   #       #
            #      #       #
            ################
         */

        // Pareti
        labirinto[2][3] = '#';
        labirinto[3][3] = '#';
        labirinto[4][3] = '#';
        labirinto[5][3] = '#';
        labirinto[6][3] = '#';
        labirinto[7][3] = '#';
        labirinto[8][3] = '#';
        labirinto[9][3] = '#';

        labirinto[3][7] = '#';
        labirinto[3][8] = '#';
        labirinto[3][9] = '#';
        labirinto[3][10] = '#';
        labirinto[3][11] = '#';
        labirinto[3][12] = '#';
        labirinto[3][13] = '#';
        labirinto[3][14] = '#';

        labirinto[8][7] = '#';
        labirinto[9][7] = '#';
        labirinto[10][7] = '#';
        labirinto[11][7] = '#';
        labirinto[12][7] = '#';
        labirinto[13][7] = '#';
        labirinto[14][7] = '#';

        labirinto[10][11] = '#';
        labirinto[10][12] = '#';
        labirinto[10][13] = '#';


        // Via d'uscita
        labirinto[11][0] = ' ';
        labirinto[12][0] = ' ';
        labirinto[13][0] = ' ';


        // Aggiungi alcuni oggetti nel labirinto
        // (in posizione casuale e che non coincida con la parete)

/*
        int ox, oy;
        // red
        do{
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        }while(labirinto[ox][oy] == '#');random.nextInt(oggetti.size())
        oggetti.add(new Oggetto('R', ox, oy));
        // green
        do{
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        }while(labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('G', ox, oy));
        // yellow
        do{
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        }while(labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('Y', ox, oy));
        // cyan
        do{
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        }while(labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('C', ox, oy));
        /*
 */

        labirinto[8][3] = '#';
        labirinto[9][3] = '#';

        labirinto[3][7] = '#';
        labirinto[3][8] = '#';
        labirinto[3][9] = '#';
        labirinto[3][10] = '#';
        labirinto[3][11] = '#';
        labirinto[3][12] = '#';
        labirinto[3][13] = '#';
        labirinto[3][14] = '#';

        labirinto[8][7] = '#';
        labirinto[9][7] = '#';
        labirinto[10][7] = '#';
        labirinto[11][7] = '#';
        labirinto[12][7] = '#';
        labirinto[13][7] = '#';
        labirinto[14][7] = '#';

        labirinto[10][11] = '#';
        labirinto[10][12] = '#';
        labirinto[10][13] = '#';


        // Via d'uscita
        labirinto[11][0] = ' ';
        labirinto[12][0] = ' ';
        labirinto[13][0] = ' ';



        int ox, oy;
        // red
        do{
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        }while(labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('R', ox, oy));
        // green
        do{
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        }while(labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('G', ox, oy));
        // yellow
        do{
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        }while(labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('Y', ox, oy));
        // cyan
        do{
            ox = random.nextInt(DIMENSIONE - 2) + 1;
            oy = random.nextInt(DIMENSIONE - 2) + 1;
        }while(labirinto[ox][oy] == '#');
        oggetti.add(new Oggetto('C', ox, oy));
    }


    public void stampa() {
        char print;
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                print = labirinto[i][j];
                if (i == robot.getX() && j == robot.getY()) {
                    print = '@';
                } else {
                    for(int k = 0; k < oggetti.size(); k++)
                    {
                        if(oggetti.get(k).getX() == i && oggetti.get(k).getY() == j)
                        {
                            print = oggetti.get(k).getTipo();
                        }
                    }
                }
                System.out.print(print + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }


    public Boolean iterate()
    {
        char c = ' ';
        int r, ox, oy;

        if(random.nextInt(100)%2 == 0)
        {

                // Aggiungi qualche oggetto
                r = random.nextInt(4);
                if(r == 0) {c = 'R';}
                else if (r == 1) {c = 'G';}
                else if (r == 2) {c = 'Y';}
                else if (r == 3) {c = 'C';}

                for(int i = 0; i < 3; i++)
                {
                    do{
                        ox = random.nextInt(DIMENSIONE - 2) + 1;
                        oy = random.nextInt(DIMENSIONE - 2) + 1;
                    }while(labirinto[ox][oy] == '#');

                    oggetti.add(new Oggetto(c,ox,oy));
                }

        }
        else {
            // Fai scomparire qualche oggetto

            if(oggetti.size() > 0)
            {
                try{

                }catch(Exception e)
                {
                    oggetti.remove(random.nextInt(oggetti.size()));
                    oggetti.remove(random.nextInt(oggetti.size()));
                    oggetti.remove(random.nextInt(oggetti.size()));
                }
            }
        }

        // Prossimo passo robot
        if(!(robot.getX() == 12 && robot.getY() == 0))
        {
            doStepRobot();
            return true;
        }
        else
        {
            return false;
        }
    }

    public void doStepRobot()
    {
        int x = robot.getX(), y = robot.getY();
        ShortestPath p = new ShortestPath();
        char matrice[][] = new char[DIMENSIONE][DIMENSIONE];
        for(int i = 0; i < DIMENSIONE; i++)
        {
            for(int j = 0; j < DIMENSIONE; j++)
            {
                matrice[i][j] = labirinto[i][j];
            }
        }

        for(int i = 0; i < oggetti.size(); i++)
        {
            matrice[oggetti.get(i).getX()][oggetti.get(i).getY()] = '#';
        }

        int graph[][] = p.generateGraph(DIMENSIONE,matrice);

        int source = 30;
        int dist[];

        int confr[] = {Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE};


        if(matrice[x-1][y] != '#')
        {
                dist = p.dijkstra(graph,((x-1)*DIMENSIONE)+y,DIMENSIONE);
                confr[0] = dist[192];
        }
        if(matrice[x+1][y] != '#')
        {
                dist = p.dijkstra(graph,((x+1)*DIMENSIONE)+y,DIMENSIONE);
                confr[1] = dist[192];
        }
        if(matrice[x][y-1] != '#')
        {
                dist = p.dijkstra(graph,(x*DIMENSIONE)+(y-1),DIMENSIONE);
                confr[2] = dist[192];
        }
        if(matrice[x][y+1] != '#')
        {
                dist = p.dijkstra(graph,(x*DIMENSIONE)+(y+1),DIMENSIONE);
                confr[3] = dist[192];
        }

        int min = 0;
        for(int i = 0; i < 4; i++)
        {
            if(confr[i] < confr[min])
            {
                min = i;
            }
        }

        if(min == 0)
        {
            if(matrice[x-1][y] != '#') {
                robot.setX(x - 1);
                robot.setY(y);
                pathRobot[x-1][y] = true;
            }
        }
        else if(min == 1)
        {
            if(matrice[x+1][y] != '#') {
                robot.setX(x + 1);
                robot.setY(y);
                pathRobot[x+1][y] = true;
            }
        }
        else if(min == 2)
        {
            if(matrice[x][y-1] != '#') {
                robot.setX(x);
                robot.setY(y - 1);
                pathRobot[x][y-1] = true;
            }
        }
        else if(min == 3)
        {
            if(matrice[x][y+1] != '#') {
                robot.setX(x);
                robot.setY(y + 1);
                pathRobot[x][y+1] = true;
            }
        }
    }

    public boolean[][] getPathRobot()
    {
        return pathRobot;
    }

    public char[][] getLabyrinth()
    {
        return labirinto;
    }
}