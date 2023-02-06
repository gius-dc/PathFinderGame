import java.util.Observable;

import static java.lang.Math.sqrt;

/**
 *    Questa classe Labirinto è responsabile solo per la creazione e l'aggiornamento del labirinto.
 */

public class Labyrinth extends Observable {
    public static int DIMENSION = 0; // dimensioni labirinto (quadrato)
    public char[][] labyrinth; // matrice del labirinto
    public int exitN; // un numero intero che corrisponde al numero della cella del labirinto dove è presente l'uscita
    public Entity player; // Questo oggetto è stato dichiarato in modo da rispettare il dependency inversion principle
    public Labyrinth(){}

    public Labyrinth(Level l) throws Exception {
        setLabyrinth(l);
    }

    public void setLabyrinth(Level l) throws Exception {
        labyrinth = l.getLabyrinth();
        exitN = l.getExit();
        if (!checkIfMatrixIsSquare(labyrinth)) {
            throw new Exception("Il livello contiene una matrice non quadrata.");
        }
        DIMENSION = (int) sqrt(getSizeMatrix(labyrinth));
    }

    public void resetLabyrinth(Level l) throws Exception {
        setLabyrinth(l);
    }

    // Metodo pubblico per ottenere il labirinto
    public char[][] getLabyrinth() {
        return labyrinth;
    }

    public int getExitN() {
        return exitN;
    }

    public static int getDimension() {
        return DIMENSION;
    }

    public boolean checkIfMatrixIsSquare(char[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        return rows == columns;
    }

    public int getSizeMatrix(char[][] matrix) {
        return matrix.length * matrix[0].length;
    }
}