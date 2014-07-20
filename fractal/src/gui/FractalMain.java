package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import julia.*;

public class FractalMain {
	public static void main(String[] args) {
		JuliaColorer.setColorPalette();

		FractalPanel fractalPanel = new FractalPanel(new JuliaSet(-0.1, 0.651));

		JPanel backgroundPanel = new JPanel(new BorderLayout());
		backgroundPanel.add(fractalPanel, BorderLayout.CENTER);

		JLabel divisorLabel = new JLabel("a + bi (a, b): ");
		divisorLabel.setBackground(Color.BLACK);
		divisorLabel.setForeground(Color.GREEN);

		JTextField complexNumberField = new JTextField();
		complexNumberField.setText("-0.1, 0.651");
		complexNumberField.setColumns(10);
		complexNumberField.addActionListener(e -> fractalPanel.setJulia(new MandelbrotSet()));

		JButton resetZoomButton = new JButton("Reset Zoom");
		resetZoomButton.addActionListener(e -> fractalPanel.resetZoom());

		JButton resetColorButton = new JButton("Reset Color");
		resetColorButton.addActionListener(e -> fractalPanel.resetColor());

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(e -> fractalPanel.save());

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.add(saveButton);
		buttonPanel.add(divisorLabel);
		buttonPanel.add(complexNumberField);
		buttonPanel.add(resetZoomButton);
		buttonPanel.add(resetColorButton);

		JPanel glassPanel = new JPanel(new BorderLayout());
		glassPanel.add(buttonPanel, BorderLayout.NORTH);

		JFrame mainFrame = new JFrame();
		mainFrame.setTitle("Mandelbrot");
		mainFrame.setSize(729, 729);
		mainFrame.add(backgroundPanel);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		JPanel glassPane = (JPanel) mainFrame.getGlassPane();
		glassPane.add(glassPanel);
		glassPane.setVisible(true);

		mainFrame.setVisible(true);
	}

	public static interface Fractal {
		public static final int MAX_ITERATIONS = 20000;

		public int getIterations(double x, double y);

		public default int[] getIterations(int x, int y, double pixelPerData, JuliaSizer sizer) {
			int dataPer = (int) Math.round(1 / pixelPerData);
			int[] iterations = new int[dataPer * dataPer];
			int i = 0;
			for (double xOffset = 0; xOffset < 1; xOffset += pixelPerData) {
				for (double yOffset = 0; yOffset < 1; yOffset += pixelPerData) {
					iterations[i] = getIterations(sizer.getX(x + xOffset), sizer.getY(y + yOffset));
					++i;
				}
			}
			return iterations;
		}
	}

	public static class MandelbrotSet implements Fractal {
		@Override
		public int getIterations(double x, double y) {
			int count = 0;

			double x1 = x;
			double y1 = y;

			double xsq = x * x;
			double ysq = y * y;

			while (xsq + ysq < 4 && count <= MAX_ITERATIONS) {
				xsq = x * x;
				ysq = y * y;

				y = 2 * x * y + y1;
				x = xsq - ysq + x1;

				++count;
			}

			return count;
		}
	}

	public static class JuliaSet implements Fractal {
		private final double cx;
		private final double cy;

		public JuliaSet(double cx, double cy) {
			this.cx = cx;
			this.cy = cy;
		}

		@Override
		public int getIterations(double x, double y) {
			int count = 0;

			double xsq = x * x;
			double ysq = y * y;

			while (xsq + ysq < 4 && count <= MAX_ITERATIONS) {
				xsq = x * x;
				ysq = y * y;

				y = 2 * x * y + cy;
				x = xsq - ysq + cx;
				++count;
			}

			return count;
		}
	}
}
