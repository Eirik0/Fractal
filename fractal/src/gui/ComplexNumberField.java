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
		if (text == null || text.equals("")) {
			return new MandelbrotSet();
		}

		try {
			text.replaceAll("\\s+", "");

			if (text.startsWith("+")) {
				text = text.substring(1);
			}

			if (text.contains("i")) {
				String[] splitOnI = text.split("i");

				if (splitOnI.length == 0) {
					return new JuliaSet(0, 1);
				} else if (splitOnI.length == 1) {
					String splitText = splitOnI[0];
					try {
						return new JuliaSet(0, Double.parseDouble(splitText));
					} catch (NumberFormatException e) {
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
						}
					}
				} else if (splitOnI.length == 2) {
					return new JuliaSet(Double.parseDouble(splitOnI[0]), Double.parseDouble(splitOnI[1]));
				}

				throw new NumberFormatException("Not valid: " + text);
			}

			return new JuliaSet(Double.parseDouble(text), 0);
		} catch (NumberFormatException e) {
			setForeground(Color.RED);
			return new MandelbrotSet();
		}
	}
}
