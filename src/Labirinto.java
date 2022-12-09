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
        char daStampare;
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                daStampare = labirinto[i][j];
                if (i == robot.getX() && j == robot.getY()) {
                    daStampare = '@';
                } else {
                    for(int k = 0; k < oggetti.size(); k++)
                    {
                        if(oggetti.get(k).getX() == i && oggetti.get(k).getY() == j)
                        {
                            daStampare = oggetti.get(k).getTipo();
                        }
                    }
                }
                System.out.print(daStampare + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }


    public void itera()
    {
        char c = ' ';
        int r, ox, oy;

        if(random.nextInt(2) == 0)
        {
            // Aggiungi qualche oggetto
            r = random.nextInt(4);
            if(r == 0) {c = 'R';}
            else if (r == 1) {c = 'G';}
            else if (r == 2) {c = 'Y';}
            else if (r == 3) {c = 'C';}

            do{
                ox = random.nextInt(DIMENSIONE - 2) + 1;
                oy = random.nextInt(DIMENSIONE - 2) + 1;
            }while(labirinto[ox][oy] == '#');
            oggetti.add(new Oggetto(c, ox, oy));
        }
        else {
            // Fai scomparire qualche oggetto
            if(oggetti.size() > 0)
            {
                oggetti.remove(random.nextInt(oggetti.size()));
            }
        }

    }
}