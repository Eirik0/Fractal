package fr.draw;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import gt.async.ThreadWorker;
import gt.gameentity.IGameImage;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.util.EMath;

public class FractalDrawerDelegate {
    private final int horizontalDrawers;
    private final int verticalDrawers;

    private final int maxAvailableWorkers;
    private final BlockingQueue<ThreadWorker> availableWorkers;
    private final List<FractalDrawer> drawers = new ArrayList<>();

    private volatile boolean needsNewImage = false;
    private volatile boolean savingImage = false;

    private final IGameImageDrawer imageDrawer;
    private IGameImage currentImage;
    private IGraphics currentGraphics;

    public FractalDrawerDelegate(IGameImageDrawer imageDrawer, int imageWidth, int imageHeight) {
        this.imageDrawer = imageDrawer;
        currentImage = imageDrawer.newGameImage(imageWidth, imageHeight);
        currentGraphics = currentImage.getGraphics();

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

        maxAvailableWorkers = verticalDrawers * horizontalDrawers;
        availableWorkers = new ArrayBlockingQueue<>(maxAvailableWorkers);
        for (int i = 0; i < maxAvailableWorkers; ++i) {
            availableWorkers.add(new ThreadWorker(worker -> workerComplete(worker)));
        }

        setUpDrawers(imageWidth, imageHeight);
    }

    private void setUpDrawers(int imageWidth, int imageHeight) {
        drawers.clear();

        if (currentImage.getWidth() != imageWidth || currentImage.getHeight() != imageHeight || savingImage) {
            IGameImage oldImage = currentImage;
            currentImage = imageDrawer.newGameImage(imageWidth, imageHeight);
            currentGraphics = currentImage.getGraphics();
            if (!savingImage) {
                imageDrawer.drawImage(currentGraphics, oldImage, 0, 0, imageWidth, imageHeight);
            }
        }

        int logCP = savingImage ? FractalDrawer.MIN_LOG_CP : FractalDrawer.INITIAL_LOG_CP;
        for (int x = 0; x < horizontalDrawers; ++x) {
            for (int y = 0; y < verticalDrawers; ++y) {
                int x0 = EMath.round((double) imageWidth * x / horizontalDrawers);
                int y0 = EMath.round((double) imageHeight * y / verticalDrawers);
                int x1 = EMath.round((double) imageWidth * (x + 1) / horizontalDrawers);
                int y1 = EMath.round((double) imageHeight * (y + 1) / verticalDrawers);
                IGameImage subimage = currentImage.getSubimage(x0, y0, x1 - x0, y1 - y0);
                FractalDrawer drawer = new FractalDrawer(imageDrawer, subimage, x0, y0, x1, y1, x0, logCP);
                drawers.add(drawer);
                startWork(drawer);
            }
        }
    }

    private void startWork(FractalDrawer drawer) {
        try {
            drawer.startDrawing(availableWorkers.take());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isDrawingComplete() {
        if (availableWorkers.size() < maxAvailableWorkers || drawers.size() < maxAvailableWorkers) {
            return false;
        }
        for (FractalDrawer drawer : drawers) {
            if (!drawer.isDrawingComplete()) {
                return false;
            }
        }
        return true;
    }

    public void requestReset() {
        requestReset(false);
    }

    public void requestReset(boolean savingImage) {
        needsNewImage = true;
        this.savingImage = savingImage;
    }

    public IGameImage requestImage(int imageWidth, int imageHeight) {
        if (needsNewImage) {
            needsNewImage = false;
            createNewImage(imageWidth, imageHeight);
        }

        return createImageFromDrawers();
    }

    private void createNewImage(int imageWidth, int imageHeight) {
        for (FractalDrawer drawer : drawers) {
            drawer.stopAndWait();
        }

        setUpDrawers(imageWidth, imageHeight);
    }

    private IGameImage createImageFromDrawers() {
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
            FractalDrawer slowest = findSlowestDrawer();

            if (!slowest.isDrawingComplete() && slowest.isSplittable()) {
                firstFinished.drawOn(currentGraphics);
                drawers.remove(firstFinished);
                drawers.remove(slowest);
                FractalDrawer[] split = slowest.splitVertically();
                drawers.add(split[0]);
                startWork(split[0]);
                drawers.add(split[1]);
                startWork(split[1]);
            }
        }
    }

    private FractalDrawer findSlowestDrawer() {
        FractalDrawer slowest = drawers.get(0);
        for (int i = 1; i < drawers.size(); ++i) {
            FractalDrawer drawer = drawers.get(i);
            if (!drawer.isDrawingComplete() && drawer.isSlowerThan(slowest)) {
                slowest = drawer;
            }
        }
        return slowest;
    }

    private void workerComplete(ThreadWorker worker) {
        availableWorkers.add(worker);
    }
}
