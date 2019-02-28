package fr.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fr.fractal.Fractal;
import fr.fractal.JuliaSet;
import fr.fractal.MandelbrotSet;

public class ComplexNumberFieldTest {
    private static void checkMandelbrotSet(Fractal fractal) {
        assertEquals(MandelbrotSet.class, fractal.getClass());
    }

    private static void checkJuliaSet(Fractal fractal, double cx, double cy) {
        assertEquals(JuliaSet.class, fractal.getClass());
        assertEquals(cx, ((JuliaSet) fractal).getCx());
        assertEquals(cy, ((JuliaSet) fractal).getCy());
    }

    @Test
    public void testNull() {
        checkMandelbrotSet(ComplexNumberField.textToFractal(null));
    }

    @Test
    public void testBlank() {
        checkMandelbrotSet(ComplexNumberField.textToFractal(""));
    }

    @Test
    public void testPlusOne() {
        checkJuliaSet(ComplexNumberField.textToFractal("+1"), 1, 0);
    }

    @Test
    public void testDouble() {
        checkJuliaSet(ComplexNumberField.textToFractal("-1.23"), -1.23, 0);
    }

    @Test
    public void testImaginarySimple() {
        checkJuliaSet(ComplexNumberField.textToFractal("i"), 0, 1);
    }

    @Test
    public void testImaginaryTimesConstant() {
        checkJuliaSet(ComplexNumberField.textToFractal("-1.23i"), 0, -1.23);
    }

    @Test
    public void testRealPlusImaginary() {
        checkJuliaSet(ComplexNumberField.textToFractal("0.12+1.23i"), 0.12, 1.23);
    }

    @Test
    public void testMinusRealMinusImaginary() {
        checkJuliaSet(ComplexNumberField.textToFractal("-0.12-1.23i"), -0.12, -1.23);
    }

    @Test
    public void testMinusRealMinusI() {
        checkJuliaSet(ComplexNumberField.textToFractal("-0.12-i"), -0.12, -1);
    }

    @Test
    public void testRealPlusSpaceI() {
        checkJuliaSet(ComplexNumberField.textToFractal("1+ i"), 1, 1);
    }

    @Test
    public void testIMinusReal() {
        checkJuliaSet(ComplexNumberField.textToFractal("i-.12"), -.12, 1);
    }

    @Test
    public void testImaginaryPlusReal() {
        checkJuliaSet(ComplexNumberField.textToFractal("1.23i+0.12"), 0.12, 1.23);
    }

    @Test
    public void testMinusI() {
        checkJuliaSet(ComplexNumberField.textToFractal("-i"), 0, -1);
    }

    @Test
    public void testMinusIMinusReal() {
        checkJuliaSet(ComplexNumberField.textToFractal("-i-2"), -2, -1);
    }
}
