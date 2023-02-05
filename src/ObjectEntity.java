/**
 * Classe che rappresenta un oggetto che estende l'entità e ha una proprietà di colore.
 */

public class ObjectEntity extends Entity {

    private char color;

    public ObjectEntity(char c, int x, int y) {
        setColor(c);
        setX(x);
        setY(y);
    }

    public void setColor(char c) {
        color = c;
    }

    public char getColor(){return color;}
}

/**
 * Implementazione della factory per la creazione di un oggetto specifico di colore. 
 */

class ObjectCyanFactory implements ObjectFactory{
    @Override
    public ObjectEntity createObject(int x, int y) {
        return new ObjectEntity('C', x, y);
    }
}

class ObjectYellowFactory implements ObjectFactory {
    @Override
    public ObjectEntity createObject(int x, int y) {
        return new ObjectEntity('Y', x, y);
    }
}

class ObjectRedFactory implements ObjectFactory {
    @Override
    public ObjectEntity createObject(int x, int y) {
        return new ObjectEntity('R', x, y);
    }
}

class ObjectGreenFactory implements ObjectFactory {
    @Override
    public ObjectEntity createObject(int x, int y) {
        return new ObjectEntity('G', x, y);
    }
}
