/*
Questa classe implementa l'algoritmo di Dijkstra per il calcolo del percorso più breve in un grafo.
Include metodi per la generazione del grafo a partire da un labirinto, per il calcolo della distanza minima tra un nodo sorgente e tutti gli altri nodi,
e per il recupero del nodo con distanza minima non ancora visitato.
 */

/**
 * Classe che implementa l'algoritmo di Dijkstra per il calcolo del percorso più breve in un grafo.
 */

public class Dijkstra {
    /**
     * Metodo che restituisce il vertice con la distanza minima non ancora selezionata.
     *
     * @param dist Array che contiene le distanze dal vertice di origine a ogni vertice
     * @param b    Array che indica se un vertice è stato selezionato o meno
     * @param size Dimensione della griglia
     * @return Indice del vertice con distanza minima non ancora selezionata
     */
    public int minDistance(int[] dist, Boolean[] b, int size) {
        int min = Integer.MAX_VALUE, index = -1;
        // Cerca il vertice con distanza minima non ancora selezionata
        for (int x = 0; x < size * size; x++) {
            if (!b[x] && dist[x] <= min) {
                min = dist[x];
                index = x;
            }
        }
        return index;
    }

    /**
     * Metodo che calcola le distanze minime da una sorgente a tutti gli altri vertici in un grafo.
     *
     * @param graph Matrice di adiacenza rappresentante il grafo
     * @param src   Indice del vertice sorgente
     * @param size  Dimensione della griglia
     * @return Array che contiene le distanze minime dalla sorgente a tutti gli altri vertici
     */

    public int[] run(int[][] graph, int src, int size) {
        int[] dist = new int[size * size]; // Array che terrà le distanze minime dalla sorgente all'altro vertice
        Boolean[] b = new Boolean[size * size]; // Array che indica se un vertice è stato visitato o meno

        // Inizializza tutti i valori di distanza come infinito e tutti i valori di visita come falsi
        for (int i = 0; i < size * size; i++) {
            dist[i] = Integer.MAX_VALUE;
            b[i] = false;
        }

        // La distanza dalla sorgente a se stessa è 0
        dist[src] = 0;

        // Esegue l'algoritmo per tutti i vertici
        for (int count = 0; count < size * size; count++) {
            int u = minDistance(dist, b, size); // Scegli il vertice non visitato con distanza minima
            b[u] = true; // Segna il vertice come visitato

            /* Verifica ogni vertice non visitato e non infinito
               e verifica se la distanza attraverso u è minore della distanza corrente
               in tal caso, aggiorna la distanza corrente */
            for (int x = 0; x < size * size; x++) {
                if (!b[x] && graph[u][x] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][x] < dist[x]) {
                    dist[x] = dist[u] + graph[u][x];
                }
            }
        }
        return dist; // Ritorna tutte le distanze a partire dalla sorgente
    }

    /**
     * Metodo che genera un grafo a partire da una matrice rappresentante il labirinto.
     * @param size Dimensione del labirinto
     * @param labyrinth Matrice che rappresenta il labirinto
     * @return Matrice di adiacenza che rappresenta il grafo
     */
    public int[][] generateGraph(int size, char[][] labyrinth) {
        int[][] graph = new int[size * size][size * size];
        int[] xOffsets = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] yOffsets = {-1, -1, -1, 0, 0, 1, 1, 1};

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < 8; k++) {
                    int cX = i + xOffsets[k];
                    int cY = j + yOffsets[k];
                    if (cX >= 0 && cX < size && cY >= 0 && cY < size && labyrinth[cX][cY] != '#') {
                        graph[(i * size) + j][(cX * size) + cY] = 1;
                    }
                }
            }
        }

        return graph;
    }
}