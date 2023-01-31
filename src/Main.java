public class Main {
    public static void main(String[] args)
    {
        Level.Builder builder = new Level.Builder(16, 16);
        builder.aggiungiPareti();
        builder.aggiungiPareteVerticale(3,2,9);
        builder.aggiungiPareteVerticale(7,8,14);
        builder.aggiungiPareteOrizzontale(3,7,14);
        builder.aggiungiPareteOrizzontale(10,11,13);
        builder.impostaPosizionePortaUscita(12,0);

        Level livello = builder.build();

        char[][] liv = livello.getLabyrinth();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                System.out.print(liv[i][j] + "  ");
            }
            System.out.println();
        }





    }
}
