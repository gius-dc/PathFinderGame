import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
/**
 *  Questa classe estende JPanel e fornisce un modello personalizzato che visualizza un'immagine ridimensionata all'interno del pannello.
 *  È utilizzato per rappresentare la matrice del labirinto, dove ogni ImagePanel visualizza uno sprite.
 *
 *  @author Giuseppe Della Corte
 *  @author Anna Greco
 *  @author Sara Flauto
 */
public class ImagePanel extends JPanel {

    private Image img;

    public ImagePanel(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage resizedImg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        g2d.dispose();
        g.drawImage(resizedImg, 0, 0, null);
    }

    public void setImage(Image img) {
        this.img = img;
        repaint();
    }

}
