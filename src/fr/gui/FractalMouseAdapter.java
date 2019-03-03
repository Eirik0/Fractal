package fr.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import fr.main.FractalManager;

public class FractalMouseAdapter extends MouseAdapter {
    private boolean isDragging = false;

    private int dragStartX = 0;
    private int dragStartY = 0;
    private int dragEndX = 0;
    private int dragEndY = 0;

    FractalMouseAdapter() {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dragStartX = e.getX();
        dragStartY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        isDragging = true;

        dragEndX = e.getX();
        dragEndY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int br_x = dragStartX > dragEndX ? dragStartX : dragEndX;
        int br_y = dragStartY > dragEndY ? dragStartY : dragEndY;

        FractalManager.zoomTo(getDragUpperLeftX(), getDragUpperLeftY(), br_x, br_y);

        isDragging = false;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        FractalManager.zoom(e.getWheelRotation());
    }

    public boolean isDragging() {
        return isDragging;
    }

    public int getDragUpperLeftY() {
        return dragStartY < dragEndY ? dragStartY : dragEndY;
    }

    public int getDragUpperLeftX() {
        return dragStartX < dragEndX ? dragStartX : dragEndX;
    }

    public int getWidth() {
        return Math.abs(dragEndX - dragStartX);
    }

    public int getHeight() {
        return Math.abs(dragEndY - dragStartY);
    }
}
