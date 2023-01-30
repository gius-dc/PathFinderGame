class OggettoRossoFactory implements OggettoFactory {
    @Override
    public Oggetto creaOggetto(int x, int y) {
        return new Oggetto('R', x, y);
    }
}