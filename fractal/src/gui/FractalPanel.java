package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import julia.JuliaImageDrawerDelegate;

public class FractalPanel extends JPanel {
	private JuliaImageDrawerDelegate delegate; // initialized on first resize

	// For mouse events
	private boolean isDragging = false;
	private int dragStartX = 0;
	private int dragStartY = 0;
	private int dragEndX = 0;
	private int dragEndY = 0;

	public FractalPanel() {
		setBackground(Color.BLACK);
		setSize(729, 729);

		delegate = new JuliaImageDrawerDelegate(getWidth(), getHeight());

		addListeners();

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> SwingUtilities.invokeLater(() -> repaint()), 0, 33, TimeUnit.MILLISECONDS);
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
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dragStartX = e.getX();
				dragStartY = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				int br_x = dragStartX > dragEndX ? dragStartX : dragEndX;
				int br_y = dragStartY > dragEndY ? dragStartY : dragEndY;

				delegate.zoomTo(getDragUpperLeftX(), getDragUpperLeftY(), br_x, br_y);

				isDragging = false;
			}
		});

		// mouse dragging
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				isDragging = true;

				dragEndX = e.getX();
				dragEndY = e.getY();
			}
		});
	}

	public void resetZoom() {
		delegate.resetZoom();
	}

	public void resetColor() {
		delegate.resetColor();
	}

	public void setJulia(double x, double y) {
		delegate.setC(x, y);
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

	@Override
	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		BufferedImage image = delegate.requestImage();
		g.drawImage(image, 0, 0, width, height, null);

		if (isDragging) {
			g.setColor(Color.RED);
			g.drawRect(getDragUpperLeftX(), getDragUpperLeftY(), Math.abs(dragEndX - dragStartX), Math.abs(dragEndY - dragStartY));
		}
	}

	private int getDragUpperLeftY() {
		return dragStartY < dragEndY ? dragStartY : dragEndY;
	}

	private int getDragUpperLeftX() {
		return dragStartX < dragEndX ? dragStartX : dragEndX;
	}
}
