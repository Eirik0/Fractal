package fr.main;

import java.io.File;

import fr.gui.SaveFractalDialog;
import gt.component.ComponentCreator;
import gt.gameentity.IGraphics;
import gt.gamestate.GameState;
import gt.gamestate.GameStateManager;
import gt.gamestate.UserInput;

public class SaveFractalGameState implements GameState {
    private final GameStateManager gameStateManager;

    private final File file;

    private int width = ComponentCreator.DEFAULT_WIDTH;
    private int height = ComponentCreator.DEFAULT_WIDTH;

    public SaveFractalGameState(GameStateManager gameStateManager, File file, int imageWidth, int imageHeight) {
        this.gameStateManager = gameStateManager;
        this.file = file;
        FractalManager.setImageSize(imageWidth, imageHeight, true);
    }

    @Override
    public void update(double dt) {
        FractalManager.updateDrawers();
        if (FractalManager.isDrawingComplete()) {
            SaveFractalDialog.writeFractalToFile(file);
            FractalMain.enableButtonsAndSliders(true);
            gameStateManager.setGameState(new FractalGameState(gameStateManager.getImageDrawer(), gameStateManager.getMouseTracker()));
        }
    }

    @Override
    public void drawOn(IGraphics g) {
        gameStateManager.getImageDrawer().drawImage(g, FractalManager.requestImage(), 0, 0, width, height);
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
