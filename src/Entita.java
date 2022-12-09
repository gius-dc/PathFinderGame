public abstract class Entita {
    private char tipo;
    private int x,y; // coordinate


    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getTipo() {
        return tipo;
    }
}
