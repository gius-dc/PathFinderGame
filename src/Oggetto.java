public class Oggetto extends Entita {

    private char color;

    public Oggetto(char c, int x, int y) {
        setColor(c);
        setX(x);
        setY(y);
    }

    public void setColor(char c) {
        color = c;
    }

    public char getColor() {
        return color;
    }
}

class OggettoCianoFactory implements OggettoFactory {
    @Override
    public Oggetto creaOggetto(int x, int y) {
        return new Oggetto('C', x, y);
    }
}

class OggettoGialloFactory implements OggettoFactory {
    @Override
    public Oggetto creaOggetto(int x, int y) {
        return new Oggetto('Y', x, y);
    }
}

class OggettoRossoFactory implements OggettoFactory {
    @Override
    public Oggetto creaOggetto(int x, int y) {
        return new Oggetto('R', x, y);
    }
}

class OggettoVerdeFactory implements OggettoFactory {
    @Override
    public Oggetto creaOggetto(int x, int y) {
        return new Oggetto('G', x, y);
    }
}