package fr.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

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

    private static final List<Component> buttonsAndSliders = new ArrayList<>();

    public static void main(String[] args) {
        // Main Panel
        MainFrame mainFrame = new MainFrame(TITLE);
        GamePanel mainPanel = mainFrame.getGamePanel();

        GameStateManager gameStateManager = mainPanel.getGameStateManager();
        FractalManager.setImageDrawer(gameStateManager.getImageDrawer());

        // Buttons, etc
        JButton saveButton = ComponentCreator.createButton("Save", () -> SaveFractalDialog.show(mainPanel));

        JLabel complexLabel = ComponentCreator.createLabel("a + bi: ", Color.GREEN);

        ComplexNumberField complexField = new ComplexNumberField(gameStateManager);
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
        gameStateManager.setGameState(new FractalGameState(gameStateManager.getImageDrawer(), gameStateManager.getMouseTracker()));

        JPanel glassPane = (JPanel) mainFrame.getFrame().getGlassPane();
        JPanel glassPanel = new JPanel(new BorderLayout());
        glassPanel.add(buttonPanel, BorderLayout.NORTH);
        glassPane.add(glassPanel);
        glassPane.setVisible(true);

        mainFrame.show();
    }

    private static JPanel createButtonPanel(Component... components) {
        JPanel buttonPanel = ComponentCreator.initComponent(new JPanel(new FlowLayout()));
        for (Component component : components) {
            buttonPanel.add(component);
            if (component instanceof JButton || component instanceof JSlider || component instanceof JTextField) {
                buttonsAndSliders.add(component);
            }
        }
        return buttonPanel;
    }

    public static void enableButtonsAndSliders(boolean enable) {
        for (Component component : buttonsAndSliders) {
            component.setEnabled(enable);
        }
    }
}
