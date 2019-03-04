package fr.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
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

        // Main Panel
        GamePanel mainPanel = new GamePanel(TITLE);
        mainPanel.setPreferredSize(new Dimension(ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT));

        // Buttons, etc
        JButton saveButton = ComponentCreator.createButton("Save", () -> saveFractal(mainPanel));

        JLabel complexLabel = ComponentCreator.createLabel("a + bi: ", Color.GREEN);

        ComplexNumberField complexField = new ComplexNumberField();
        complexField.setText(DEFAULT_FRACTAL_TEXT);

        JLabel colorLabel = ComponentCreator.createLabel("colors: ", Color.GREEN);
        JSlider colorSlider = ComponentCreator.createSlider(2, 50, FractalColorer.DEFAULT_NUM_BASE_COLORS,
                value -> FractalManager.setNumberOfColors(value));

        JLabel gradientLabel = ComponentCreator.createLabel("gradient: ", Color.GREEN);
        JSlider gradientSlider = ComponentCreator.createSlider(1, 100, FractalColorer.DEFAULT_NUM_BETWEEN_COLORS,
                value -> FractalManager.setNumberOfBetweenColors(value));

        JButton resetColorButton = ComponentCreator.createButton("Reset Color", () -> FractalManager.resetColors());

        JButton resetZoomButton = ComponentCreator.createButton("Reset Zoom", () -> FractalManager.setFractalBounds(-2.5, -2.5, 5, 5));

        JPanel buttonPanel = createButtonPanel(
                saveButton, Box.createHorizontalStrut(5),
                complexLabel, complexField, Box.createHorizontalStrut(5),
                colorLabel, colorSlider, Box.createHorizontalStrut(5),
                gradientLabel, gradientSlider, Box.createHorizontalStrut(5),
                resetColorButton, Box.createHorizontalStrut(5),
                resetZoomButton);

        // MainFrame, etc
        GameStateManager.setMainPanel(mainPanel);
        GameStateManager.setGameState(new FractalGameState(GameStateManager.getMouseTracker()));

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
