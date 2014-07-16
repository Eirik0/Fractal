package julia;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class JuliaDrawer {
	private BufferedImage image;

	private JuliaSet julia;
	private JuliaSizer sizer;
	private JuliaColorer colorer;

	private boolean noStopRequested;
	private boolean isDrawingComplete;
	private double dataPerPixel;

	private int x;
	private int initialX;

	private int x0;
	private int y0;
	private int x1;
	private int y1;

	public JuliaDrawer(JuliaSet julia, JuliaSizer sizer, int x0, int y0, int x1, int y1) {
		init(julia, sizer, x0, y0, x1, y1, x0, 0.03125);
		image = new BufferedImage(x1 - x0, y1 - y0, BufferedImage.TYPE_INT_RGB);
		startDrawing();
	}

	private JuliaDrawer(BufferedImage image, JuliaSet julia, JuliaSizer sizer, int x0, int y0, int x1, int y1, double dataPerPixel, int initialX) {
		init(julia, sizer, x0, y0, x1, y1, initialX, dataPerPixel);
		this.image = image;
		startDrawing();
	}

	private void init(JuliaSet julia, JuliaSizer sizer, int x0, int y0, int x1, int y1, int initialX, double dataPerPixel) {
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
		int currentX = getCurrentX();
		requestStop(); // this will inadvertently increment dataPointsPer

		int newHeight = (y1 - y0) / 2;
		int newWidth = x1 - x0;

		int midpoint = y0 + newHeight;

		BufferedImage top = image.getSubimage(0, 0, newWidth, newHeight);
		Graphics2D g1 = top.createGraphics();
		g1.setColor(Color.RED);
		g1.drawLine(currentX - x0, 0, newWidth, 0);

		BufferedImage bottom = image.getSubimage(0, newHeight, newWidth, (y1 - y0) - newHeight);
		Graphics2D g2 = bottom.createGraphics();
		g2.setColor(Color.RED);
		g2.drawLine(currentX - x0, 0, newWidth, 0);

		List<JuliaDrawer> drawers = new ArrayList<>();
		drawers.add(new JuliaDrawer(top, julia.clone(), sizer, x0, y0, x1, midpoint, newDataPointsPer, currentX));
		drawers.add(new JuliaDrawer(bottom, julia.clone(), sizer, x0, midpoint, x1, y1, newDataPointsPer, currentX));

		return drawers;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void requestStop() {
		noStopRequested = false;
	}

	public boolean isStopRequested() {
		return !noStopRequested;
	}

	public boolean isDrawingComplete() {
		return isDrawingComplete;
	}

	public double getDataPerPixel() {
		return dataPerPixel;
	}

	public int getImageX0() {
		return x0;
	}

	public int getImageY0() {
		return y0;
	}

	public int getImageHeight() {
		return y1 - y0;
	}

	public int getImageWidth() {
		return x1 - x0;
	}

	private int getCurrentX() {
		return x;
	}

	private void startDrawing() {
		new Thread(() -> {
			Graphics2D g = image.createGraphics();

			while (noStopRequested && dataPerPixel <= 8) {
				drawSquares(g, dataPerPixel);
//				if (dataPerPixel >= 1) {
//					julia.doubleMaxIterations();
//				}
				dataPerPixel *= 2;
			}

			isDrawingComplete = true;
		}).start();
	}

	private void drawSquares(Graphics2D g, double dataPerPixel) {
		double pixelPerData = 1 / dataPerPixel;
		int width = dataPerPixel < 1 ? (int) pixelPerData : 1;
		int offset = width / 2;

		x = initialX;
		do {
			int y = y0;
			do {
				if (dataPerPixel <= 1) {
					g.setColor(colorer.getColor(julia.getIterations(sizer.getX(x + offset), sizer.getY(y + offset)), julia.maxIterations));
				} else {
					g.setColor(getAntiAliasedColor(pixelPerData, x, y));
				}
				g.fillRect(x - x0, y - y0, width, width);
				y += width;
			} while (noStopRequested && y < y1);
			x += width;
		} while (noStopRequested && x < x1);

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
		return colorer.getColor(julia.getIterations(xs, ys), julia.maxIterations);
	}

	@Override
	public String toString() {
		return "(" + x0 + " - " + x1 + ") x (" + y0 + " - " + y1 + "): " + (noStopRequested ? "" : "Stop Requested ")
				+ (isDrawingComplete ? "Finished Drawing " : "") + dataPerPixel + " data/pixel";
	}
}
