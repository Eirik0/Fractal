package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import julia.JuliaImageDrawerDelegate;
import julia.JuliaSizer;

@SuppressWarnings("serial")
public class FractalPanel extends JPanel {
    private static final int FRAMES_PER_MILLI = (int) ((1.0 / 60) * 1000);

    private JuliaImageDrawerDelegate delegate;
    private FractalMouseAdapter mouseAdapter;

    FractalPanel(JuliaImageDrawerDelegate delegate) {
        this.delegate = delegate;
        mouseAdapter = new FractalMouseAdapter(delegate);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (getWidth() > 0 && getHeight() > 0) {
                    JuliaSizer.setImageDimensions(getWidth(), getHeight());
                    delegate.requestReset();
                }
            }
        });

        addMouseWheelListener(mouseAdapter);
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> repaint(), 0, FRAMES_PER_MILLI, TimeUnit.MILLISECONDS);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(delegate.requestImage(), 0, 0, null);

        if (mouseAdapter.isDragging()) {
            drawZoomPrediction(g);

            g.setColor(Color.RED);
            g.drawRect(mouseAdapter.getDragUpperLeftX(), mouseAdapter.getDragUpperLeftY(), mouseAdapter.getWidth(), mouseAdapter.getHeight());
        }
    }

    private void drawZoomPrediction(Graphics g) {
        g.setColor(Color.GREEN);

        double x0 = mouseAdapter.getDragUpperLeftX();
        double y0 = mouseAdapter.getDragUpperLeftY();

        double zoomWidth = mouseAdapter.getWidth();
        double zoomHeight = mouseAdapter.getHeight();

        double screenAspectRatio = (double) getWidth() / getHeight();
        double zoomAspectRatio = zoomWidth / zoomHeight;

        if (screenAspectRatio > zoomAspectRatio) {
            zoomWidth *= (screenAspectRatio / zoomAspectRatio);
            x0 -= (zoomWidth - mouseAdapter.getWidth()) / 2;
        } else {
            zoomHeight *= (zoomAspectRatio / screenAspectRatio);
            y0 -= (zoomHeight - mouseAdapter.getHeight()) / 2;
        }

        g.drawRect((int) Math.round(x0), (int) Math.round(y0), (int) Math.round(zoomWidth), (int) Math.round(zoomHeight));
    }

    public void save() {
        JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.home") + File.separator + "Desktop");
        if (jFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            try {
                ImageIO.write(delegate.requestImage(), "bmp", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
