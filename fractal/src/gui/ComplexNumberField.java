package gui;

import gui.FractalMain.Fractal;
import gui.FractalMain.JuliaSet;
import gui.FractalMain.MandelbrotSet;

import javax.swing.JTextField;

public class ComplexNumberField extends JTextField {
	FractalPanel fractalPanel;

	public ComplexNumberField(FractalPanel fractalPanel) {
		this.fractalPanel = fractalPanel;
		setColumns(10);
		addActionListener(e -> fractalPanel.setFractal(textToFractal(getText())));
	}

	public static Fractal textToFractal(String text) {
		if (text == null || text.equals("")) {
			return new MandelbrotSet();
		}
		try {
			if (text.contains("i")) {
				String[] split = text.split("i");
				if (split.length == 0) {
					return new JuliaSet(0, 1);
				} else if (split.length > 2) {
					throw new NumberFormatException("Too many 'i's");
				} else if (split.length == 2) {
					double real = Double.parseDouble(split[0]);
					double imaginary = Double.parseDouble(split[1]);
					return new JuliaSet(real, imaginary);
				}
				// String splitText = split[0];
				throw new NumberFormatException("Not yet implemented or invalid");
			}

			double real = Double.parseDouble(text);
			JuliaSet julia = new JuliaSet(real, 0);
			return julia;
		} catch (NumberFormatException e) {
			return new MandelbrotSet();
		}
	}
}
