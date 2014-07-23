package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import julia.*;

public class FractalMain {
	public static void main(String[] args) {
		JuliaColorer.resetColors();

		FractalPanel fractalPanel = new FractalPanel(new JuliaSet(-0.1, 0.651));

		JPanel backgroundPanel = new JPanel(new BorderLayout());
		backgroundPanel.add(fractalPanel, BorderLayout.CENTER);

		JLabel divisorLabel = new JLabel("a + bi (a, b): ");
		divisorLabel.setBackground(Color.BLACK);
		divisorLabel.setForeground(Color.GREEN);

		ComplexNumberField complexNumberField = new ComplexNumberField(fractalPanel);
		complexNumberField.setText("-0.1, 0.651");

		JButton resetZoomButton = new JButton("Reset Zoom");
		resetZoomButton.addActionListener(e -> fractalPanel.resetZoom());

		JButton resetColorButton = new JButton("Reset Color");
		resetColorButton.addActionListener(e -> fractalPanel.resetColor());

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(e -> fractalPanel.save());

		JSlider colorSlider = createSlider(2, 50, JuliaColorer.DEFAULT_NUMBER_OF_COLORS);
		colorSlider.addChangeListener(e -> fractalPanel.setNumberOfColors(colorSlider.getValue()));

		JSlider distanceSlider = createSlider(1, 100, JuliaColorer.DEFAULT_DISTANCE_BETWEEN_COLORS);
		distanceSlider.addChangeListener(e -> fractalPanel.setDistanceBetweenColors(distanceSlider.getValue()));

		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.BLACK);
		buttonPanel.add(saveButton);
		buttonPanel.add(divisorLabel);
		buttonPanel.add(complexNumberField);
		buttonPanel.add(resetZoomButton);
		buttonPanel.add(resetColorButton);
		buttonPanel.add(colorSlider);
		buttonPanel.add(distanceSlider);

		JPanel glassPanel = new JPanel(new BorderLayout());
		glassPanel.add(buttonPanel, BorderLayout.NORTH);

		JFrame mainFrame = new JFrame();
		mainFrame.setTitle("Mandelbrot");
		mainFrame.setSize(FractalPanel.DEFAULT_WIDTH, FractalPanel.DEFAULT_HEIGHT);
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

	private static JSlider createSlider(int minimum, int maximum, int defaultVlue) {
		JSlider slider = new JSlider(JSlider.HORIZONTAL);
		slider.setBackground(Color.BLACK);
		slider.setMinimum(minimum);
		slider.setMaximum(maximum);
		slider.setSnapToTicks(true);
		slider.setMinorTickSpacing(minimum);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setValue(defaultVlue);
		return slider;
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
		final double cx;
		final double cy;

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
