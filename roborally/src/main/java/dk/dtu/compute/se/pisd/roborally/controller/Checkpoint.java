package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Checkpoint extends FieldAction {
    private final int number;
    private boolean isLast;

    public Checkpoint(int number, boolean isLast) {
        this.number = number;
        this.isLast = isLast;
    }

    public int getNumber() {
        return number;
    }

    /**
     * Implementation of the action of a checkpoint. Needs to be implemented for A3.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // TODO-DONE A6d: needs to be implemented
        return space.getPlayer().markCheckpoint(number) && isLast;
    }
}
