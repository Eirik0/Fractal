package fr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.fractal.Fractal;
import fr.fractal.JuliaSet;
import fr.julia.JuliaColorer;
import fr.julia.JuliaImageDrawerDelegate;
import fr.julia.JuliaSizer;

public class FractalMain {
    private static final Fractal DEFAULT_FRACTAL = new JuliaSet(-0.1, 0.651);
    private static final String DEFAULT_TEXT = "-0.1 + 0.651i";

    private static final int DEFAULT_WIDTH = 1000;
    private static final int DEFAULT_HEIGHT = DEFAULT_WIDTH * 9 / 16;

    public static void main(String[] args) {
        JuliaImageDrawerDelegate delegate = new JuliaImageDrawerDelegate(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_FRACTAL);

        FractalPanel fractalPanel = new FractalPanel(delegate);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> fractalPanel.save());

        JLabel complexLabel = createLabel("a + bi: ");

        ComplexNumberField complexField = new ComplexNumberField(delegate);
        complexField.setText(DEFAULT_TEXT);

        JLabel colorLabel = createLabel("colors: ");

        JSlider colorSlider = createSlider(2, 50, JuliaColorer.DEFAULT_NUMBER_OF_COLORS);
        colorSlider.addChangeListener(e -> {
            JuliaColorer.setNumberOfColors(colorSlider.getValue());
            delegate.requestReset();
        });

        JLabel gradientLabel = createLabel("gradient: ");

        JSlider gradientSlider = createSlider(1, 100, JuliaColorer.DEFAULT_DISTANCE_BETWEEN_COLORS);
        gradientSlider.addChangeListener(e -> {
            JuliaColorer.setDistanceBetweenColors(gradientSlider.getValue());
            delegate.requestReset();
        });

        JButton resetColorButton = new JButton("Reset Color");
        resetColorButton.addActionListener(e -> {
            JuliaColorer.resetColors();
            delegate.requestReset();
        });

        JButton resetZoomButton = new JButton("Reset Zoom");
        resetZoomButton.addActionListener(e -> {
            JuliaSizer.setJuliaBounds(-2.5, -2.5, 5, 5);
            delegate.requestReset();
        });

        JPanel buttonPanel = createButtonPanel(saveButton, complexLabel, complexField, colorLabel, colorSlider, gradientLabel, gradientSlider,
                resetColorButton, resetZoomButton);

        JFrame mainFrame = createMainFrame();
        mainFrame.add(fractalPanel);

        JPanel overlayPanel = createButtonOverlay(buttonPanel, mainFrame);
        overlayPanel.setVisible(true);

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

    private static JFrame createMainFrame() {
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("Fractals");
        mainFrame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        return mainFrame;
    }

    private static JPanel createButtonOverlay(JPanel buttonPanel, JFrame mainFrame) {
        JPanel glassPane = (JPanel) mainFrame.getGlassPane();
        JPanel glassPanel = new JPanel(new BorderLayout());
        glassPanel.add(buttonPanel, BorderLayout.NORTH);
        glassPane.add(glassPanel);
        return glassPane;
    }
}
