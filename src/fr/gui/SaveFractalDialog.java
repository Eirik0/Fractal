package fr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import fr.main.FractalManager;
import fr.main.SaveFractalGameState;
import gt.component.ComponentCreator;
import gt.gamestate.GameStateManager;

public class SaveFractalDialog {
    private static final SaveFractalDialog instance = new SaveFractalDialog();

    private JFrame frame;
    private JTextField widthTextField;
    private JTextField heightTextField;

    public SaveFractalDialog() {
        frame = new JFrame("Save Fractal");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    private void rebuild(JComponent parent) {
        // Buttons
        JPanel buttonPanel = ComponentCreator.initComponent(new JPanel(new FlowLayout()));
        buttonPanel.add(ComponentCreator.createButton("Save Current", () -> showSaveFileDialog(parent, SaveFractalDialog::writeFractalToFile)));
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(ComponentCreator.createButton("Choose Resolution", () -> showChooseResolutionDialog(parent)));
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(ComponentCreator.createButton("Close", () -> frame.setVisible(false)));
        // Main panel
        JPanel mainPanel = ComponentCreator.initComponent(new JPanel(new BorderLayout()));
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
        mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
    }

    private void showChooseResolutionDialog(JComponent parent) {
        // Top panel
        widthTextField = createTextField(Integer.toString(FractalManager.getImageWidth()));
        heightTextField = createTextField(Integer.toString(FractalManager.getImageHeight()));
        JPanel topPanel = ComponentCreator.initComponent(new JPanel(new BorderLayout()));
        JPanel inputPanel = ComponentCreator.initComponent(new JPanel(new FlowLayout()));
        inputPanel.add(ComponentCreator.createLabel("width:", Color.GREEN));
        inputPanel.add(widthTextField);
        inputPanel.add(Box.createHorizontalStrut(10));
        inputPanel.add(ComponentCreator.createLabel("height:", Color.GREEN));
        inputPanel.add(heightTextField);
        topPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        topPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        // Buttons
        JPanel buttonPanel = ComponentCreator.initComponent(new JPanel(new FlowLayout()));
        buttonPanel.add(ComponentCreator.createButton("Save", () -> showSaveFileDialog(parent, SaveFractalDialog::setSaveFractalState)));
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(ComponentCreator.createButton("Close", () -> frame.setVisible(false)));
        // Main panel
        JPanel mainPanel = ComponentCreator.initComponent(new JPanel(new BorderLayout()));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
        mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
        mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.setContentPane(mainPanel);
        frame.pack();
    }

    private static JTextField createTextField(String text) {
        JTextField widthTextField = ComponentCreator.initComponent(new JTextField());
        widthTextField.setText(text);
        widthTextField.setColumns(10);
        return widthTextField;
    }

    public static void show(JComponent parent) {
        instance.rebuild(parent);
        instance.frame.pack();
        instance.frame.setLocationRelativeTo(null);
        instance.frame.setVisible(true);
    }

    private static void showSaveFileDialog(JComponent parent, Consumer<File> fileConsumer) {
        JFileChooser jFileChooser = ComponentCreator.initComponent(new JFileChooser(System.getProperty("user.home") + File.separator + "Desktop"));
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("fractal image types", "bmp", "jpg", "png");
        jFileChooser.addChoosableFileFilter(extensionFilter);
        if (jFileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            fileConsumer.accept(jFileChooser.getSelectedFile());
            instance.frame.setVisible(false);
        }
    }

    private static void setSaveFractalState(File file) {
        int imageWidth;
        try {
            imageWidth = Integer.parseInt(instance.widthTextField.getText());
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            return;
        }
        int imageHeight;
        try {
            imageHeight = Integer.parseInt(instance.heightTextField.getText());
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            return;
        }
        GameStateManager.setGameState(new SaveFractalGameState(file, imageWidth, imageHeight));
    }

    public static void writeFractalToFile(File file) {
        try {
            String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            ImageIO.write(FractalManager.requestImage(), extension, file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
