import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Labirinto l = new Labirinto("l");
        Scanner scanner = new Scanner(System.in);
        String input;
        while (true)
        {
            System.out.print("\033[H\033[2J"); // Pulisce il terminale
            l.stampa();
            //System.out.println("Premi INVIO per continuare...");
            //input = scanner.nextLine();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            l.itera();
        }



        //Stato stato = new Stato();
        //stato.simulaMacchinaStati();
    }
}