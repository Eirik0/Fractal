package fr.draw;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import gt.gameentity.DrawingMethods;

public class FractalDrawerDelegate {
    private final int horizontalDrawers;
    private final int verticalDrawers;

    private List<FractalDrawer> drawers = new ArrayList<>();

    private boolean needsNewImage = false;

    private BufferedImage currentImage;
    private Graphics2D currentGraphics;

    public FractalDrawerDelegate(int imageWidth, int imageHeight) {
        currentImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        currentGraphics = currentImage.createGraphics();

        int numCores = Runtime.getRuntime().availableProcessors();
        int coresToUse = Math.max(1, numCores - 2);
        if (numCores >= 64) {
            horizontalDrawers = 8;
        } else if (numCores >= 32) {
            horizontalDrawers = 4;
        } else if (numCores >= 4) {
            horizontalDrawers = 2;
        } else {
            horizontalDrawers = 1;
        }
        verticalDrawers = coresToUse / horizontalDrawers;

        setUpDrawers(imageWidth, imageHeight);
    }

    private void setUpDrawers(int imageWidth, int imageHeight) {
        drawers.clear();

        if (currentImage.getWidth() != imageWidth || currentImage.getHeight() != imageHeight) {
            BufferedImage oldImage = currentImage;
            currentImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
            currentGraphics = currentImage.createGraphics();
            currentGraphics.drawImage(oldImage, (imageWidth - oldImage.getWidth()) / 2, (imageHeight - oldImage.getHeight()) / 2, null);
        }

        for (int x = 0; x < horizontalDrawers; ++x) {
            for (int y = 0; y < verticalDrawers; ++y) {
                int x0 = DrawingMethods.roundS((double) imageWidth * x / horizontalDrawers);
                int y0 = DrawingMethods.roundS((double) imageHeight * y / verticalDrawers);
                int x1 = DrawingMethods.roundS((double) imageWidth * (x + 1) / horizontalDrawers);
                int y1 = DrawingMethods.roundS((double) imageHeight * (y + 1) / verticalDrawers);
                drawers.add(new FractalDrawer(currentImage.getSubimage(x0, y0, x1 - x0, y1 - y0), x0, y0, x1, y1));
            }
        }
    }

    public void requestReset() {
        needsNewImage = true;
    }

    public BufferedImage requestImage(int imageWidth, int imageHeight) {
        if (needsNewImage) {
            needsNewImage = false;
            return requestNewImage(imageWidth, imageHeight);
        }

        return createImageFromDrawers();
    }

    private BufferedImage requestNewImage(int imageWidth, int imageHeight) {
        for (FractalDrawer drawer : drawers) {
            drawer.requestStop();
        }

        setUpDrawers(imageWidth, imageHeight);

        return createImageFromDrawers();
    }

    private BufferedImage createImageFromDrawers() {
        for (FractalDrawer drawer : drawers) {
            drawer.drawOn(currentGraphics);
        }
        return currentImage;
    }

    public void checkSplit() {
        FractalDrawer firstFinished = null;
        for (FractalDrawer drawer : drawers) {
            if (drawer.isDrawingComplete()) {
                firstFinished = drawer;
                break;
            }
        }

        if (firstFinished != null) {
            firstFinished.drawOn(currentGraphics);
            drawers.remove(firstFinished);

            if (drawers.size() == 0 || drawers.size() >= horizontalDrawers * verticalDrawers) {
                return;
            }

            FractalDrawer slowest = findSlowestDrawer();

            if (!slowest.isDrawingComplete() && slowest.isSplittable()) {
                drawers.remove(slowest);
                drawers.addAll(slowest.splitVertically());
            }
        }
    }

    private FractalDrawer findSlowestDrawer() {
        FractalDrawer slowest = drawers.get(0);
        for (int i = 1; i < drawers.size(); ++i) {
            if (drawers.get(i).isSlowerThan(slowest)) {
                slowest = drawers.get(i);
            }
        }
        return slowest;
    }
}
