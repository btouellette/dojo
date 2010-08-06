import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;

public class Scaling {
    public static void main(String[] args) throws IOException {
        BufferedImage image1 = createTestImage(150);
        BufferedImage image2 = createTestImage(3000);
        URL url = new URL("http://web.archive.org/web/20030801001434/http://www2.us.porsche.com/english/usa/carreragt/modelinformation/experience/desktop/bilder/icon3_1600x1200.jpg");
        BufferedImage image3 = ImageIO.read(url);
        url = new URL("http://web.archive.org/web/20051216013804/http://today.java.net/jag/Image24-small.jpeg");
        BufferedImage image4 = ImageIO.read(url);

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.addTab("line drawing grow", createTab(image1, 2.0, true));
        tabbed.addTab("original big line drawing", new JScrollPane(new JLabel(new ImageIcon(image2))));
        tabbed.addTab("line drawing shrink", createTab(image2, 0.1, false));
        tabbed.addTab("original porsche", new JScrollPane(new JLabel(new ImageIcon(image3))));
        tabbed.addTab("picture shrink", createTab(image3, 0.2, false));
        tabbed.addTab("picture grow", createTab(image4, 2.0, true));

        JFrame f = new JFrame("Scaling");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(tabbed);
        f.setSize(800,600);
        f.setLocationRelativeTo(null);
        f.setExtendedState(Frame.MAXIMIZED_BOTH);
        f.setVisible(true);
    }

    static JComponent createTab(BufferedImage image, double factor, boolean addOriginal) {
        GraphicsConfiguration gc = getDefaultConfiguration();
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        if (addOriginal)
            addToPanel(p, gbc, image, "Original Image");
        Object hint = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        addToPanel(p, gbc, getScaledInstance2D(image, factor, hint, gc), hint.toString());
        hint = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        addToPanel(p, gbc, getScaledInstance2D(image, factor, hint, gc), hint.toString());
        hint = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        addToPanel(p, gbc, getScaledInstance2D(image, factor, hint, gc), hint.toString());
        gbc.gridy = 1;
        if (addOriginal)
            addToPanel(p, gbc, image, "Original Image");
        addToPanel(p, gbc, getScaledInstanceAWT(image, factor, Image.SCALE_REPLICATE), "SCALE_REPLICATE");
        addToPanel(p, gbc, getScaledInstanceAWT(image, factor, Image.SCALE_SMOOTH), "SCALE_SMOOTH");
        addToPanel(p, gbc, getScaledInstanceAWT(image, factor, Image.SCALE_AREA_AVERAGING), "SCALE_AREA_AVERAGING");
        return new JScrollPane(p);
    }

    static BufferedImage createTestImage(int side) {
        BufferedImage image = getDefaultConfiguration().createCompatibleImage(side, side);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke((float)side/50));
        g.setColor(Color.GREEN);
            for(int i=0; i<10; ++i)
        g.drawLine(0, i*side/10, side, 2+i*side/10);
        g.setColor(Color.WHITE);
        g.drawOval(side/20, side/20, 9*side/10, 9*side/10);
        g.dispose();
        return image;
    }

    static void addToPanel(JPanel p, GridBagConstraints gbc, Image image, String title) {
        JLabel label = new JLabel(new ImageIcon(image));
        label.setBorder(BorderFactory.createTitledBorder(title));
        p.add(label, gbc);
    }

    //UTILITY METHODS:
    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    public static BufferedImage copy(BufferedImage source, BufferedImage target, Object interpolationHint) {
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolationHint);
        double scalex = (double) target.getWidth() / source.getWidth();
        double scaley = (double) target.getHeight() / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(scalex, scaley);
        g.drawRenderedImage(source, at);
        g.dispose();
        return target;
    }

    public static BufferedImage getScaledInstance2D(BufferedImage source, double factor, Object interpolationHint, GraphicsConfiguration gc) {
        if (gc == null)
            gc = getDefaultConfiguration();
        int w = (int) (source.getWidth() * factor);
        int h = (int) (source.getHeight() * factor);
        int transparency = source.getColorModel().getTransparency();
        return copy(source, gc.createCompatibleImage(w, h, transparency), interpolationHint);
    }

    public static Image getScaledInstanceAWT(BufferedImage source, double factor, int hint) {
        int w = (int) (source.getWidth() * factor);
        int h = (int) (source.getHeight() * factor);
        return source.getScaledInstance(w, h, hint);
    }

    //best of breed
    public static Image getScaledInstance(BufferedImage source, double factor, GraphicsConfiguration gc) {
         if (factor >= 1.0)
             return getScaledInstance2D(source, factor, RenderingHints.VALUE_INTERPOLATION_BICUBIC, gc);
         else
             return getScaledInstanceAWT(source, factor, Image.SCALE_AREA_AVERAGING);
    }
}
