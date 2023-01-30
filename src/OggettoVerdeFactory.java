class OggettoVerdeFactory implements OggettoFactory {
    @Override
    public Oggetto creaOggetto(int x, int y) {
        return new Oggetto('G', x, y);
    }
}