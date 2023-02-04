import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RankGUI extends JFrame {
    private JPanel mainPanel;
    private JButton nextButton;
    private JButton backButton;
    private JTable table1;
    private JLabel jlabel1;
    MyTableModel model;
    int level = 0;
    int maxLevels = 0;

    public RankGUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(10, 10);
        setResizable(false);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (level < maxLevels) {
                    level++;
                    backButton.setEnabled(true);
                    setTableLevel();
                    if(level == maxLevels)
                    {
                        nextButton.setEnabled(false);
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (level > 0) {
                    level--;
                    nextButton.setEnabled(true);
                    setTableLevel();
                    if(level == 0)
                    {
                        backButton.setEnabled(false);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        RankGUI myRankGUI = new RankGUI();
    }

    public void showRank(MyTableModel model, int maxLevels) {
        this.model = model;
        setContentPane(mainPanel);
        setTitle("Classifica");
        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setSize(1000, 700);
        this.maxLevels = maxLevels;
        setTableLevel();

    }

    public void setTableLevel() {
        MyTableModel modelRank = new MyTableModel(new String[]{"Nome", "Cognome", "Punteggio"});
        table1.setModel(modelRank);

        if (level == maxLevels) {
            jlabel1.setText("Classifica finale");
        } else {
            jlabel1.setText("Classifica - Livello " + (level + 1));
        }
        for (int i = 0; i < model.getRowCount(); i++) {
            if (!model.getValueAt(i, level + 2).toString().equals("?")) {
                modelRank.addRow(new Object[]{model.getValueAt(i, 0), model.getValueAt(i, 1), model.getValueAt(i, level + 2)});
            }
        }

        modelRank.sortByColumn(2);
    }


}
