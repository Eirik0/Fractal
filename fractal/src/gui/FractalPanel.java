package gui;

import gui.FractalMain.Fractal;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import julia.JuliaImageDrawerDelegate;

public class FractalPanel extends JPanel {
	public static int DEFAULT_WIDTH = 1000;
	public static int DEFAULT_HEIGHT = DEFAULT_WIDTH * 9 / 16;

	private static final int FRAMES_PER_MILLI = (int) ((1.0 / 60) * 1000);

	private JuliaImageDrawerDelegate delegate;
	private FractalMouseAdapter mouseAdapter;

	public FractalPanel(Fractal fractal) {
		setBackground(Color.BLACK);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		delegate = new JuliaImageDrawerDelegate(getWidth(), getHeight(), fractal);

		mouseAdapter = new FractalMouseAdapter(delegate);

		addListeners();

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> repaint(), 0, FRAMES_PER_MILLI, TimeUnit.MILLISECONDS);
	}

	private void addListeners() {
		// resize
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (getWidth() > 0 && getHeight() > 0) {
					delegate.setImageDimensions(getWidth(), getHeight());
				}
			}
		});

		// mouse wheel
		addMouseWheelListener(e -> delegate.zoom(e.getWheelRotation()));
		// mouse pressed / released
		addMouseListener(mouseAdapter);
		// mouse dragging
		addMouseMotionListener(mouseAdapter);
	}

	public void resetZoom() {
		delegate.resetZoom();
	}

	public void resetColor() {
		delegate.resetColor();
	}

	public void setNumberOfColors(int numberOfColors) {
		delegate.setNumberOfColors(numberOfColors);
	}

	public void setDistanceBetweenColors(int distanceBetweenColors) {
		delegate.setDistanceBetweenColors(distanceBetweenColors);
	}

	public void setFractal(Fractal fractal) {
		delegate.setFractal(fractal);
	}

	@Override
	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		BufferedImage image = delegate.requestImage();
		g.drawImage(image, 0, 0, width, height, null);

		if (mouseAdapter.isDragging()) {
			g.setColor(Color.RED);
			g.drawRect(mouseAdapter.getDragUpperLeftX(), mouseAdapter.getDragUpperLeftY(), mouseAdapter.getWidth(), mouseAdapter.getHeight());
		}
	}

	public void save() {
		BufferedImage image = delegate.requestImage();
		JFileChooser jFileChooser = new JFileChooser();
		if (jFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				File file = jFileChooser.getSelectedFile();
				ImageIO.write(image, "bmp", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
