import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MyTableModel extends AbstractTableModel {
    private String[] columnNames = {};
    private Object[][] data = {};

    public MyTableModel(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public int searchRow(String nome, String cognome) {
        int rowCount = getRowCount();
        int nameColumn = 0;
        int surnameColumn = 1;

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            if (getValueAt(rowIndex, nameColumn).equals(nome) &&
                    getValueAt(rowIndex, surnameColumn).equals(cognome)) {
                return rowIndex;
            }
        }
        return -1;
    }

    public int searchColumn(int rowIndex, Object value) {
        int columnCount = getColumnCount();

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            if (getValueAt(rowIndex, columnIndex).equals(value)) {
                return columnIndex;
            }
        }
        return -1;
    }

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

    public void addRow(Object[] rowData) {
        int rowCount = getRowCount();
        Object[][] newData = new Object[rowCount + 1][getColumnCount()];
        System.arraycopy(data, 0, newData, 0, rowCount);
        newData[rowCount] = rowData;
        data = newData;
        fireTableRowsInserted(rowCount, rowCount);
    }

    public void addColumn(String columnName, Object[] columnData) {
        int columnCount = getColumnCount();
        String[] newColumnNames = new String[columnCount + 1];
        System.arraycopy(columnNames, 0, newColumnNames, 0, columnCount);
        newColumnNames[columnCount] = columnName;
        columnNames = newColumnNames;

        Object[][] newData = new Object[getRowCount()][columnCount + 1];
        for (int i = 0; i < getRowCount(); i++) {
            System.arraycopy(data[i], 0, newData[i], 0, columnCount);
            newData[i][columnCount] = columnData[i];
        }
        data = newData;
        fireTableStructureChanged();
    }

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

    public void loadFromFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file.toURI()));
        columnNames = lines.get(0).split(",");
        List<Object[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",");
            Object[] row = new Object[values.length];
            for (int j = 0; j < values.length; j++) {
                row[j] = values[j];
            }
            rows.add(row);
        }
        data = rows.toArray(new Object[0][]);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = (String) aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}