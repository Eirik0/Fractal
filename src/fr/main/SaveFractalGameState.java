package fr.main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;

import fr.gui.SaveFractalDialog;
import gt.component.ComponentCreator;
import gt.gamestate.GameState;
import gt.gamestate.GameStateManager;
import gt.gamestate.UserInput;

public class SaveFractalGameState implements GameState {
    private final File file;

    private int width = ComponentCreator.DEFAULT_WIDTH;
    private int height = ComponentCreator.DEFAULT_WIDTH;

    public SaveFractalGameState(File file, int imageWidth, int imageHeight) {
        this.file = file;
        FractalManager.setImageSize(imageWidth, imageHeight, true);
    }

    @Override
    public void update(double dt) {
        FractalManager.updateDrawers();
        if (FractalManager.isDrawingComplete()) {
            SaveFractalDialog.writeFractalToFile(file);
            FractalMain.enableButtonsAndSliders(true);
            GameStateManager.setGameState(new FractalGameState());
        }
    }

    @Override
    public void drawOn(Graphics2D graphics) {
        graphics.drawImage(FractalManager.requestImage().getScaledInstance(width, height, Image.SCALE_FAST), 0, 0, null);
    }

    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void handleUserInput(UserInput input) {
    }
}
