package fr.draw;

import java.awt.Color;

import fr.main.FractalManager;
import gt.async.ThreadWorker;
import gt.gameentity.Drawable;
import gt.gameentity.GameImageDrawer;
import gt.gameentity.IGameImage;
import gt.gameentity.IGraphics;

public class FractalDrawer implements Drawable {
    public static final int INITIAL_LOG_CP = 5;
    public static final int MIN_LOG_CP = -3;

    private final GameImageDrawer imageDrawer;
    private final IGameImage image;
    private final IGraphics graphics;

    private volatile boolean noStopRequested = true;
    private volatile boolean isDrawingComplete = false;

    private final int x0;
    private final int y0;
    private final int x1;
    private final int y1;

    private int currentX;
    private int initialX;

    // Ranges from 5 to -3 and is used to determine the number of calculations per pixel
    // 5 -> 32x32 blocks -3 -> 1x1 blocks with 64 calculations
    private int logCP;

    public FractalDrawer(GameImageDrawer imageDrawer, IGameImage image, int x0, int y0, int x1, int y1, int initialX, int logCP) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;

        this.initialX = initialX;
        this.logCP = logCP;

        this.imageDrawer = imageDrawer;
        this.image = image;
        graphics = image.getGraphics();
    }

    public void startDrawing(ThreadWorker worker) {
        worker.workOn(() -> {
            while (noStopRequested && logCP >= MIN_LOG_CP) {
                drawSquares();
                if (noStopRequested) {
                    --logCP;
                }
            }
            isDrawingComplete = true;
            synchronized (this) {
                notify();
            }
        });
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
                    graphics.drawPixel(currentX - x0, y - y0);
                }
                y += width;
            } while (noStopRequested && y < y1);
            if (noStopRequested) {
                currentX += width;
            }
        } while (noStopRequested && currentX < x1);
        initialX = x0;
    }

    @Override
    public void drawOn(IGraphics g) {
        imageDrawer.drawImage(g, image, x0, y0, getImageWidth(), getImageHeight());
    }

    public void stopAndWait() {
        noStopRequested = false;
        synchronized (this) {
            while (!isDrawingComplete) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public boolean isDrawingComplete() {
        return isDrawingComplete;
    }

    public boolean isSlowerThan(FractalDrawer other) {
        return (logCP > other.logCP)
                || (logCP == other.logCP && getImageHeight() > other.getImageHeight())
                || (getImageHeight() == other.getImageHeight() && currentX - initialX < other.currentX - other.initialX);
    }

    public boolean isSplittable() {
        return getImageHeight() > 1;
    }

    public FractalDrawer[] splitVertically() {
        stopAndWait();

        IGameImage top = splitImage(0, 0, getImageWidth(), getImageHeight() / 2);
        IGameImage bottom = splitImage(0, getImageHeight() / 2, getImageWidth(), getImageHeight() - (getImageHeight() / 2));

        return new FractalDrawer[] {
                new FractalDrawer(imageDrawer, top, x0, y0, x1, y0 + getImageHeight() / 2, currentX, logCP),
                new FractalDrawer(imageDrawer, bottom, x0, y0 + getImageHeight() / 2, x1, y1, currentX, logCP)
        };
    }

    private IGameImage splitImage(int x, int y, int width, int height) {
        IGameImage split = image.getSubimage(x, y, width, height);
        IGraphics g = split.getGraphics();
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
