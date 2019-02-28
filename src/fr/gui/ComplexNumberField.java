package fr.gui;

import java.awt.Color;

import javax.swing.JTextField;

import fr.fractal.Fractal;
import fr.fractal.JuliaSet;
import fr.fractal.MandelbrotSet;
import fr.julia.JuliaImageDrawerDelegate;

@SuppressWarnings("serial")
public class ComplexNumberField extends JTextField {
    public ComplexNumberField(JuliaImageDrawerDelegate delegate) {
        setColumns(10);
        addActionListener(e -> delegate.setFractal(textToFractal(getText())));
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

        if (text.equals("")) {
            return new MandelbrotSet();
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
                return new JuliaSet(0, parseImaginary(text, firstIsNegative));
            } else {
                return new JuliaSet(parseReal(text, firstIsNegative), 0);
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
            return new JuliaSet(parseReal(split[1], secondIsNegative), parseImaginary(split[0], firstIsNegative));
        } else {
            return new JuliaSet(parseReal(split[0], firstIsNegative), parseImaginary(split[1], secondIsNegative));
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
