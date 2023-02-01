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
