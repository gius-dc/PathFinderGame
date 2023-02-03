import java.util.ArrayList;
import java.util.List;

public class Caretaker {
    private static Caretaker instance = null;

    private Caretaker() {
    }

    public static Caretaker getInstance() {
        if (instance == null) {
            instance = new Caretaker();
        }
        return instance;
    }

    private List<Memento> mementos = new ArrayList<>();

    public void addMemento(Memento memento) {
        mementos.add(memento);
    }

    public Memento getMemento(int index) {
        return mementos.get(index);
    }

    public int sizeMemento() {
        return mementos.size();
    }

    public void resetMemento() {
        mementos = new ArrayList<>();
    }
}