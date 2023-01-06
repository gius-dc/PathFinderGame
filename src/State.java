import java.io.BufferedReader;
import java.io.InputStreamReader;

public class State {

    String stato;
    enum States {pursuit, evade, flee, seek};
    enum Colors {red, green, yellow, cyan};

    public void simulaMacchinaStati()
    {
        // Il codice non è ottimale, è una bozza per il passaggio degli stati che verrà implementato nel progetto

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String stato = "", scelta = "";

        stato = "pursuit";

        do{
            System.out.print("\033[H\033[2J"); // Pulisce il terminale
            System.out.println("Stato attuale: " + stato);
            switch(stato){
                case "seek":
                    System.out.println("Il robot va nella direzione dell’uscita di una singola cella alla volta.");
                    break;
                case "pursuit":
                    System.out.println("Il robot va nella direzione dell’uscita di una singola cella alla volta.");
                    break;
                case "flee":
                    System.out.println("Il robot va nella direzione dell’uscita di due celle alla volta.");
                    break;
                case "evade":
                    System.out.println("Il robot avanza in maniera casuale di una singola cella.");
                    break;
            }
            System.out.println();

            System.out.println("Inserisci il colore dell'oggetto che si trova in prossimità al robot per fargli cambiare stato, oppure digita 0 per uscire.");
            System.out.println("* red");
            System.out.println("* green");
            System.out.println("* yellow");
            System.out.println("* cyan");
            System.out.println("(0) Esci");
            System.out.println("--------");
            do{
                System.out.print("Inserisci la scelta: ");
                try {
                    scelta = br.readLine();
                    if(!scelta.equals("0") && !colorsContains(scelta)) {System.out.println("Scelta non valida, riprova.");}
                }catch(Exception e) {
                    System.out.println(e);
                }
            }while(!scelta.equals("0") && !colorsContains(scelta));

            if(!scelta.equals("0"))
            {
                switch(stato){
                    case "pursuit":
                        switch(scelta){
                            case "red":
                                stato = "seek";
                                break;
                            case "yellow":
                                stato = "flee";
                                break;
                            case "cyan":
                                stato = "evade";
                                break;
                        }
                        break;
                    case "seek":
                        switch(scelta){
                            case "yellow":
                                stato = "flee";
                                break;
                            case "cyan":
                                stato = "evade";
                                break;
                            case "green":
                                stato = "pursuit";
                                break;
                        }
                        break;
                    case "flee":
                        switch(scelta){
                            case "cyan":
                                stato = "evade";
                                break;
                            case "green":
                                stato = "pursuit";
                                break;
                            case "red":
                                stato = "seek";
                                break;
                        }
                        break;
                    case "evade":
                        switch(scelta){
                            case "green":
                                stato = "pursuit";
                                break;
                            case "red":
                                stato = "seek";
                                break;
                            case "yellow":
                                stato = "flee";
                                break;
                        }
                        break;
                }
            }
        }while(!scelta.equals("0"));

        System.out.println("Uscita...");
    }

    public static boolean statesContains(String test) {

        for (States s : States.values()) {
            if (s.name().equals(test)) {
                return true;
            }
        }

        return false;
    }

    public static boolean colorsContains(String test) {

        for (Colors c : Colors.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}
