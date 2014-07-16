package julia;

import gui.FractalMain.Fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class JuliaDrawer {
	private BufferedImage image;

	private Fractal julia;
	private JuliaSizer sizer;
	private JuliaColorer colorer;

	private boolean noStopRequested;
	private boolean isDrawingComplete;
	private double dataPerPixel;

	private int currentX;
	private int initialX;

	private int x0;
	private int y0;
	private int x1;
	private int y1;

	public JuliaDrawer(Fractal julia, JuliaSizer sizer, int x0, int y0, int x1, int y1) {
		init(julia, sizer, x0, y0, x1, y1, x0, 0.03125);
		image = new BufferedImage(x1 - x0, y1 - y0, BufferedImage.TYPE_INT_RGB);
		startDrawing();
	}

	private JuliaDrawer(BufferedImage image, Fractal julia, JuliaSizer sizer, int x0, int y0, int x1, int y1, double dataPerPixel, int initialX) {
		init(julia, sizer, x0, y0, x1, y1, initialX, dataPerPixel);
		this.image = image;
		startDrawing();
	}

	private void init(Fractal julia, JuliaSizer sizer, int x0, int y0, int x1, int y1, int initialX, double dataPerPixel) {
		this.julia = julia;
		this.sizer = sizer;

		colorer = JuliaColorer.getInstance();

		noStopRequested = true;
		isDrawingComplete = false;

		this.x0 = x0;
		this.y0 = y0;

		this.x1 = x1;
		this.y1 = y1;

		this.initialX = initialX;
		this.dataPerPixel = dataPerPixel;
	}

	public List<JuliaDrawer> splitVertically() {
		double newDataPointsPer = dataPerPixel;
		int xAtTimeOfSplit = currentX;
		requestStop(); // this will inadvertently increment dataPointsPer

		int newHeight = (y1 - y0) / 2;
		int newWidth = getImageWidth();

		int midpoint = y0 + newHeight;

		BufferedImage top = image.getSubimage(0, 0, newWidth, newHeight);
		Graphics2D g1 = top.createGraphics();
		g1.setColor(Color.RED);
		g1.drawLine(xAtTimeOfSplit - x0, 0, newWidth, 0);

		BufferedImage bottom = image.getSubimage(0, newHeight, newWidth, (y1 - y0) - newHeight);
		Graphics2D g2 = bottom.createGraphics();
		g2.setColor(Color.RED);
		g2.drawLine(xAtTimeOfSplit - x0, 0, newWidth, 0);

		List<JuliaDrawer> drawers = new ArrayList<>();
		drawers.add(new JuliaDrawer(top, julia, sizer, x0, y0, x1, midpoint, newDataPointsPer, xAtTimeOfSplit));
		drawers.add(new JuliaDrawer(bottom, julia, sizer, x0, midpoint, x1, y1, newDataPointsPer, xAtTimeOfSplit));

		return drawers;
	}

	public void requestStop() {
		noStopRequested = false;
	}

	public boolean isSlowerThan(JuliaDrawer slowest) {
		if (dataPerPixel < slowest.dataPerPixel) {
			return true;
		} else if (dataPerPixel == slowest.dataPerPixel && getImageHeight() > slowest.getImageHeight()) {
			return true;
		} else if (getImageHeight() == slowest.getImageHeight() && currentX - x0 < slowest.currentX - slowest.x0) {
			return true;
		}
		return false;
	}

	public void drawOn(Graphics g) {
		g.drawImage(image, x0, y0, getImageWidth(), getImageHeight(), null);
	}

	public boolean isDrawingComplete() {
		return isDrawingComplete;
	}

	public int getImageHeight() {
		return y1 - y0;
	}

	private int getImageWidth() {
		return x1 - x0;
	}

	private void startDrawing() {
		new Thread(() -> {
			Graphics2D g = image.createGraphics();

			while (noStopRequested && dataPerPixel <= 8) {
				drawSquares(g, dataPerPixel);
				dataPerPixel *= 2;
			}

			isDrawingComplete = true;
		}).start();
	}

	private void drawSquares(Graphics2D g, double dataPerPixel) {
		double pixelPerData = 1 / dataPerPixel;
		int width = dataPerPixel < 1 ? (int) pixelPerData : 1;
		int offset = width / 2;

		currentX = initialX;
		do {
			int y = y0;
			do {
				if (dataPerPixel <= 1) {
					g.setColor(colorer.getColor(julia.getIterations(sizer.getX(currentX + offset), sizer.getY(y + offset))));
				} else {
					g.setColor(getAntiAliasedColor(pixelPerData, currentX, y));
				}
				g.fillRect(currentX - x0, y - y0, width, width);
				y += width;
			} while (noStopRequested && y < y1);
			currentX += width;
		} while (noStopRequested && currentX < x1);
		initialX = x0;
	}

	private Color getAntiAliasedColor(double pixelPerData, int x, int y) {
		List<Double> xs = new ArrayList<Double>();
		List<Double> ys = new ArrayList<Double>();

		for (double xOffset = 0; noStopRequested && xOffset < 1; xOffset += pixelPerData) {
			for (double yOffset = 0; noStopRequested && yOffset < 1; yOffset += pixelPerData) {
				xs.add(sizer.getX(x + xOffset));
				ys.add(sizer.getY(y + yOffset));
			}
		}
		if (!noStopRequested) {
			return Color.BLACK;
		}
		return colorer.getColor(julia.getIterations(xs, ys));
	}

	@Override
	public String toString() {
		return "(" + x0 + " - " + x1 + ") x (" + y0 + " - " + y1 + "): " + (noStopRequested ? "" : "Stop Requested ")
				+ (isDrawingComplete ? "Finished Drawing " : "") + dataPerPixel + " data/pixel";
	}
}
