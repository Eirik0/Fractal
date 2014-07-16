package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FractalMain {
	public static void main(String[] args) {
		FractalPanel fractalPanel = new FractalPanel();

		JLabel divisorLabel = new JLabel("a + bi (a, b): ");
		divisorLabel.setBackground(Color.BLACK);
		divisorLabel.setForeground(Color.GREEN);

		JTextField complexNumberField = new JTextField();
		complexNumberField.setText("-0.1, 0.651");
		complexNumberField.setColumns(10);
		complexNumberField.addActionListener(e -> {
			try {
				String[] z = complexNumberField.getText().split("[,+]");
				double x = Double.valueOf(z[0].trim());
				double y = 0;
				if (z.length > 1) {
					y = Double.valueOf(z[1].trim());
				}
				fractalPanel.setJulia(x, y);
			} catch (Exception ex) {
			}
		});

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
}
