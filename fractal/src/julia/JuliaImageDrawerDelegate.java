package julia;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;

import julia.Fractals.Fractal;

public class JuliaImageDrawerDelegate {
	private Fractal juliaSet;
	private JuliaSizer sizer;

	private int numberOfDrawers = 0;
	private List<JuliaDrawer> drawers = new ArrayList<>();

	private boolean needsNewImage = false;

	BufferedImage currentImage;
	Graphics2D currentGraphics;

	public JuliaImageDrawerDelegate(int imageWidth, int imageHeight, Fractal fractal) {
		juliaSet = fractal;

		sizer = new JuliaSizer(-2.5, -2.5, 5, 5, imageWidth, imageHeight);

		currentImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		currentGraphics = currentImage.createGraphics();

		setUpDrawers(imageWidth, imageHeight);
	}

	private void setUpDrawers(double imageWidth, double imageHeight) {
		drawers.clear();

		int coresToUse = Math.max(1, Runtime.getRuntime().availableProcessors() - 2);
		int horizontalDrawers = coresToUse == 1 ? 1 : 2;
		int verticalDrawers = coresToUse / horizontalDrawers;
		numberOfDrawers = horizontalDrawers * verticalDrawers;

		double width = imageWidth / horizontalDrawers;
		double height = imageHeight / verticalDrawers;
		for (int x = 0; x < horizontalDrawers; ++x) {
			for (int y = 0; y < verticalDrawers; ++y) {
				double x0 = x * width;
				double x1 = x0 + width;

				double y0 = y * height;
				double y1 = y0 + height;

				drawers.add(new JuliaDrawer(juliaSet, sizer, (int) x0, (int) y0, (int) x1, (int) y1));
			}
		}
	}

	public BufferedImage requestImage() {
		if (needsNewImage) {
			needsNewImage = false;
			return requestNewImage();
		}

		checkSplit();

		return createImageFromDrawers();
	}

	private BufferedImage requestNewImage() {
		for (JuliaDrawer drawer : drawers) {
			drawer.requestStop();
		}

		setUpDrawers(sizer.getImageWidth(), sizer.getImageHeight());

		return createImageFromDrawers();
	}

	private BufferedImage createImageFromDrawers() {
		int width = sizer.getImageWidth();
		int height = sizer.getImageHeight();

		if (currentImage.getWidth() != width || currentImage.getHeight() != height) {
			currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			currentGraphics = currentImage.createGraphics();
		}

		for (JuliaDrawer drawer : drawers) {
			drawer.drawOn(currentGraphics);
		}

		return currentImage;
	}

	// Sizer
	public void setImageDimensions(int width, int height) {
		sizer.setImageDimensions(width, height);
		needsNewImage = true;
	}

	public void resetZoom() {
		sizer.setJuliaBounds(-2.5, -2.5, 5, 5);
		needsNewImage = true;
	}

	public void zoom(int rotations) {
		sizer.zoom(rotations);
		needsNewImage = true;
	}

	public void zoomTo(double ul_x, double ul_y, double br_x, double br_y) {
		sizer.zoomTo(ul_x, ul_y, br_x, br_y);
		needsNewImage = true;
	}

	// Other methods
	public void setFractal(Fractal fractal) {
		juliaSet = fractal;
		needsNewImage = true;
	}

	public void resetColor() {
		JuliaColorer.resetColors();
		needsNewImage = true;
	}

	public void setNumberOfColors(int numberOfColors) {
		JuliaColorer.setNumberOfColors(numberOfColors);
		needsNewImage = true;
	}

	public void setDistanceBetweenColors(int distanceBetweenColors) {
		JuliaColorer.setDistanceBetweenColors(distanceBetweenColors);
		needsNewImage = true;
	}

	private void checkSplit() {
		JuliaDrawer firstFinished = null;
		for (JuliaDrawer drawer : drawers) {
			if (drawer.isDrawingComplete()) {
				firstFinished = drawer;
				break;
			}
		}

		if (firstFinished != null) {
			firstFinished.drawOn(currentGraphics);
			drawers.remove(firstFinished);

			if (drawers.size() == 0 || drawers.size() >= numberOfDrawers) {
				return;
			}

			JuliaDrawer slowest = getSlowest();

			if (!slowest.isDrawingComplete() && slowest.isSplittable()) {
				drawers.remove(slowest);
				drawers.addAll(slowest.splitVertically());
			}
		}
	}

	private JuliaDrawer getSlowest() {
		JuliaDrawer slowest = drawers.get(0);
		for (JuliaDrawer drawer : drawers) {
			if (drawer.isSlowerThan(slowest)) {
				slowest = drawer;
			}
		}
		return slowest;
	}
}
