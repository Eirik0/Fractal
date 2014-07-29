package gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import julia.Fractals.JuliaSet;
import julia.*;

public class FractalMain {
	public static void main(String[] args) {
		JuliaColorer.resetColors();

		FractalPanel fractalPanel = new FractalPanel(new JuliaSet(-0.1, 0.651));

		JPanel backgroundPanel = new JPanel(new BorderLayout());
		backgroundPanel.add(fractalPanel, BorderLayout.CENTER);

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(e -> fractalPanel.save());

		JLabel complexLabel = createLabel("a + bi: ");

		ComplexNumberField complexField = new ComplexNumberField(fractalPanel);
		complexField.setText("-0.1 + 0.651i");

		JLabel colorLabel = createLabel("colors: ");

		JSlider colorSlider = createSlider(2, 50, JuliaColorer.DEFAULT_NUMBER_OF_COLORS);
		colorSlider.addChangeListener(e -> fractalPanel.setNumberOfColors(colorSlider.getValue()));

		JLabel gradientLabel = createLabel("gradient: ");

		JSlider gradientSlider = createSlider(1, 100, JuliaColorer.DEFAULT_DISTANCE_BETWEEN_COLORS);
		gradientSlider.addChangeListener(e -> fractalPanel.setDistanceBetweenColors(gradientSlider.getValue()));

		JButton resetColorButton = new JButton("Reset Color");
		resetColorButton.addActionListener(e -> fractalPanel.resetColor());

		JButton resetZoomButton = new JButton("Reset Zoom");
		resetZoomButton.addActionListener(e -> fractalPanel.resetZoom());

		JPanel buttonPanel = createButtonPanel(saveButton, complexLabel, complexField, colorLabel, colorSlider, gradientLabel, gradientSlider,
				resetColorButton, resetZoomButton);

		JPanel glassPanel = new JPanel(new BorderLayout());
		glassPanel.add(buttonPanel, BorderLayout.NORTH);

		JFrame mainFrame = createMainFrame(backgroundPanel);

		JPanel glassPane = (JPanel) mainFrame.getGlassPane();
		glassPane.add(glassPanel);
		glassPane.setVisible(true);

		mainFrame.setVisible(true);
	}

	private static JLabel createLabel(String title) {
		JLabel label = new JLabel(title);
		label.setBackground(Color.BLACK);
		label.setForeground(Color.GREEN);
		return label;
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

	private static JPanel createButtonPanel(Component... buttons) {
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.setBackground(Color.BLACK);
		for (Component button : buttons) {
			buttonPanel.add(button);
		}
		return buttonPanel;
	}

	private static JFrame createMainFrame(JPanel backgroundPanel) {
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
		return mainFrame;
	}
}
