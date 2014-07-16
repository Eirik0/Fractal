package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.*;

public class FractalMain {
	public static void main(String[] args) {
		FractalPanel fractalPanel = new FractalPanel(new JuliaSet(-0.1, 0.651));

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

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.NORTH);
		mainPanel.add(fractalPanel, BorderLayout.CENTER);

		JFrame mainFrame = new JFrame();
		mainFrame.setTitle("Mandelbrot");
		mainFrame.setSize(729, 729);
		mainFrame.add(mainPanel);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		mainFrame.setVisible(true);
	}

	public static interface Fractal {
		public static final int MAX_ITERATIONS = 20000;

		public int getIterations(double x, double y);

		public default int[] getIterations(List<Double> xs, List<Double> ys) {
			return IntStream.range(0, xs.size()).map(i -> getIterations(xs.get(i), ys.get(i))).toArray();
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
