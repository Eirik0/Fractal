package fr.gui;

import java.awt.Color;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.swing.JTextField;

import fr.fractal.BurningShip;
import fr.fractal.BurningShipJuliaSet;
import fr.fractal.Fractal;
import fr.fractal.JuliaSet;
import fr.fractal.MandelbrotSet;
import fr.main.FractalManager;
import gt.component.ComponentCreator;
import gt.gamestate.GameStateManager;

@SuppressWarnings("serial")
public class ComplexNumberField extends JTextField {
    public ComplexNumberField(GameStateManager gameStateManager) {
        ComponentCreator.initComponent(this);
        setColumns(10);
        addActionListener(e -> {
            FractalManager.setFractal(textToFractal(getText()));
            gameStateManager.requestFocus();
        });
    }

    public Fractal getFractal(String text) {
        setForeground(Color.BLACK);

        try {
            return textToFractal(text);
        } catch (Exception e) {
            System.err.println("Error parsing \"" + text + "\": " + e.getMessage());
            setForeground(Color.RED);
            return new MandelbrotSet();
        }
    }

    public static Fractal textToFractal(String text) {
        text = text == null ? "" : text.replaceAll("\\s+", "");
        if (text.toLowerCase().startsWith("b")) {
            return textToFractal(text.substring(1), () -> new BurningShip(), (re, im) -> new BurningShipJuliaSet(re.doubleValue(), im.doubleValue()));
        } else {
            return textToFractal(text, () -> new MandelbrotSet(), (re, im) -> new JuliaSet(re.doubleValue(), im.doubleValue()));
        }
    }

    private static Fractal textToFractal(String text, Supplier<Fractal> zFractal, BiFunction<Double, Double, Fractal> z0Fractal) {
        if (text.equals("")) {
            return zFractal.get();
        }

        boolean firstIsNegative = false;
        if (text.startsWith("+")) {
            text = text.substring(1);
        } else if (text.startsWith("-")) {
            text = text.substring(1);
            firstIsNegative = true;
        }

        boolean containsPlus = checkCount(text, '+');
        boolean containsMinus = checkCount(text, '-');
        boolean containsI = checkCount(text, 'i');

        if (!containsPlus && !containsMinus) {
            if (containsI) {
                return z0Fractal.apply(Double.valueOf(0), Double.valueOf(parseImaginary(text, firstIsNegative)));
            } else {
                return z0Fractal.apply(Double.valueOf(parseReal(text, firstIsNegative)), Double.valueOf(0));
            }
        }

        boolean secondIsNegative = false;
        String[] split;
        if (containsPlus) {
            split = text.split("\\+");
        } else { // containsMinus
            split = text.split("-");
            secondIsNegative = true;
        }

        if (!containsI) {
            throw new NumberFormatException("Missing 'i'");
        }

        if (split[0].length() == 0) {
            throw new NumberFormatException("First is empty");
        } else if (split[1].length() == 0) {
            throw new NumberFormatException("Second is empty");
        }

        if (split[0].contains("i")) {
            return z0Fractal.apply(Double.valueOf(parseReal(split[1], secondIsNegative)), Double.valueOf(parseImaginary(split[0], firstIsNegative)));
        } else {
            return z0Fractal.apply(Double.valueOf(parseReal(split[0], firstIsNegative)), Double.valueOf(parseImaginary(split[1], secondIsNegative)));
        }
    }

    private static boolean checkCount(String text, char ch) {
        long count = text.chars().filter(c -> c == ch).count();
        if (count > 1) {
            throw new NumberFormatException("too many '" + ch + "'s");
        }
        return count == 1;
    }

    private static double parseReal(String text, boolean negate) {
        double d = Double.parseDouble(text);
        return negate ? -d : d;
    }

    private static double parseImaginary(String text, boolean negate) {
        if (text.endsWith("i")) {
            String subText = text.substring(0, text.length() - 1);
            double d = subText.isEmpty() ? 1 : Double.parseDouble(subText);
            return negate ? -d : d;
        } else {
            throw new NumberFormatException("'i' in incorrect position");
        }
    }
}
