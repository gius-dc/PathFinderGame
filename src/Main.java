/**
 * Questo è il main dell'applicazione.
 * Imposta il look FlatLaf e crea una nuova istanza MainController.
 *
 * @author Giuseppe Della Corte
 * @author Anna Greco
 * @author Sara Flauto
 * @see com.formdev.flatlaf.FlatLightLaf
 */

import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    /**
     * Il metodo main dell'applicazione
     * @param args argomenti per la linea di comando (non utilizzato)
     */
    public static void main(String[] args) {
        FlatLightLaf.setup();
        new MainController();
    }
}