package fr.main;

import java.awt.Color;

import gt.component.IMouseTracker;
import gt.gameentity.Drawable;
import gt.gameentity.IGraphics;
import gt.gameentity.Sizable;
import gt.gamestate.UserInput;

public class FractalUserInputHandler implements Drawable, Sizable {
    private final IMouseTracker mouseTracker;

    private boolean leftButtonPressed = false;
    private boolean ctrlPressed = false;

    private int mouseX = 0;
    private int mouseY = 0;

    private double screenWidth = 0;
    private double screenHeight = 0;

    private boolean isZoomDragging = false;
    private int dragStartX = 0;
    private int dragStartY = 0;

    public FractalUserInputHandler(IMouseTracker mouseTracker) {
        this.mouseTracker = mouseTracker;
    }

    public int getDragUpperLeftX() {
        return dragStartX < mouseX ? dragStartX : mouseX;
    }

    public int getDragUpperLeftY() {
        return dragStartY < mouseY ? dragStartY : mouseY;
    }

    public int getDragWidth() {
        return Math.abs(mouseX - dragStartX);
    }

    public int getDragHeight() {
        return Math.abs(mouseY - dragStartY);
    }

    @Override
    public void drawOn(IGraphics g) {
        if (isZoomDragging) {
            drawZoomPrediction(g);
            g.drawRect(getDragUpperLeftX(), getDragUpperLeftY(), getDragWidth(), getDragHeight(), Color.RED);
        }
    }

    private void drawZoomPrediction(IGraphics g) {
        double x0 = getDragUpperLeftX();
        double y0 = getDragUpperLeftY();

        double zoomWidth = getDragWidth();
        double zoomHeight = getDragHeight();

        double screenAspectRatio = screenWidth / screenHeight;
        double zoomAspectRatio = zoomWidth / zoomHeight;

        if (screenAspectRatio > zoomAspectRatio) {
            zoomWidth *= (screenAspectRatio / zoomAspectRatio);
            x0 -= (zoomWidth - getDragWidth()) / 2;
        } else {
            zoomHeight *= (zoomAspectRatio / screenAspectRatio);
            y0 -= (zoomHeight - getDragHeight()) / 2;
        }

        g.drawRect(x0, y0, zoomWidth, zoomHeight, Color.GREEN);
    }

    @Override
    public void setSize(double width, double height) {
        screenWidth = width;
        screenHeight = height;
    }

    public void handleUserInput(UserInput input) {
        switch (input) {
        case MOUSE_MOVED:
            if (leftButtonPressed && ctrlPressed && !isZoomDragging) {
                FractalManager.move(mouseX - mouseTracker.mouseX(), mouseY - mouseTracker.mouseY());
            }
            mouseX = mouseTracker.mouseX();
            mouseY = mouseTracker.mouseY();
            break;
        case MOUSE_WHEEL_MOVED:
            FractalManager.zoom(-0.1 * mouseTracker.wheelRotationDelta(), mouseX, mouseY);
            break;
        case LEFT_BUTTON_PRESSED:
            leftButtonPressed = true;
            if (!ctrlPressed) {
                isZoomDragging = true;
                dragStartX = mouseTracker.mouseX();
                dragStartY = mouseTracker.mouseY();
            }
            break;
        case LEFT_BUTTON_RELEASED:
            if (isZoomDragging) {
                isZoomDragging = false;
                int br_x = dragStartX > mouseX ? dragStartX : mouseX;
                int br_y = dragStartY > mouseY ? dragStartY : mouseY;
                int ul_x = getDragUpperLeftX();
                int ul_y = getDragUpperLeftY();
                if (br_x - ul_x > 3 || br_y - ul_y > 3) {
                    FractalManager.zoomTo(ul_x, ul_y, br_x, br_y);
                }
            }
            leftButtonPressed = false;
            break;
        case CTRL_KEY_PRESSED:
            ctrlPressed = true;
            break;
        case CTRL_KEY_RELEASED:
            ctrlPressed = false;
            break;
        }
    }
}
