import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RankGUI extends JFrame {
    private JPanel mainPanel;
    private JButton nextButton;
    private JButton backButton;
    private JTable table1;
    private JLabel jlabel1;
    MainGUI.MyTableModel model;
    int level = 0;

    public RankGUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(level < 2)
                        {
                            level++;
                            setTableLevel();
                        }
                    }
                });
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(level > 0)
                        {
                            level--;
                            setTableLevel();
                        }

                    }
                });
            }
        });


    }
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            closeCurrentFrame();
        } else {
            super.processWindowEvent(e);
        }
    }

    private void closeCurrentFrame() {
        // Ottieni il riferimento al frame corrente
        Container container = SwingUtilities.getAncestorOfClass(JFrame.class, this);
        if (container instanceof JFrame) {
            JFrame currentFrame = (JFrame) container;
            // Chiudi il frame corrente
            currentFrame.dispose();
        }
    }


    public static void main(String[] args) {
        FlatLightLaf.setup();
        RankGUI myRankGUI = new RankGUI();
    }

    public void showRank(MainGUI.MyTableModel model)
    {
        this.model = model;
        setContentPane(mainPanel);
        setTitle("Classifica");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 700);
        setVisible(true);

        setTableLevel();

    }

    public void setTableLevel()
    {
        jlabel1.setText("Classifica - livello " + (level+1));
        DefaultTableModel modelRank = new DefaultTableModel();
        modelRank.addColumn("Nome");
        modelRank.addColumn("Cognome");
        modelRank.addColumn("Punteggio");


        table1.setModel(modelRank);

        for(int i = 0; i < model.getRowCount(); i++)
        {
            if(!model.getValueAt(i,level+2).equals("?"))
            {
                modelRank.addRow(new Object[]{model.getValueAt(i,0), model.getValueAt(i,1), model.getValueAt(i,level+2)});
            }
        }
    }


}
