/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // TODO-DONE A6a: this method should be implemented for Assignment 6a:
        //   - the current player should be moved to the given space
        //     (if it is free())
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if and when the player is moved (the counter and the status line
        //     message needs to be implemented at another place)
        if (space.getPlayer() != null) return;
        this.board.incrementMoveCounter();

        var currentPlayer = this.board.getCurrentPlayer();
        space.setPlayer(currentPlayer);

        var i = this.board.getPlayerNumber(currentPlayer);
        var max = this.board.getPlayersNumber();

        if (++i >= max) {
            i = 0;
        }

        var newPlayer = this.board.getPlayer(i);
        this.board.setCurrentPlayer(newPlayer);

    }

    // XXX A6c
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX A6c
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX A6c
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX A6c
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX A6c
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX A6c
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX A6c
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX A6c
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX A6c
    // TODO A6d: add the execution of the field actions at the right
    //      place in this method
    // TODO A6e: implement the execution af an interactive card to
    //     this method (e.g. by switching to the PLAYER_INTERACTION phase
    //     at the right point)
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX A6c
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                // TODO-DONE A6c: add the cases for the new commands BACK and UTURN to
                //     this case statement.
                case BACK:
                    this.moveBack(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                default:
                    // DO NOTHING (for now)//
            }
        }
    }


    /**
     * @param player
     * @param heading
     * @return true if move was possible!
     */
    private boolean moveDir(@NotNull Player player, @NotNull Heading heading) {
        var n = board.getNeighbour(player.getSpace(), heading);
        if (n == null) return false;


        // if newSpace has a player already, we need to try and push the player first!
        var nextPlayer = n.getPlayer();
        if (nextPlayer != null) {
            var pushResult = pushPlayer(nextPlayer, heading);
            if (!pushResult) {
                return false;
            }
        }


        player.setSpace(n);

        // hvis vi lander på en plads, hvor der findes et conveyorbelt, så skal vi fortsætte i den retning, hvis muligt.
        var actions = n.getActions();
        for (var action : actions) {
            if (!(action instanceof ConveyorBelt)) continue;

            var newHeading = ((ConveyorBelt) action).getHeading();
            moveDir(player, newHeading);
        }

        return true;
    }

    private boolean pushPlayer(@NotNull Player player, @NotNull Heading heading) {
        return moveDir(player, heading);
    }

    // TODO-DONE A6c: implement this method
    public void moveForward(@NotNull Player player) {
        moveDir(player, player.getHeading());
    }

    // TODO A6c: implement this method
    public void fastForward(@NotNull Player player) {

    }

    // TODO-DONE A6c: implement this method
    public void turnRight(@NotNull Player player) {
        var newDir = player.getHeading().next();
        moveDir(player, newDir);
        player.setHeading(newDir);
    }

    // TODO-DONE A6c: implement this method
    public void turnLeft(@NotNull Player player) {
        var newDir = player.getHeading().prev();
        moveDir(player, newDir);
        player.setHeading(newDir);
    }

    // TODO-DONE A6c: Add two methods for the new commands BACK and UTURN here.
    public void uTurn(@NotNull Player player) {
        var newHeading = player.getHeading().reverse();
        moveDir(player, newHeading);
        player.setHeading(newHeading);

    }

    public void moveBack(@NotNull Player player) {
        var originalHeading = player.getHeading();
        var newHeading = originalHeading.reverse();
        moveDir(player, newHeading);
    }

    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
