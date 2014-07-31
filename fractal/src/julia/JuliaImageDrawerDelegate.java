package julia;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.*;

import julia.Fractals.Fractal;

public class JuliaImageDrawerDelegate {
	private Fractal juliaSet;
	private JuliaSizer sizer;

	private final int horizontalDrawers;
	private final int verticalDrawers;

	private List<JuliaDrawer> drawers = new ArrayList<>();

	private boolean needsNewImage = false;

	BufferedImage currentImage;
	Graphics2D currentGraphics;

	public JuliaImageDrawerDelegate(int imageWidth, int imageHeight, Fractal fractal) {
		JuliaColorer.resetColors();

		juliaSet = fractal;

		sizer = new JuliaSizer(-2.5, -2.5, 5, 5, imageWidth, imageHeight);

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

				drawers.add(new JuliaDrawer(currentImage.getSubimage(x0, y0, x1 - x0, y1 - y0), juliaSet, sizer, x0, y0, x1, y1));
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

			if (drawers.size() == 0 || drawers.size() >= horizontalDrawers * verticalDrawers) {
				return;
			}

			JuliaDrawer slowest = getSlowestDrawer();

			if (!slowest.isDrawingComplete() && slowest.isSplittable()) {
				drawers.remove(slowest);
				drawers.addAll(slowest.splitVertically());
			}
		}
	}

	private JuliaDrawer getSlowestDrawer() {
		JuliaDrawer slowest = drawers.get(0);
		for (JuliaDrawer drawer : drawers) {
			if (drawer.isSlowerThan(slowest)) {
				slowest = drawer;
			}
		}
		return slowest;
	}
}
