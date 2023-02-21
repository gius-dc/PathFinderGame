/**
 * Interfaccia per implementare il Factory Method che permette di creare oggetti
 * senza specificare la classe concreta dell'oggetto da creare.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */
interface ObjectFactory {
    ObjectEntity createObject(int x, int y);
}
 
