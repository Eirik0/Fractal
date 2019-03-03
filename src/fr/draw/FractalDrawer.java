package fr.draw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import fr.fractal.Fractal;
import fr.main.FractalManager;
import gt.gameentity.DrawingMethods;

public class FractalDrawer implements DrawingMethods {
    private static final int INITIAL_LOG_CP = 5;
    private static final double MIN_LOG_CP = -3;

    private BufferedImage image;
    private Graphics2D graphics;

    private Fractal fractal;

    private boolean noStopRequested = true;
    private boolean isDrawingComplete = false;

    private int currentX;
    private int initialX;

    // Ranges from 5 to -3 and is used to determine the number of calculations per pixel
    // 5 -> 32x32 blocks -3 => 1x1 blocks with 64 calculations
    private int logCP;

    private int x0;
    private int y0;
    private int x1;
    private int y1;

    public FractalDrawer(BufferedImage currentImage, int x0, int y0, int x1, int y1) {
        init(fractal, x0, y0, x1, y1, x0, INITIAL_LOG_CP);
        setImage(new BufferedImage(x1 - x0, y1 - y0, BufferedImage.TYPE_INT_RGB));
        graphics.drawImage(currentImage, 0, 0, null);
        startDrawing();
    }

    private FractalDrawer(BufferedImage image, int x0, int y0, int x1, int y1, int initialX, int logCP) {
        init(fractal, x0, y0, x1, y1, initialX, logCP);
        setImage(image);
        startDrawing();
    }

    private void init(Fractal fractal, int x0, int y0, int x1, int y1, int initialX, int logCP) {
        this.fractal = fractal;

        this.x0 = x0;
        this.y0 = y0;

        this.x1 = x1;
        this.y1 = y1;

        this.initialX = initialX;
        this.logCP = logCP;
    }

    private void setImage(BufferedImage image) {
        this.image = image;
        graphics = image.createGraphics();
    }

    private void startDrawing() {
        new Thread(() -> {
            while (noStopRequested && logCP >= MIN_LOG_CP) {
                drawSquares();
                if (noStopRequested) {
                    --logCP;
                }
            }

            isDrawingComplete = true;
        }).start();
    }

    private void drawSquares() {
        int width = logCP < 1 ? 1 : 1 << logCP;
        double offset = (double) width / 2;
        int calculationsX = logCP < 0 ? 1 << -logCP : 1;

        currentX = initialX;
        do {
            int y = y0;
            do {
                if (logCP >= 0) {
                    graphics.setColor(FractalManager.getColor(currentX + offset, y + offset));
                    graphics.fillRect(currentX - x0, y - y0, width, width);
                } else {
                    graphics.setColor(FractalManager.getColor(currentX + offset, y + offset, calculationsX));
                    graphics.drawLine(currentX - x0, y - y0, currentX - x0, y - y0);
                }
                y += width;
            } while (noStopRequested && y < y1);
            if (noStopRequested) {
                currentX += width;
            }
        } while (noStopRequested && currentX < x1);
        initialX = x0;
    }

    public void drawOn(Graphics g) {
        g.drawImage(image, x0, y0, getImageWidth(), getImageHeight(), null);
    }

    public void requestStop() {
        noStopRequested = false;
    }

    public boolean isDrawingComplete() {
        return isDrawingComplete;
    }

    public boolean isSlowerThan(FractalDrawer slowest) {
        return (logCP > slowest.logCP)
                || (logCP == slowest.logCP && getImageHeight() > slowest.getImageHeight())
                || (getImageHeight() == slowest.getImageHeight() && currentX < slowest.currentX);
    }

    public boolean isSplittable() {
        return getImageHeight() > 1;
    }

    public List<FractalDrawer> splitVertically() {
        requestStop();

        BufferedImage top = splitImage(0, 0, getImageWidth(), getImageHeight() / 2);
        BufferedImage bottom = splitImage(0, getImageHeight() / 2, getImageWidth(), getImageHeight() - (getImageHeight() / 2));

        List<FractalDrawer> drawers = new ArrayList<>();
        drawers.add(new FractalDrawer(top, x0, y0, x1, y0 + getImageHeight() / 2, currentX, logCP));
        drawers.add(new FractalDrawer(bottom, x0, y0 + getImageHeight() / 2, x1, y1, currentX, logCP));

        return drawers;
    }

    private BufferedImage splitImage(int x, int y, int width, int height) {
        BufferedImage split = image.getSubimage(x, y, width, height);
        Graphics2D g = split.createGraphics();
        g.setColor(Color.RED);
        g.drawLine(currentX - x0, 0, width, 0);
        return split;
    }

    private int getImageHeight() {
        return y1 - y0;
    }

    private int getImageWidth() {
        return x1 - x0;
    }

    @Override
    public String toString() {
        return "(" + x0 + " - " + x1 + ") x (" + y0 + " - " + y1 + "): " + (noStopRequested ? "" : "Stop Requested ")
                + (isDrawingComplete ? "Finished Drawing " : "") + logCP + " logCP";
    }
}
