
import java.util.ArrayList;
import java.util.List;
/**
 * Questa classe implementa il pattern Memento.
 * Costruttore privato per implementare il pattern Singleton.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */
public class Caretaker {
    private static Caretaker instance = null;

    private Caretaker() {}

    /**
     * Questo metodo restituisce l'istanza unica di Caretaker. Se non esiste ancora, viene creata una nuova istanza.
     *
     * @return L'istanza unica di Caretaker.
     */
    public static Caretaker getInstance() {
        if (instance == null) {
            instance = new Caretaker();
        }
        return instance;
    }

    private List<Memento> mementos = new ArrayList<>();

    /**
     * Questo metodo viene utilizzato per salvare uno stato del robot nel labirinto tramite il design pattern Memento.
     *
     * @param memento Lo stato del robot da salvare.
     */
    public void addMemento(Memento memento) {
        mementos.add(memento);
    }

    /**
     * Questo metodo viene utilizzato per recuperare uno stato del robot salvato in precedenza.
     *
     * @param index L'indice dello stato del robot da recuperare.
     * @return Lo stato del robot recuperato.
     */
    public Memento getMemento(int index) {
        return mementos.get(index);
    }

    /**
     * Questo metodo restituisce il numero di stati del robot salvati.
     *
     * @return Il numero di stati del robot salvati.
     */
    public int sizeMemento() {
        return mementos.size();
    }

    /**
     * Questo metodo viene utilizzato per ripulire la lista di stati del robot salvati.
     */
    public void resetMemento() {
        mementos = new ArrayList<>();
    }
}