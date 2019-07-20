package fr.main;

import gt.component.MouseTracker;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gamestate.GameState;
import gt.gamestate.UserInput;

public class FractalGameState implements GameState {
    private final IGameImageDrawer imageDrawer;
    private final FractalUserInputHandler inputHandler;

    public FractalGameState(IGameImageDrawer imageDrawer, MouseTracker mouseTracker) {
        this.imageDrawer = imageDrawer;
        inputHandler = new FractalUserInputHandler(mouseTracker);
    }

    @Override
    public void update(double dt) {
        FractalManager.updateDrawers();
    }

    @Override
    public void drawOn(IGraphics g) {
        imageDrawer.drawImage(g, FractalManager.requestImage(), 0, 0);
        inputHandler.drawOn(g);
    }

    @Override
    public void setSize(int width, int height) {
        FractalManager.setImageSize(width, height, false);
        inputHandler.setSize(width, height);
    }

    @Override
    public void handleUserInput(UserInput input) {
        inputHandler.handleUserInput(input);
    }
}
