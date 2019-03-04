package fr.main;

import java.awt.Graphics2D;

import gt.component.MouseTracker;
import gt.gamestate.GameState;
import gt.gamestate.UserInput;

public class FractalGameState implements GameState {
    private final FractalUserInputHandler inputHandler;

    public FractalGameState(MouseTracker mouseTracker) {
        inputHandler = new FractalUserInputHandler(mouseTracker);
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void drawOn(Graphics2D graphics) {
        graphics.drawImage(FractalManager.requestImage(), 0, 0, null);
        inputHandler.drawOn(graphics);
    }

    @Override
    public void setSize(int width, int height) {
        FractalManager.setImageSize(width, height);
        inputHandler.setSize(width, height);
    }

    @Override
    public void handleUserInput(UserInput input) {
        inputHandler.handleUserInput(input);
    }
}
