
class OggettoCianoFactory implements OggettoFactory {
    @Override
    public Oggetto creaOggetto(int x, int y) {
        return new Oggetto('C', x, y);
    }
}