/*
Questa classe implementa l'algoritmo di Dijkstra per il calcolo del percorso più breve in un grafo.
Include metodi per la generazione del grafo a partire da un labirinto, per il calcolo della distanza minima tra un nodo sorgente e tutti gli altri nodi,
e per il recupero del nodo con distanza minima non ancora visitato.
 */

public class ShortestPath {
    public int minDistance(int dist[], Boolean b[], int size) {
        int min = Integer.MAX_VALUE, index = -1;
        for (int x = 0; x < size * size; x++) {
            if (b[x] == false && dist[x] <= min) {
                min = dist[x];
                index = x;
            }
        }
        return index;
    }


    public int[] dijkstra(int graph[][], int src, int size) {
        int dist[] = new int[size * size];
        Boolean b[] = new Boolean[size * size];
        for (int i = 0; i < size * size; i++) {
            dist[i] = Integer.MAX_VALUE;
            b[i] = false;
        }

        dist[src] = 0;
        for (int count = 0; count < size * size; count++) {
            int u = minDistance(dist, b, size);
            b[u] = true;
            for (int x = 0; x < size * size; x++) {
                if (!b[x] && graph[u][x] != 0 && dist[u] != Integer.MAX_VALUE && dist[u] + graph[u][x] < dist[x]) {
                    dist[x] = dist[u] + graph[u][x];
                }
            }
        }
        return dist;
    }

    public int[][] generateGraph(int size, char labyrinth[][]) {
        int graph[][] = new int[size * size][size * size];
        int cX = 0, cY = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i > 0) {
                    // i--
                    cX = i - 1;
                    cY = j;
                    if (labyrinth[cX][cY] != '#') {
                        graph[(i * size) + j][(cX * size) + cY] = 1;
                    }
                }
                if (i < size - 1) {
                    // i++
                    cX = i + 1;
                    cY = j;
                    if (labyrinth[cX][cY] != '#') {
                        graph[(i * size) + j][(cX * size) + cY] = 1;
                    }
                }
                if (j > 0) {
                    // j--
                    cX = i;
                    cY = j - 1;
                    if (labyrinth[cX][cY] != '#') {
                        graph[(i * size) + j][(cX * size) + cY] = 1;
                    }
                }
                if (j < size - 1) {
                    // j++
                    cX = i;
                    cY = j + 1;
                    if (labyrinth[cX][cY] != '#') {
                        graph[(i * size) + j][(cX * size) + cY] = 1;
                    }
                }
            }
        }

        return graph;
    }

}