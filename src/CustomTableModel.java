import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 *  Questa classe estende la classe {@link AbstractTableModel} per rappresentare il modello utilizzato dalle tabelle dell'interfaccia grafica.
 *  Dato che il progetto utilizza la libreria FlatLaf per migliorare l'aspetto grafico dei componenti grafici Swing,
 *  l'utilizzo di {@link javax.swing.table.DefaultTableModel} genera eccezioni nel momento in cui vengono aggiornati i dati al suo
 *  interno. Utilizzando un modello personalizzato che estende AbstractTableModel il problema è stato risolto.
 *
 *  Inoltre, adottando questa soluzione, è stato possibile estendere questo modello con ulteriori metodi utili
 *  al fine del progetto.
 *
 *   @author Giuseppe Della Corte
 *   @author Anna Greco
 *   @author Sara Flauto
 */

public class CustomTableModel extends AbstractTableModel {
    private String[] columnNames;
    private Object[][] data = {};


    /**
     * Costruttore che inizializza i nomi delle colonne.
     * @param columnNames i nomi delle colonne
     */
    public CustomTableModel(String[] columnNames) {
        this.columnNames = columnNames;
    }

    /**
     * Cerca la riga corrispondente ai valori di una determinata coppia di colonne.
     * @param firstColumn il valore da cercare nella prima colonna
     * @param secondColumn il valore da cercare nella seconda colonna
     * @param firstColumnIndex l'indice della prima colonna
     * @param secondColumnIndex l'indice della seconda colonna
     * @return l'indice della riga corrispondente o -1 se non viene trovata
     */
    public int searchRow(String firstColumn, String secondColumn, int firstColumnIndex, int secondColumnIndex) {
        int rowCount = getRowCount();

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            if (getValueAt(rowIndex, firstColumnIndex).equals(firstColumn) &&
                    getValueAt(rowIndex, secondColumnIndex).equals(secondColumn)) {
                return rowIndex;
            }
        }
        return -1;
    }

    /**
     * Cerca la colonna corrispondente a un determinato valore in una riga specifica.
     * @param rowIndex l'indice della riga
     * @param value il valore da cercare
     * @return l'indice della colonna corrispondente o -1 se non viene trovata
     */
    public int searchColumn(int rowIndex, Object value) {
        int columnCount = getColumnCount();

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            if (getValueAt(rowIndex, columnIndex).equals(value)) {
                return columnIndex;
            }
        }
        return -1;
    }

    /**
     * Ordina i dati del modello in base ai valori di una determinata colonna.
     * @param columnIndex l'indice della colonna in base a cui ordinare i dati
     */
    public void sortByColumn(int columnIndex) {
        List<List<Object>> dataList = new ArrayList<>();
        for (int i = 0; i < getRowCount(); i++) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < getColumnCount(); j++) {
                row.add(getValueAt(i, j));
            }
            dataList.add(row);
        }
        dataList.sort((o1, o2) -> {
            Object o1Value = o1.get(columnIndex);
            Object o2Value = o2.get(columnIndex);
            if (o1Value instanceof String && o2Value instanceof String) {
                int int1 = Integer.parseInt((String) o1Value);
                int int2 = Integer.parseInt((String) o2Value);
                return Integer.compare(int1, int2);
            }
            return 0;
        });
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                setValueAt(dataList.get(i).get(j), i, j);
            }
        }
        fireTableDataChanged();
    }

    /**
     * Rimuove una riga dal modello.
     * @param rowIndex indice della riga da rimuovere
     */
    public void removeRow(int rowIndex) {
        int rowCount = getRowCount();
        if (rowIndex >= 0 && rowIndex < rowCount) {
            Object[][] newData = new Object[rowCount - 1][];
            // Copia le righe precedenti e successive alla riga da rimuovere in un nuovo array
            System.arraycopy(data, 0, newData, 0, rowIndex);
            System.arraycopy(data, rowIndex + 1, newData, rowIndex, rowCount - rowIndex - 1);
            // Sostituisci l'array vecchio con quello nuovo
            data = newData;
            // Notifica la rimozione della riga alla JTable
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }


    /**
     * Aggiunge una riga nel modello.
     * @param rowData dati della riga da aggiungere
     */
    public void addRow(Object[] rowData) {
        int rowCount = getRowCount();
        Object[][] newData = new Object[rowCount + 1][getColumnCount()];
        System.arraycopy(data, 0, newData, 0, rowCount);
        newData[rowCount] = rowData;
        data = newData;
        fireTableRowsInserted(rowCount, rowCount);
    }

    /**
     * Salva i dati del modello in un file csv.
     * @param file Il file csv su cui salvare i dati
     * @throws IOException in caso di errore di scrittura sul file
     */
    public void saveToFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            for (String columnName : columnNames) {
                writer.append(columnName).append(",");
            }
            writer.append("\n");
            for (Object[] row : data) {
                for (Object value : row) {
                    writer.append(String.valueOf(value)).append(",");
                }
                writer.append("\n");
            }
        }
    }

    /**
     * Carica i dati da un file csv e li inserisce nel modello.
     * @param file Il file csv da cui caricare i dati
     * @throws IOException in caso di errore di lettura dal file
     */
    public void loadFromFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
        columnNames = lines.get(0).split(",");
        List<Object[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",");
            Object[] row = new Object[values.length];
            System.arraycopy(values, 0, row, 0, values.length);
            rows.add(row);
        }
        data = rows.toArray(new Object[0][]);
        fireTableDataChanged();
    }

    /**
     * Restituisce il numero di righe del modello.
     * @return Il numero di colonne del modello.
     */
    @Override
    public int getRowCount() {
        return data.length;
    }

    /**
     * Restituisce il numero di colonne del modello.
     * @return Il numero di colonne del modello.
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Restituisce il valore di una cella a una determinata riga e colonna.
     * @param rowIndex L'indice della riga della cella.
     * @param columnIndex L'indice della colonna della cella.
     * @return Il valore della cella a una determinata riga e colonna.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    /**
     * Restituisce il nome della colonna a un indice specificato.
     * @param columnIndex L'indice della colonna di cui ottenere il nome.
     * @return Il nome della colonna a un indice specificato.
     */
    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    /**
     * Imposta il valore di una cella nella tabella.
     * @param aValue valore da impostare
     * @param rowIndex indice della riga
     * @param columnIndex indice della colonna
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}