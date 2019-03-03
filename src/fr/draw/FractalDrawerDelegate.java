package fr.draw;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FractalDrawerDelegate {
    private final int horizontalDrawers;
    private final int verticalDrawers;

    private List<FractalDrawer> drawers = new ArrayList<>();

    private boolean needsNewImage = false;

    BufferedImage currentImage;
    Graphics2D currentGraphics;

    public FractalDrawerDelegate(int imageWidth, int imageHeight) {
        currentImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        currentGraphics = currentImage.createGraphics();

        int coresToUse = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);
        horizontalDrawers = coresToUse == 1 ? 1 : 2;
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

        double width = (double) imageWidth / horizontalDrawers;
        double height = (double) imageHeight / verticalDrawers;

        for (double x = 0; x < imageWidth; x += width) {
            for (double y = 0; y < imageHeight; y += height) {
                int x0 = (int) Math.round(x);
                int y0 = (int) Math.round(y);
                int x1 = (int) Math.round(x + width);
                int y1 = (int) Math.round(y + height);

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

        checkSplit();

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

    private void checkSplit() {
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

            FractalDrawer slowest = getSlowestDrawer();

            if (!slowest.isDrawingComplete() && slowest.isSplittable()) {
                drawers.remove(slowest);
                drawers.addAll(slowest.splitVertically());
            }
        }
    }

    private FractalDrawer getSlowestDrawer() {
        FractalDrawer slowest = drawers.get(0);
        for (FractalDrawer drawer : drawers) {
            if (drawer.isSlowerThan(slowest)) {
                slowest = drawer;
            }
        }
        return slowest;
    }
}
