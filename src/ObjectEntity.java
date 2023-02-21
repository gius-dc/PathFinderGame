/**
 * Classe che rappresenta un oggetto che estende l'entità e ha una proprietà di colore.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */

public class ObjectEntity implements Entity {
    private int x,y;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
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
