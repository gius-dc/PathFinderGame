import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class App extends JFrame  {
    private JPanel mainPanel;
    private JPanel labirintoPanel;
    private JButton avviaButton;
    private JLabel stateLabel;
    private JTable table1;
    Labirinto l;
    int row = 16, col = 16;
    Boolean firstRun = true;
    DefaultTableModel model;
    RobotState state;

    public App() {
        setContentPane(mainPanel);
        setTitle("Labirinto");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table1.getModel());
        table1.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);




        File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "file.dat");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        avviaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Creare un executor con un pool di un solo thread
                Executor executor = Executors.newSingleThreadExecutor();

                // Eseguire l'operazione in modo asincrono
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        //int state;
                        labirintoPanel.setBackground(Color.WHITE);
                        avviaButton.setEnabled(false);

                        Color checker;
                        l = new Labirinto("l");
                        char lab[][] = l.getLabyrinth();

                        if(firstRun)
                        {
                            labirintoPanel.setLayout(new GridLayout(row, col));
                            for (int x = 0; x < 16; x++) {
                                for (int y = 0; y < 16; y++) {
                                    if (lab[x][y] == '#') {
                                        checker = Color.BLACK;
                                    } else {
                                        checker = Color.WHITE;
                                    }
                                    //JPanel panel = new JPanel();
                                    ImagePanel panel = new ImagePanel(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                                    if(checker == Color.BLACK)
                                    {
                                        panel.setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                                    }
                                    panel.setPreferredSize(new Dimension(400 / row, 400 / col));
                                    panel.setBackground(Color.WHITE);
                                    labirintoPanel.add(panel);
                                }
                            }
                            firstRun = false;
                        }
                        else
                        {
                            for (int x = 0; x < 16; x++) {
                                for (int y = 0; y < 16; y++) {
                                    if (lab[x][y] == '#') {
                                        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                                    } else {
                                        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                                    }
                                }
                            }
                        }


                        setSize(800,600);
                        while (l.iterate())
                        {
                            state = l.getRobot().getState();
                            if (state instanceof PursuitState) {
                                // esegui l'azione per lo stato di inseguimento
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: pursuit");
                            }
                            else if (state instanceof SeekState) {
                                // esegui l'azione per lo stato di ricerca
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: seek");
                            }
                            else if (state instanceof FleeState) {
                                // esegui l'azione per lo stato di fuga
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: flee");
                            }
                            else if (state instanceof EvadeState) {
                                // esegui l'azione per lo stato di evitamento
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: evade");
                            }

/*
                            if (state instanceof PursuitState) {
                                // esegui l'azione per lo stato di inseguimento
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: pursuit");
                            } else if (state instanceof SeekState) {
                                // esegui l'azione per lo stato di ricerca
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: seek");
                            } else if (state instanceof FleeState) {
                                // esegui l'azione per lo stato di fuga
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: flee");
                            } else if (state instanceof EvadeState) {
                                // esegui l'azione per lo stato di evitamento
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: evade");
                            }
                            */

                            /*state = l.getStateRobot();
                            // 0 = pursuit, 1 = seek, 2 = flee, 3 = evade
                            if(state == 0)
                            {
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: pursuit");
                            }
                            else if(state == 1)
                            {
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: seek");
                            }
                            else if(state == 2)
                            {
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: flee");
                            }
                            else if(state == 3) {
                                stateLabel.setText("Labirinto in esecuzione. Stato robot: evade");
                            }*/

                            for (int x = 0; x < 16; x++) {
                                for (int y = 0; y < 16; y++) {
                                    if (lab[x][y] == '#') {
                                        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                                    } else {
                                        ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());
                                    }
                                }
                            }


                            ((ImagePanel) labirintoPanel.getComponent((l.getRobotX() * 16) + l.getRobotY())).setImage(new ImageIcon(getClass().getResource("/img/circle_gray.png").toString().substring(5)).getImage());
                            //labirintoPanel.getComponent((l.getRobotX() * 16) + l.getRobotY()).setBackground(Color.GRAY);
                            List<Oggetto> oggetti = l.getObjects();
                            for (int i = 0; i < oggetti.size(); i++) {
                                if (oggetti.get(i).getTipo() == 'R') {
                                    ((ImagePanel) labirintoPanel.getComponent((oggetti.get(i).getX() * 16) + oggetti.get(i).getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_red.png").toString().substring(5)).getImage());
                                } else if (oggetti.get(i).getTipo() == 'Y') {
                                    ((ImagePanel) labirintoPanel.getComponent((oggetti.get(i).getX() * 16) + oggetti.get(i).getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_yellow.png").toString().substring(5)).getImage());
                                } else if (oggetti.get(i).getTipo() == 'C') {
                                    ((ImagePanel) labirintoPanel.getComponent((oggetti.get(i).getX() * 16) + oggetti.get(i).getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_cyan.png").toString().substring(5)).getImage());
                                } else if (oggetti.get(i).getTipo() == 'G') {
                                    ((ImagePanel) labirintoPanel.getComponent((oggetti.get(i).getX() * 16) + oggetti.get(i).getY())).setImage(new ImageIcon(getClass().getResource("/img/circle_green.png").toString().substring(5)).getImage());
                                }
                            }

                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        stateLabel.setText("Il robot ha raggiunto la destinazione.");
                        //avviaButton.setEnabled(true);
                        // ricava il percorso
                        boolean percorso[][] = l.getPathRobot();

                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                                if (lab[x][y] == '#') {
                                    ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_black.png").toString().substring(5)).getImage());
                                } else {
                                    ((ImagePanel) labirintoPanel.getComponent((x * 16) + y)).setImage(new ImageIcon(getClass().getResource("/img/square_white.png").toString().substring(5)).getImage());

                                }
                            }
                        }

                        for(int i = 0; i < 16; i++) // aggiusterò il size, per ora è 16...
                        {
                            for(int j = 0; j < 16; j++)
                            {
                                if(lab[i][j] != '#' && percorso[i][j] == true)
                                {
                                    ((ImagePanel) labirintoPanel.getComponent((i * 16) + j)).setImage(new ImageIcon(getClass().getResource("/img/square_gray.png").toString().substring(5)).getImage());
                                }
                            }
                            System.out.println();
                        }

                        Object[] row = new Object[2];
                        String input = JOptionPane.showInputDialog("Inserisci il nome del robot da salvare in classifica");
                        if(input == "" || input == null)
                        {
                            row[0] = "(senza nome)";
                            row[1] = l.getPassi();
                        }
                        else {
                            row[0] = input;
                            row[1] = l.getPassi();
                        }

                        model.addRow(row);

                        sorter.sort();



                        avviaButton.setEnabled(true);
                        saveTableData(table1,file);

                        // Aggiornare l'interfaccia grafica qui, utilizzando il risultato ottenuto
                        //updateGUI(result);
                    }
                });
            }
        });
    }
    /*
    public List<Oggetto> itera()
    {

    }

    public void updateGUI()
    {

    }
    */
    public void start()
    {





    }

    public static void main(String[] args) {
        App myApp = new App();



    }

    // metodo per salvare i dati della tabella in un file
    public void saveTableData(JTable table, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(table.getModel());
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // metodo per caricare i dati della tabella da un file
    public void loadTableData(JTable table, File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            table.setModel((TableModel) ois.readObject());
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createUIComponents() throws URISyntaxException, MalformedURLException {
        // TODO: place custom component creation code here
        //String fileDir = this.getClass().getResource("file.dat").toString().substring(5);

        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());

        File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "file.dat");


        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            model = new DefaultTableModel();
            model.addColumn("Nome robot");
            model.addColumn("Numero passi");
            table1 = new JTable(model);
        }
        else {
            table1 = new JTable();
            loadTableData(table1,file);
            model = (DefaultTableModel) table1.getModel();
        }

    }
}
