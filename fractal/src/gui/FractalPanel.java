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
	private JuliaImageDrawerDelegate delegate; // initialized on first resize
	private FractalMouseAdapter mouseAdapter;

	public FractalPanel(Fractal fractal) {
		setBackground(Color.BLACK);
		setSize(729, 729);

		delegate = new JuliaImageDrawerDelegate(getWidth(), getHeight(), fractal);

		mouseAdapter = new FractalMouseAdapter(delegate);

		addListeners();

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> repaint(), 0, 33, TimeUnit.MILLISECONDS);
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

	public void setJulia(Fractal fractal) {
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
