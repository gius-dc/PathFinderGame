/**
 *  Questa classe rappresenta un labirinto generico.
 *  Il labirinto è rappresentato da una matrice quadrata, la porta d'uscita e un'entità giocatore.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */


import static java.lang.Math.sqrt;

public class Labyrinth {
    public static int DIMENSION = 0; // dimensioni labirinto (quadrato)
    public char[][] labyrinth; // matrice del labirinto
    public int exitN; // un numero intero che corrisponde al numero della cella del labirinto dove è presente l'uscita
    public Entity player; // Questo oggetto è stato dichiarato in modo da rispettare il dependency inversion principle
    public Labyrinth(){}

    public Labyrinth(Level l) throws Exception {
        setLabyrinth(l);
    }

    /**
     * Imposta il labirinto con un oggetto Level che contiene il livello.
     *
     * @param l oggetto Level contenente il labirinto e l'uscita
     * @throws Exception se il labirinto non è quadrato
     */
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


    /**
     * Verifica se una matrice è quadrata.
     *
     * @param matrix la matrice da controllare
     * @return true se la matrice è quadrata, false altrimenti
     */
    public boolean checkIfMatrixIsSquare(char[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        return rows == columns;
    }

    /**
     * Restituisce la dimensione della matrice del labirinto.
     *
     * @param matrix la matrice del labirinto
     * @return la dimensione della matrice del labirinto
     */
    public int getSizeMatrix(char[][] matrix) {
        return matrix.length * matrix[0].length;
    }
}