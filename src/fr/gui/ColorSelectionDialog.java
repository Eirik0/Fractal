package fr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import fr.main.FractalManager;
import gt.component.ComponentCreator;

public class ColorSelectionDialog {
    private static final ColorSelectionDialog instance = new ColorSelectionDialog();

    private final JFrame frame;

    private ColorSelectionDialog() {
        frame = new JFrame("Select Colors");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        rebuild();
    }

    private void rebuild() {
        Color[] baseColors = FractalManager.getBaseColors();
        // Color List
        DefaultListModel<Color> listModel = new DefaultListModel<>();
        for (Color color : baseColors) {
            listModel.addElement(color);
        }
        JList<Color> colorList = ComponentCreator.initComponent(new JList<>(listModel));
        colorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(colorList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        colorList.setCellRenderer(new ListCellRenderer<Color>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Color> list, Color value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = ComponentCreator.createLabel("Color(r=" + value.getRed() + ", g=" + value.getGreen() + ", b=" + value.getBlue() + ")", value);
                label.setBackground(isSelected ? ComponentCreator.foregroundColor() : ComponentCreator.backgroundColor());
                return label;
            }
        });
        colorList.setSelectedIndex(0);

        // Buttons
        JPanel buttonPanel = ComponentCreator.initComponent(new JPanel(new FlowLayout()));
        buttonPanel.add(ComponentCreator.createButton("Apply", () -> {
            Color[] newBaseColors = new Color[listModel.size()];
            listModel.copyInto(newBaseColors);
            FractalManager.setBaseColors(newBaseColors);
        }));
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(ComponentCreator.createButton("Close", () -> frame.setVisible(false)));

        // Color chooser
        JColorChooser colorChooser = ComponentCreator.initComponent(new JColorChooser(baseColors[0]));
        colorList.addListSelectionListener(e -> colorChooser.setColor(colorList.getSelectedValue()));
        colorChooser.getSelectionModel().addChangeListener(e -> listModel.set(colorList.getSelectedIndex(), colorChooser.getColor()));

        // Main panel
        JPanel mainPanel = ComponentCreator.initComponent(new JPanel(new BorderLayout()));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(colorChooser, BorderLayout.EAST);
        frame.setContentPane(mainPanel);
    }

    public static void show() {
        instance.rebuild();
        instance.frame.pack();
        instance.frame.setVisible(true);
    }
}
