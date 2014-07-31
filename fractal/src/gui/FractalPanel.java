package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import julia.*;

public class FractalPanel extends JPanel {
	private static final int FRAMES_PER_MILLI = (int) ((1.0 / 60) * 1000);

	private JuliaImageDrawerDelegate delegate;
	private FractalMouseAdapter mouseAdapter;

	FractalPanel(JuliaImageDrawerDelegate delegate) {
		this.delegate = delegate;
		mouseAdapter = new FractalMouseAdapter(delegate);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (getWidth() > 0 && getHeight() > 0) {
					JuliaSizer.setImageDimensions(getWidth(), getHeight());
					delegate.requestReset();
				}
			}
		});

		addMouseWheelListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> repaint(), 0, FRAMES_PER_MILLI, TimeUnit.MILLISECONDS);
	}

	@Override
	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		BufferedImage image = delegate.requestImage();
		g.drawImage(image, 0, 0, width, height, null);

		if (mouseAdapter.isDragging()) {
			g.setColor(Color.RED);
			// TODO draw predicted square with static sizer
			g.drawRect(mouseAdapter.getDragUpperLeftX(), mouseAdapter.getDragUpperLeftY(), mouseAdapter.getWidth(), mouseAdapter.getHeight());
		}
	}

	public void save() {
		JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.home") + File.separator + "Desktop");
		if (jFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = jFileChooser.getSelectedFile();
			try {
				ImageIO.write(delegate.requestImage(), "bmp", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
