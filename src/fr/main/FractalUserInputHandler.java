package fr.main;

import java.awt.Color;
import java.awt.Graphics2D;

import gt.component.MouseTracker;
import gt.gameentity.Drawable;
import gt.gameentity.Sizable;
import gt.gamestate.UserInput;

public class FractalUserInputHandler implements Drawable, Sizable {
    private final MouseTracker mouseTracker;

    private boolean leftButtonPressed = false;
    private boolean ctrlPressed = false;

    private int mouseX = 0;
    private int mouseY = 0;
    private double mouseWheelRotation = 0;

    double screenWidth = 0;
    double screenHeight = 0;

    private boolean isDragging = false;
    private int dragStartX = 0;
    private int dragStartY = 0;

    public FractalUserInputHandler(MouseTracker mouseTracker) {
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
    public void drawOn(Graphics2D graphics) {
        if (isDragging) {
            drawZoomPrediction(graphics);
            graphics.setColor(Color.RED);
            graphics.drawRect(getDragUpperLeftX(), getDragUpperLeftY(), getDragWidth(), getDragHeight());
        }
    }

    private void drawZoomPrediction(Graphics2D g) {
        g.setColor(Color.GREEN);

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

        drawRect(g, x0, y0, zoomWidth, zoomHeight);
    }

    @Override
    public void setSize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
    }

    public void handleUserInput(UserInput input) {
        switch (input) {
        case MOUSE_MOVED:
            if (leftButtonPressed && ctrlPressed) {
                FractalManager.move(mouseX - mouseTracker.mouseX, mouseY - mouseTracker.mouseY);
            }
            mouseX = mouseTracker.mouseX;
            mouseY = mouseTracker.mouseY;
            break;
        case MOUSE_WHEEL_MOVED:
            double wheelDelta = mouseWheelRotation - mouseTracker.wheelRotation;
            FractalManager.zoom(0.1 * wheelDelta, mouseX, mouseY);
            mouseWheelRotation = mouseTracker.wheelRotation;
            break;
        case LEFT_BUTTON_PRESSED:
            leftButtonPressed = true;
            if (!ctrlPressed) {
                isDragging = true;
                dragStartX = mouseTracker.mouseX;
                dragStartY = mouseTracker.mouseY;
            }
            break;
        case LEFT_BUTTON_RELEASED:
            if (isDragging) {
                isDragging = false;

                int br_x = dragStartX > mouseX ? dragStartX : mouseX;
                int br_y = dragStartY > mouseY ? dragStartY : mouseY;

                FractalManager.zoomTo(getDragUpperLeftX(), getDragUpperLeftY(), br_x, br_y);
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
