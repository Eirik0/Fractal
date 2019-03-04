package fr.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.draw.FractalColorer;
import fr.gui.ComplexNumberField;
import gt.component.ComponentCreator;
import gt.component.GamePanel;
import gt.component.MainFrame;
import gt.gamestate.GameStateManager;

public class FractalMain {
    private static final String TITLE = "Fractals";

    public static final String DEFAULT_FRACTAL_TEXT = "-0.1 + 0.651i";

    public static void main(String[] args) {
        ComponentCreator.setCrossPlatformLookAndFeel();

        // Buttons, etc
        JButton saveButton = new JButton("Save");
        saveButton.setFocusable(false);

        JLabel complexLabel = createLabel("a + bi: ");

        ComplexNumberField complexField = new ComplexNumberField();
        complexField.setText(DEFAULT_FRACTAL_TEXT);

        JLabel colorLabel = createLabel("colors: ");

        JSlider colorSlider = createSlider(2, 50, FractalColorer.DEFAULT_NUM_BASE_COLORS);
        colorSlider.addChangeListener(e -> FractalManager.setNumberOfColors(colorSlider.getValue()));

        JLabel gradientLabel = createLabel("gradient: ");

        JSlider gradientSlider = createSlider(1, 100, FractalColorer.DEFAULT_NUM_BETWEEN_COLORS);
        gradientSlider.addChangeListener(e -> FractalManager.setNumberOfBetweenColors(gradientSlider.getValue()));

        JButton resetColorButton = new JButton("Reset Color");
        resetColorButton.setFocusable(false);
        resetColorButton.addActionListener(e -> FractalManager.resetColors());

        JButton resetZoomButton = new JButton("Reset Zoom");
        resetZoomButton.setFocusable(false);
        resetZoomButton.addActionListener(e -> FractalManager.setFractalBounds(-2.5, -2.5, 5, 5));

        JPanel buttonPanel = createButtonPanel(saveButton, complexLabel, complexField, colorLabel, colorSlider, gradientLabel, gradientSlider,
                resetColorButton, resetZoomButton);

        // MainFrame, etc
        GamePanel mainPanel = new GamePanel("Pascal");
        mainPanel.setPreferredSize(new Dimension(ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT));

        saveButton.addActionListener(e -> saveFractal(mainPanel));

        GameStateManager.setMainPanel(mainPanel);
        GameStateManager.setGameState(new FractalGameState(GameStateManager.getMouseTracker()));

        MainFrame mainFrame = new MainFrame(TITLE, mainPanel);

        JPanel overlayPanel = createButtonOverlay(buttonPanel, mainFrame.getFrame());
        overlayPanel.setVisible(true);

        mainFrame.show();
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
        slider.setFocusable(false);
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

    private static JPanel createButtonOverlay(JPanel buttonPanel, JFrame mainFrame) {
        JPanel glassPane = (JPanel) mainFrame.getGlassPane();
        JPanel glassPanel = new JPanel(new BorderLayout());
        glassPanel.add(buttonPanel, BorderLayout.NORTH);
        glassPane.add(glassPanel);
        return glassPane;
    }

    public static void saveFractal(JPanel parent) {
        JFileChooser jFileChooser = new JFileChooser(System.getProperty("user.home") + File.separator + "Desktop");
        if (jFileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            try {
                ImageIO.write(FractalManager.requestImage(), "bmp", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
