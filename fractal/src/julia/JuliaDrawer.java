package julia;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import julia.Fractals.Fractal;

public class JuliaDrawer {
	private static final int INITIAL_PIXEL_PER_DATA = 32;
	private static final double MIN_PIXEL_PER_DATA = 1.0 / 8;

	private BufferedImage image;
	private Graphics2D graphics;

	private Fractal julia;
	private JuliaSizer sizer;

	private boolean noStopRequested = true;
	private boolean isDrawingComplete = false;

	private int currentX;
	private int initialX;
	private double pixelPerData;

	private int x0;
	private int y0;
	private int x1;
	private int y1;

	public JuliaDrawer(BufferedImage currentImage, Fractal julia, JuliaSizer sizer, int x0, int y0, int x1, int y1) {
		init(julia, sizer, x0, y0, x1, y1, x0, INITIAL_PIXEL_PER_DATA);
		setImage(new BufferedImage(x1 - x0, y1 - y0, BufferedImage.TYPE_INT_RGB));
		graphics.drawImage(currentImage, 0, 0, null);
		startDrawing();
	}

	private JuliaDrawer(BufferedImage image, Fractal julia, JuliaSizer sizer, int x0, int y0, int x1, int y1, double pixelPerData, int initialX) {
		init(julia, sizer, x0, y0, x1, y1, initialX, pixelPerData);
		setImage(image);
		startDrawing();
	}

	private void init(Fractal julia, JuliaSizer sizer, int x0, int y0, int x1, int y1, int initialX, double pixelPerData) {
		this.julia = julia;
		this.sizer = sizer;

		this.x0 = x0;
		this.y0 = y0;

		this.x1 = x1;
		this.y1 = y1;

		this.initialX = initialX;
		this.pixelPerData = pixelPerData;
	}

	private void setImage(BufferedImage image) {
		this.image = image;
		graphics = image.createGraphics();
	}

	private void startDrawing() {
		new Thread(() -> {
			while (noStopRequested && pixelPerData >= MIN_PIXEL_PER_DATA) {
				drawSquares();
				if (noStopRequested) {
					pixelPerData /= 2;
				}
			}

			isDrawingComplete = true;
		}).start();
	}

	private void drawSquares() {
		int width = pixelPerData > 1 ? (int) pixelPerData : 1;
		int offset = width / 2;

		currentX = initialX;
		do {
			int y = y0;
			do {
				if (pixelPerData >= 1) {
					graphics.setColor(JuliaColorer.getColor(julia.getIterations(sizer.getX(currentX + offset), sizer.getY(y + offset))));
					graphics.fillRect(currentX - x0, y - y0, width, width);
				} else {
					graphics.setColor(JuliaColorer.getColor(julia.getIterations(currentX, y, pixelPerData, sizer)));
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

	public boolean isSlowerThan(JuliaDrawer slowest) {
		return (pixelPerData > slowest.pixelPerData)
				|| (pixelPerData == slowest.pixelPerData && getImageHeight() > slowest.getImageHeight())
				|| (getImageHeight() == slowest.getImageHeight() && currentX < slowest.currentX);
	}

	public boolean isSplittable() {
		return getImageHeight() > 1;
	}

	public List<JuliaDrawer> splitVertically() {
		requestStop();

		BufferedImage top = splitImage(0, 0, getImageWidth(), getImageHeight() / 2);
		BufferedImage bottom = splitImage(0, getImageHeight() / 2, getImageWidth(), getImageHeight() - (getImageHeight() / 2));

		List<JuliaDrawer> drawers = new ArrayList<>();
		drawers.add(new JuliaDrawer(top, julia, sizer, x0, y0, x1, y0 + getImageHeight() / 2, pixelPerData, currentX));
		drawers.add(new JuliaDrawer(bottom, julia, sizer, x0, y0 + getImageHeight() / 2, x1, y1, pixelPerData, currentX));

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
				+ (isDrawingComplete ? "Finished Drawing " : "") + pixelPerData + " pixel/data";
	}
}
