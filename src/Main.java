import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        Labirinto l = new Labirinto("l");
        int flag = 1;

        if(flag == 0)
        {
            //l.calculateCost();
            //l.scelta();
        }
        else if (flag == 1)
        {
            while (l.iterate())
            {
                System.out.print("\033[H\033[2J"); // Pulisce il terminale
                l.stampa();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // ricava il percorso
            boolean percorso[][] = l.getPathRobot();
            char lab[][] = l.getLabyrinth();
            System.out.print("\033[H\033[2J");
            System.out.println("Il robot ha raggiunto la destinazione, ecco il percorso che ha effettuato:");
            for(int i = 0; i < 16; i++) // aggiusterò il size, per ora è 16...
            {
                for(int j = 0; j < 16; j++)
                {
                    if(lab[i][j] != '#')
                    {
                        if(percorso[i][j] == true)
                        {
                            System.out.printf("ο  ");
                        }
                        else{
                            System.out.printf("   ");
                        }
                    }
                    else{
                        System.out.printf("#  ");
                    }
                }
                System.out.println();
            }
        }
        else if (flag == 2)
        {
            State stato = new State();
            stato.simulaMacchinaStati();
        }
    }








}