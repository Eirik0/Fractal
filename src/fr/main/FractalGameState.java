package fr.main;

import java.awt.Graphics2D;

import gt.gamestate.GameState;
import gt.gamestate.GameStateManager;
import gt.gamestate.UserInput;

public class FractalGameState implements GameState {
    private final FractalUserInputHandler inputHandler;

    public FractalGameState() {
        inputHandler = new FractalUserInputHandler(GameStateManager.getMouseTracker());
    }

    @Override
    public void update(double dt) {
        FractalManager.updateDrawers();
    }

    @Override
    public void drawOn(Graphics2D graphics) {
        graphics.drawImage(FractalManager.requestImage(), 0, 0, null);
        inputHandler.drawOn(graphics);
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
