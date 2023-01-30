class OggettoGialloFactory implements OggettoFactory {
    @Override
    public Oggetto creaOggetto(int x, int y) {
        return new Oggetto('Y', x, y);
    }
}