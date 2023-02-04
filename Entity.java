/**
 * Classe astratta che rappresenta un'entità con coordinate x e y.
 * Tali entità possono essere un oggetto o un robot, rappresentati dalle loro rispettive classi concrete
 */


public abstract class Entity {
    private int x,y; // coordinate

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




}
