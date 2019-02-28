package gui;

import java.awt.Color;

import javax.swing.JTextField;

import julia.Fractals.Fractal;
import julia.Fractals.JuliaSet;
import julia.Fractals.MandelbrotSet;
import julia.*;

public class ComplexNumberField extends JTextField {
	public ComplexNumberField(JuliaImageDrawerDelegate delegate) {
		setColumns(10);
		addActionListener(e -> delegate.setFractal(textToFractal(getText())));
	}

	public Fractal textToFractal(String text) {
		setForeground(Color.BLACK);
		if (text == null) {
			return new MandelbrotSet();
		}

		text = text.replaceAll("\\s+", "");
		if (text.equals("")) {
			return new MandelbrotSet();
		}
		try {
			if (text.startsWith("+")) {
				text = text.substring(1);
			} else if (text.chars().filter(c -> c == 'i').count() > 1) {
				throw new NumberFormatException("too many 'i's");
			} else if (!text.contains("i")) {
				return new JuliaSet(Double.parseDouble(text), 0);
			} else if (text.startsWith("i")) {
				return new JuliaSet(text.length() == 1 ? 0 : Double.parseDouble(text.substring(1)), 1);
			}

			String[] splitOnI = text.split("i");
			if (splitOnI.length == 1) {
				String splitText = splitOnI[0];
				if (splitText.equals("-")) {
					return new JuliaSet(0, -1);
				} else if (splitText.matches("-?\\d+(\\.\\d+)?")) {
					return new JuliaSet(0, Double.parseDouble(splitText));
				} else {
					return parseComplex(splitText);
				}
			} else if (splitOnI.length == 2) {
				return new JuliaSet(Double.parseDouble(splitOnI[1]), splitOnI[0].equals("-") ? -1 : Double.parseDouble(splitOnI[0]));
			} else {
				throw new NumberFormatException("Not valid: " + text);
			}
		} catch (NumberFormatException e) {
			setForeground(Color.RED);
			return new MandelbrotSet();
		}
	}

	private JuliaSet parseComplex(String splitText) {
		int realSign = 1;
		if (splitText.startsWith("-")) {
			realSign = -1;
			splitText = splitText.substring(1);
		}

		if (splitText.endsWith("-")) {
			return new JuliaSet(realSign * Double.parseDouble(splitText.substring(0, splitText.length() - 1)), -1);
		} else if (splitText.endsWith("+")) {
			return new JuliaSet(realSign * Double.parseDouble(splitText.substring(0, splitText.length() - 1)), 1);
		} else if (splitText.contains("+")) {
			String[] nextSplit = splitText.split("[+]");
			return new JuliaSet(realSign * Double.parseDouble(nextSplit[0]), Double.parseDouble(nextSplit[1]));
		} else if (splitText.contains("-")) {
			String[] nextSplit = splitText.split("-");
			return new JuliaSet(realSign * Double.parseDouble(nextSplit[0]), -Double.parseDouble(nextSplit[1]));
		} else {
			throw new NumberFormatException("Not valid: " + splitText);
		}
	}
}
