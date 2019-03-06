package fr.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.gui.ColorSelectionDialog;
import fr.gui.ComplexNumberField;
import fr.gui.SaveFractalDialog;
import gt.component.ComponentCreator;
import gt.component.GamePanel;
import gt.component.MainFrame;
import gt.gamestate.GameStateManager;

public class FractalMain {
    private static final String TITLE = "Fractals";

    public static final String DEFAULT_FRACTAL_TEXT = "-0.1 + 0.651i";

    public static final int DEFAULT_NUM_BASE_COLORS = 5;
    public static final int DEFAULT_NUM_BETWEEN_COLORS = 50;

    public static final int DEFAULT_NUM_ITERATIONS = 20000;

    public static void main(String[] args) {
        ComponentCreator.setCrossPlatformLookAndFeel();

        // Main Panel
        GamePanel mainPanel = new GamePanel(TITLE);
        mainPanel.setPreferredSize(new Dimension(ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT));

        // Buttons, etc
        JButton saveButton = ComponentCreator.createButton("Save", () -> SaveFractalDialog.show(mainPanel));

        JLabel complexLabel = ComponentCreator.createLabel("a + bi: ", Color.GREEN);

        ComplexNumberField complexField = new ComplexNumberField();
        complexField.setText(DEFAULT_FRACTAL_TEXT);

        JLabel colorLabel = ComponentCreator.createLabel("cols:", Color.GREEN);
        JSlider colorSlider = ComponentCreator.createSlider(2, 50, DEFAULT_NUM_BASE_COLORS,
                value -> FractalManager.setNumberOfColors(value));

        JLabel gradientLabel = ComponentCreator.createLabel("grad:", Color.GREEN);
        JSlider gradientSlider = ComponentCreator.createSlider(1, 100, DEFAULT_NUM_BETWEEN_COLORS,
                value -> FractalManager.setNumberOfBetweenColors(value));

        JLabel iterationsLabel = ComponentCreator.createLabel("iters:", Color.GREEN);
        JSlider iterationsSlider = ComponentCreator.createSlider(10000, 500000, DEFAULT_NUM_ITERATIONS,
                value -> FractalManager.setNumberOfIterations(value));

        JButton resetColorButton = ComponentCreator.createButton("Reset Color", () -> FractalManager.resetColors());

        JButton configureColorButton = ComponentCreator.createButton("C", () -> ColorSelectionDialog.show());

        JButton resetZoomButton = ComponentCreator.createButton("Reset Zoom", () -> FractalManager.setFractalBounds(-2.5, -2.5, 5, 5));

        JPanel buttonPanel = createButtonPanel(
                complexLabel, complexField, Box.createHorizontalStrut(5),
                colorLabel, colorSlider,
                gradientLabel, gradientSlider,
                iterationsLabel, iterationsSlider,
                resetColorButton,
                configureColorButton,
                resetZoomButton, Box.createHorizontalStrut(5),
                saveButton);

        // MainFrame, etc
        GameStateManager.setMainPanel(mainPanel);
        GameStateManager.setGameState(new FractalGameState());

        MainFrame mainFrame = new MainFrame(TITLE, mainPanel);

        JPanel overlayPanel = createButtonOverlay(buttonPanel, mainFrame.getFrame());
        overlayPanel.setVisible(true);

        mainFrame.show();
    }

    private static JPanel createButtonPanel(Component... components) {
        JPanel buttonPanel = ComponentCreator.initComponent(new JPanel(new FlowLayout()));
        for (Component component : components) {
            buttonPanel.add(component);
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
}
